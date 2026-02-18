package com.interview.platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Service for S3 object storage operations using AWS SDK v2.
 *
 * <h3>P1 Changes from SDK v1:</h3>
 * <ul>
 *   <li>Migrated from {@code AmazonS3} (SDK v1) to {@code S3Client} (SDK v2).</li>
 *   <li>Upload methods now return <strong>S3 object keys</strong> instead of
 *       presigned GET URLs. This eliminates the expiry problem where URLs stored
 *       in the database would become invalid after 7 days.</li>
 *   <li>Presigned GET and PUT URL generation is now handled via {@code S3Presigner}
 *       with configurable durations.</li>
 *   <li>Logging migrated from {@code java.util.logging} to SLF4J.</li>
 * </ul>
 *
 * <h3>Key Storage Strategy:</h3>
 * <p>All entity fields that previously stored presigned URLs ({@code avatarVideoUrl},
 * {@code videoUrl}, {@code fileUrl}) now store S3 object keys. Presigned GET URLs
 * are generated on-demand when building DTOs for the frontend. This means stored
 * references never expire.</p>
 *
 * <h3>Presigned PUT Flow (P1 — direct-to-S3 uploads):</h3>
 * <ol>
 *   <li>Frontend requests a presigned PUT URL via {@code /api/interviews/{id}/upload-url}</li>
 *   <li>This service generates the presigned PUT URL and returns it with the S3 key</li>
 *   <li>Frontend uploads directly to S3 using HTTP PUT</li>
 *   <li>Frontend calls {@code /confirm-upload} — this service verifies the object exists</li>
 * </ol>
 */
@Service
public class VideoStorageService {

    private static final Logger log = LoggerFactory.getLogger(VideoStorageService.class);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    /**
     * Duration for presigned GET URLs (video/audio playback, resume download).
     * Default: 1 hour. Configurable via {@code aws.s3.presigned.get.duration-minutes}.
     */
    @Value("${aws.s3.presigned.get.duration-minutes:60}")
    private int presignedGetDurationMinutes;

    /**
     * Duration for presigned PUT URLs (frontend direct upload).
     * Default: 15 minutes. Configurable via {@code aws.s3.presigned.put.duration-minutes}.
     */
    @Value("${aws.s3.presigned.put.duration-minutes:15}")
    private int presignedPutDurationMinutes;

    public VideoStorageService(S3Client s3Client,
                               S3Presigner s3Presigner,
                               @Qualifier("s3BucketName") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    // ════════════════════════════════════════════════════════════════
    // Server-side uploads (used by resume upload, TTS audio, avatar)
    // ════════════════════════════════════════════════════════════════

    /**
     * Upload a video response to S3 via the server (legacy path).
     *
     * <p>This method is retained for backward compatibility but the preferred
     * path for user video uploads is presigned PUT (see {@link #generatePresignedPutUrl}).
     * It is still used internally when the server needs to upload content
     * (e.g., avatar videos downloaded from D-ID).</p>
     *
     * @param file        the video file
     * @param userId      owner user ID (used in S3 key path)
     * @param interviewId interview ID (used in S3 key path)
     * @param questionId  question ID (used in S3 key path)
     * @return the S3 object key (NOT a presigned URL)
     */
    public String uploadVideo(MultipartFile file, Long userId, Long interviewId, Long questionId) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String key = String.format("interviews/%d/%d/response_%d_%s.webm",
                userId, interviewId, questionId, timestamp);

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .metadata(Map.of(
                            "user-id", String.valueOf(userId),
                            "interview-id", String.valueOf(interviewId),
                            "question-id", String.valueOf(questionId)
                    ))
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("Uploaded video to S3: key={}, user={}", key, userId);

            return key;
        } catch (IOException e) {
            log.error("Failed to read video file for upload: user={}, interview={}, question={}",
                    userId, interviewId, questionId, e);
            throw new RuntimeException("Failed to upload video: " + e.getMessage(), e);
        } catch (S3Exception e) {
            log.error("S3 error uploading video: key={}, statusCode={}", key, e.statusCode(), e);
            throw new RuntimeException("Failed to upload video to S3: " + e.getMessage(), e);
        }
    }

    /**
     * Upload a resume file to S3.
     *
     * @param file   the resume file (PDF or DOCX)
     * @param userId owner user ID
     * @return the S3 object key (NOT a presigned URL)
     */
    public String uploadResumeFile(MultipartFile file, Long userId) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".pdf";
        String key = String.format("resumes/%d/resume_%s%s", userId, timestamp, extension);

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .metadata(Map.of(
                            "user-id", String.valueOf(userId),
                            "original-filename", originalFilename != null ? originalFilename : "resume"
                    ))
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("Uploaded resume to S3: key={}, user={}", key, userId);

            return key;
        } catch (IOException e) {
            log.error("Failed to read resume file for upload: user={}", userId, e);
            throw new RuntimeException("Failed to upload resume: " + e.getMessage(), e);
        } catch (S3Exception e) {
            log.error("S3 error uploading resume: key={}, statusCode={}", key, e.statusCode(), e);
            throw new RuntimeException("Failed to upload resume to S3: " + e.getMessage(), e);
        }
    }

    /**
     * Upload raw bytes to S3 (used by TTS audio and avatar video pipeline).
     *
     * @param data        the file bytes
     * @param key         the S3 object key
     * @param contentType the MIME type (e.g., "audio/mpeg", "video/mp4")
     * @return the S3 object key (same as input — returned for API consistency)
     */
    public String uploadBytes(byte[] data, String key, String contentType) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) data.length)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(data));
            log.info("Uploaded bytes to S3: key={}, size={}, type={}", key, data.length, contentType);

            return key;
        } catch (S3Exception e) {
            log.error("S3 error uploading bytes: key={}, statusCode={}", key, e.statusCode(), e);
            throw new RuntimeException("Failed to upload to S3: " + e.getMessage(), e);
        }
    }

    /**
     * @deprecated Use {@link #uploadBytes(byte[], String, String)} instead.
     *             This method exists only for backward compatibility during migration.
     *             It delegates to {@code uploadBytes} and returns the S3 key.
     */
    @Deprecated(forRemoval = true, since = "P1")
    public String uploadAudioBytes(byte[] audioData, String key, String contentType) {
        return uploadBytes(audioData, key, contentType);
    }

    // ════════════════════════════════════════════════════════════════
    // Presigned URL Generation
    // ════════════════════════════════════════════════════════════════

    /**
     * Generate a presigned GET URL for accessing an S3 object.
     *
     * <p>Used when building DTOs for the frontend — generates a short-lived
     * URL for video playback, audio playback, or resume download.</p>
     *
     * @param s3Key the S3 object key stored in the database
     * @return a presigned GET URL valid for {@code presignedGetDurationMinutes}
     * @throws IllegalArgumentException if the key is null or blank
     */
    public String generatePresignedGetUrl(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            throw new IllegalArgumentException("S3 key must not be null or blank");
        }

        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(presignedGetDurationMinutes))
                    .getObjectRequest(getRequest)
                    .build();

            PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
            log.debug("Generated presigned GET URL for key={}, expires in {} min", s3Key, presignedGetDurationMinutes);
            return presigned.url().toString();
        } catch (S3Exception e) {
            log.error("Failed to generate presigned GET URL for key={}", s3Key, e);
            throw new RuntimeException("Failed to generate presigned GET URL: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a presigned GET URL with a custom duration.
     *
     * <p>Used when a specific expiry time is needed, e.g., when providing a URL
     * to AssemblyAI for transcription (needs to be valid long enough for processing).</p>
     *
     * @param s3Key          the S3 object key
     * @param durationMinutes custom validity duration in minutes
     * @return a presigned GET URL valid for the specified duration
     */
    public String generatePresignedGetUrl(String s3Key, int durationMinutes) {
        if (s3Key == null || s3Key.isBlank()) {
            throw new IllegalArgumentException("S3 key must not be null or blank");
        }

        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(durationMinutes))
                    .getObjectRequest(getRequest)
                    .build();

            PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
            log.debug("Generated presigned GET URL for key={}, expires in {} min", s3Key, durationMinutes);
            return presigned.url().toString();
        } catch (S3Exception e) {
            log.error("Failed to generate presigned GET URL for key={}", s3Key, e);
            throw new RuntimeException("Failed to generate presigned GET URL: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a presigned PUT URL for direct-to-S3 uploads from the frontend.
     *
     * <p>The frontend uses this URL to upload video response files directly to S3
     * via HTTP PUT, bypassing the backend server entirely. This eliminates the
     * bandwidth and memory bottleneck of proxying large video files through
     * the application server.</p>
     *
     * @param s3Key       the S3 object key where the file will be stored
     * @param contentType expected MIME type of the upload (e.g., "video/webm")
     * @return a presigned PUT URL valid for {@code presignedPutDurationMinutes}
     */
    public String generatePresignedPutUrl(String s3Key, String contentType) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(presignedPutDurationMinutes))
                    .putObjectRequest(putRequest)
                    .build();

            PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
            log.info("Generated presigned PUT URL for key={}, expires in {} min", s3Key, presignedPutDurationMinutes);
            return presigned.url().toString();
        } catch (S3Exception e) {
            log.error("Failed to generate presigned PUT URL for key={}", s3Key, e);
            throw new RuntimeException("Failed to generate presigned PUT URL: " + e.getMessage(), e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // S3 Object Operations
    // ════════════════════════════════════════════════════════════════

    /**
     * Generate an S3 key for a user's video response upload.
     *
     * <p>Called by the presigned PUT upload flow to compute the key
     * before generating the presigned URL.</p>
     *
     * @param userId      owner user ID
     * @param interviewId interview ID
     * @param questionId  question ID
     * @return the computed S3 key
     */
    public String buildVideoResponseKey(Long userId, Long interviewId, Long questionId) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        return String.format("interviews/%d/%d/response_%d_%s.webm",
                userId, interviewId, questionId, timestamp);
    }

    /**
     * Delete an object from S3.
     *
     * <p>Failures are logged but do not throw exceptions (graceful degradation).
     * This prevents S3 cleanup errors from blocking business logic.</p>
     *
     * @param s3Key the S3 object key to delete
     */
    public void deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("Deleted S3 object: key={}", s3Key);
        } catch (S3Exception e) {
            log.error("Failed to delete S3 object: key={}, statusCode={}", s3Key, e.statusCode(), e);
            // Graceful — don't throw, just log
        }
    }

    /**
     * Check if an object exists in S3.
     *
     * <p>Used by the confirm-upload endpoint to verify that the frontend
     * successfully uploaded the file via presigned PUT.</p>
     *
     * @param s3Key the S3 object key to check
     * @return true if the object exists
     */
    public boolean fileExists(String s3Key) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.headObject(headRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.warn("Error checking S3 object existence: key={}, statusCode={}", s3Key, e.statusCode(), e);
            return false;
        }
    }

    /**
     * @deprecated Use {@link #generatePresignedGetUrl(String)} instead.
     *             This method exists for backward compatibility during migration.
     *             The {@code validDays} parameter is ignored — the configured
     *             {@code presignedGetDurationMinutes} is used instead.
     */
    @Deprecated(forRemoval = true, since = "P1")
    public String generatePresignedUrl(String key, int validDays) {
        return generatePresignedGetUrl(key, validDays * 24 * 60);
    }
}
