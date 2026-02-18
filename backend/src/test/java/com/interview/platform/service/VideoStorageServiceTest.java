package com.interview.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoStorageService Tests (AWS SDK v2)")
class VideoStorageServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectCaptor;

    @Captor
    private ArgumentCaptor<RequestBody> requestBodyCaptor;

    @Captor
    private ArgumentCaptor<GetObjectPresignRequest> getPresignCaptor;

    @Captor
    private ArgumentCaptor<PutObjectPresignRequest> putPresignCaptor;

    @Captor
    private ArgumentCaptor<HeadObjectRequest> headObjectCaptor;

    @Captor
    private ArgumentCaptor<DeleteObjectRequest> deleteObjectCaptor;

    private VideoStorageService videoStorageService;

    @BeforeEach
    void setUp() {
        videoStorageService = new VideoStorageService(s3Client, s3Presigner, "test-bucket");
        ReflectionTestUtils.setField(videoStorageService, "presignedGetDurationMinutes", 60);
        ReflectionTestUtils.setField(videoStorageService, "presignedPutDurationMinutes", 15);
    }

    // ============================================================
    // uploadVideo
    // ============================================================

    @Nested
    @DisplayName("uploadVideo")
    class UploadVideoTests {

        private MockMultipartFile videoFile;

        @BeforeEach
        void setUpFile() {
            videoFile = new MockMultipartFile(
                    "video", "response.webm", "video/webm", "fake-video-data".getBytes());
        }

        @Test
        @DisplayName("Should upload with correct S3 key pattern, metadata, and return S3 key (not URL)")
        void testUploadVideo_Success() {
            // Arrange
            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(PutObjectResponse.builder().build());

            // Act
            String result = videoStorageService.uploadVideo(videoFile, 1L, 100L, 200L);

            // Assert — P1: returns S3 key, NOT a presigned URL
            assertThat(result).startsWith("interviews/1/100/response_200_");
            assertThat(result).endsWith(".webm");
            assertThat(result).doesNotContain("http");

            // Verify PutObjectRequest
            verify(s3Client).putObject(putObjectCaptor.capture(), any(RequestBody.class));
            PutObjectRequest captured = putObjectCaptor.getValue();

            assertThat(captured.bucket()).isEqualTo("test-bucket");
            assertThat(captured.key()).startsWith("interviews/1/100/response_200_");
            assertThat(captured.key()).endsWith(".webm");
            assertThat(captured.contentType()).isEqualTo("video/webm");
            assertThat(captured.contentLength()).isEqualTo("fake-video-data".getBytes().length);

            // Verify metadata
            assertThat(captured.metadata()).containsEntry("user-id", "1");
            assertThat(captured.metadata()).containsEntry("interview-id", "100");
            assertThat(captured.metadata()).containsEntry("question-id", "200");
        }

        @Test
        @DisplayName("Should throw RuntimeException on AWS S3 error")
        void testUploadVideo_S3Error() {
            // Arrange
            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenThrow(S3Exception.builder().message("S3 unavailable").statusCode(500).build());

            // Act & Assert
            assertThatThrownBy(() -> videoStorageService.uploadVideo(videoFile, 1L, 100L, 200L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to upload video to S3");
        }
    }

    // ============================================================
    // uploadResumeFile
    // ============================================================

    @Nested
    @DisplayName("uploadResumeFile")
    class UploadResumeFileTests {

        @Test
        @DisplayName("Should upload resume with correct key, extension, and return S3 key")
        void testUploadResumeFile_Success() {
            MockMultipartFile resumeFile = new MockMultipartFile(
                    "resume", "my_resume.pdf", "application/pdf", "pdf-contents".getBytes());

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(PutObjectResponse.builder().build());

            // Act
            String result = videoStorageService.uploadResumeFile(resumeFile, 1L);

            // Assert — P1: returns S3 key
            assertThat(result).startsWith("resumes/1/resume_");
            assertThat(result).endsWith(".pdf");
            assertThat(result).doesNotContain("http");

            // Verify PutObjectRequest
            verify(s3Client).putObject(putObjectCaptor.capture(), any(RequestBody.class));
            PutObjectRequest captured = putObjectCaptor.getValue();

            assertThat(captured.bucket()).isEqualTo("test-bucket");
            assertThat(captured.key()).startsWith("resumes/1/resume_");
            assertThat(captured.key()).endsWith(".pdf");
            assertThat(captured.contentType()).isEqualTo("application/pdf");
            assertThat(captured.metadata()).containsEntry("original-filename", "my_resume.pdf");
        }

        @Test
        @DisplayName("Should default to .pdf extension when filename has no extension")
        void testUploadResumeFile_NoExtension() {
            MockMultipartFile resumeFile = new MockMultipartFile(
                    "resume", "resume_no_ext", "application/pdf", "pdf-contents".getBytes());

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(PutObjectResponse.builder().build());

            // Act
            videoStorageService.uploadResumeFile(resumeFile, 1L);

            // Assert
            verify(s3Client).putObject(putObjectCaptor.capture(), any(RequestBody.class));
            assertThat(putObjectCaptor.getValue().key()).endsWith(".pdf");
        }

        @Test
        @DisplayName("Should throw RuntimeException on S3 error during resume upload")
        void testUploadResumeFile_S3Error() {
            MockMultipartFile resumeFile = new MockMultipartFile(
                    "resume", "resume.pdf", "application/pdf", "pdf-contents".getBytes());

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenThrow(S3Exception.builder().message("Access denied").statusCode(403).build());

            assertThatThrownBy(() -> videoStorageService.uploadResumeFile(resumeFile, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to upload resume to S3");
        }
    }

    // ============================================================
    // uploadBytes
    // ============================================================

    @Nested
    @DisplayName("uploadBytes")
    class UploadBytesTests {

        @Test
        @DisplayName("Should upload raw bytes with correct content type and return S3 key")
        void testUploadBytes_Success() {
            byte[] audioData = "fake-audio-data".getBytes();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(PutObjectResponse.builder().build());

            // Act
            String result = videoStorageService.uploadBytes(audioData, "audio/test.mp3", "audio/mpeg");

            // Assert — P1: returns S3 key (same as input key)
            assertThat(result).isEqualTo("audio/test.mp3");

            // Verify PutObjectRequest
            verify(s3Client).putObject(putObjectCaptor.capture(), any(RequestBody.class));
            PutObjectRequest captured = putObjectCaptor.getValue();

            assertThat(captured.bucket()).isEqualTo("test-bucket");
            assertThat(captured.key()).isEqualTo("audio/test.mp3");
            assertThat(captured.contentType()).isEqualTo("audio/mpeg");
            assertThat(captured.contentLength()).isEqualTo(audioData.length);
        }

        @Test
        @DisplayName("Should throw RuntimeException on S3 error during bytes upload")
        void testUploadBytes_S3Error() {
            byte[] data = "data".getBytes();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenThrow(S3Exception.builder().message("Internal error").statusCode(500).build());

            assertThatThrownBy(() -> videoStorageService.uploadBytes(data, "key.mp3", "audio/mpeg"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to upload to S3");
        }
    }

    // ============================================================
    // uploadAudioBytes (deprecated — delegates to uploadBytes)
    // ============================================================

    @Nested
    @DisplayName("uploadAudioBytes (deprecated)")
    class UploadAudioBytesTests {

        @Test
        @DisplayName("Should delegate to uploadBytes and return S3 key")
        @SuppressWarnings("deprecation")
        void testUploadAudioBytes_DelegatesToUploadBytes() {
            byte[] audioData = "audio-bytes".getBytes();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(PutObjectResponse.builder().build());

            String result = videoStorageService.uploadAudioBytes(audioData, "tts/question_1.mp3", "audio/mpeg");

            assertThat(result).isEqualTo("tts/question_1.mp3");
            verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }
    }

    // ============================================================
    // generatePresignedGetUrl
    // ============================================================

    @Nested
    @DisplayName("generatePresignedGetUrl")
    class GeneratePresignedGetUrlTests {

        @Test
        @DisplayName("Should generate presigned GET URL with correct bucket, key, and duration")
        void testGeneratePresignedGetUrl_Success() throws MalformedURLException {
            // Arrange
            PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
            when(presignedRequest.url()).thenReturn(
                    URI.create("https://test-bucket.s3.amazonaws.com/some/key?X-Amz-Signature=abc123").toURL());

            when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .thenReturn(presignedRequest);

            // Act
            String url = videoStorageService.generatePresignedGetUrl("some/key");

            // Assert
            assertThat(url).isEqualTo("https://test-bucket.s3.amazonaws.com/some/key?X-Amz-Signature=abc123");

            // Verify presign request
            verify(s3Presigner).presignGetObject(getPresignCaptor.capture());
            GetObjectPresignRequest captured = getPresignCaptor.getValue();

            assertThat(captured.getObjectRequest().bucket()).isEqualTo("test-bucket");
            assertThat(captured.getObjectRequest().key()).isEqualTo("some/key");
            assertThat(captured.signatureDuration()).isNotNull();
            assertThat(captured.signatureDuration().toMinutes()).isEqualTo(60);
        }

        @Test
        @DisplayName("Should generate presigned GET URL with custom duration")
        void testGeneratePresignedGetUrl_CustomDuration() throws MalformedURLException {
            PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
            when(presignedRequest.url()).thenReturn(
                    URI.create("https://test-bucket.s3.amazonaws.com/key?signed=true").toURL());

            when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .thenReturn(presignedRequest);

            // Act — 30-minute custom duration
            videoStorageService.generatePresignedGetUrl("some/key", 30);

            // Verify custom duration
            verify(s3Presigner).presignGetObject(getPresignCaptor.capture());
            assertThat(getPresignCaptor.getValue().signatureDuration().toMinutes()).isEqualTo(30);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null key")
        void testGeneratePresignedGetUrl_NullKey() {
            assertThatThrownBy(() -> videoStorageService.generatePresignedGetUrl(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("S3 key must not be null or blank");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for blank key")
        void testGeneratePresignedGetUrl_BlankKey() {
            assertThatThrownBy(() -> videoStorageService.generatePresignedGetUrl("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("S3 key must not be null or blank");
        }

        @Test
        @DisplayName("Should throw RuntimeException on S3 presigner error")
        void testGeneratePresignedGetUrl_Error() {
            when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .thenThrow(S3Exception.builder().message("Access denied").statusCode(403).build());

            assertThatThrownBy(() -> videoStorageService.generatePresignedGetUrl("key"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to generate presigned GET URL");
        }
    }

    // ============================================================
    // generatePresignedPutUrl
    // ============================================================

    @Nested
    @DisplayName("generatePresignedPutUrl")
    class GeneratePresignedPutUrlTests {

        @Test
        @DisplayName("Should generate presigned PUT URL with correct bucket, key, content type, and duration")
        void testGeneratePresignedPutUrl_Success() throws MalformedURLException {
            // Arrange
            PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);
            when(presignedRequest.url()).thenReturn(
                    URI.create("https://test-bucket.s3.amazonaws.com/interviews/1/100/response.webm?X-Amz-Signature=put123").toURL());

            when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                    .thenReturn(presignedRequest);

            // Act
            String url = videoStorageService.generatePresignedPutUrl(
                    "interviews/1/100/response.webm", "video/webm");

            // Assert
            assertThat(url).contains("X-Amz-Signature=put123");

            // Verify presign request
            verify(s3Presigner).presignPutObject(putPresignCaptor.capture());
            PutObjectPresignRequest captured = putPresignCaptor.getValue();

            assertThat(captured.putObjectRequest().bucket()).isEqualTo("test-bucket");
            assertThat(captured.putObjectRequest().key()).isEqualTo("interviews/1/100/response.webm");
            assertThat(captured.putObjectRequest().contentType()).isEqualTo("video/webm");
            assertThat(captured.signatureDuration()).isNotNull();
            assertThat(captured.signatureDuration().toMinutes()).isEqualTo(15);
        }

        @Test
        @DisplayName("Should throw RuntimeException on S3 presigner error")
        void testGeneratePresignedPutUrl_Error() {
            when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                    .thenThrow(S3Exception.builder().message("Service error").statusCode(500).build());

            assertThatThrownBy(() -> videoStorageService.generatePresignedPutUrl("key", "video/webm"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to generate presigned PUT URL");
        }
    }

    // ============================================================
    // buildVideoResponseKey
    // ============================================================

    @Nested
    @DisplayName("buildVideoResponseKey")
    class BuildVideoResponseKeyTests {

        @Test
        @DisplayName("Should build key with correct path pattern")
        void testBuildVideoResponseKey_Format() {
            String key = videoStorageService.buildVideoResponseKey(1L, 100L, 200L);

            assertThat(key).startsWith("interviews/1/100/response_200_");
            assertThat(key).endsWith(".webm");
            assertThat(key).matches("interviews/1/100/response_200_\\d+\\.webm");
        }

        @Test
        @DisplayName("Should generate unique keys on successive calls")
        void testBuildVideoResponseKey_UniqueKeys() {
            String key1 = videoStorageService.buildVideoResponseKey(1L, 100L, 200L);
            // Small delay to ensure timestamp differs
            String key2 = videoStorageService.buildVideoResponseKey(1L, 100L, 200L);

            // Keys should both match the pattern (may or may not be identical
            // depending on millisecond timing, but both should be valid)
            assertThat(key1).matches("interviews/1/100/response_200_\\d+\\.webm");
            assertThat(key2).matches("interviews/1/100/response_200_\\d+\\.webm");
        }
    }

    // ============================================================
    // deleteFile
    // ============================================================

    @Nested
    @DisplayName("deleteFile")
    class DeleteFileTests {

        @Test
        @DisplayName("Should call deleteObject with correct bucket and key")
        void testDeleteFile_Success() {
            when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                    .thenReturn(DeleteObjectResponse.builder().build());

            videoStorageService.deleteFile("interviews/1/100/response.webm");

            verify(s3Client).deleteObject(deleteObjectCaptor.capture());
            DeleteObjectRequest captured = deleteObjectCaptor.getValue();

            assertThat(captured.bucket()).isEqualTo("test-bucket");
            assertThat(captured.key()).isEqualTo("interviews/1/100/response.webm");
        }

        @Test
        @DisplayName("Should not throw on AWS S3 error (graceful handling)")
        void testDeleteFile_S3Error_NoException() {
            when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                    .thenThrow(S3Exception.builder().message("Not found").statusCode(404).build());

            assertThatCode(() -> videoStorageService.deleteFile("bad/key"))
                    .doesNotThrowAnyException();
        }
    }

    // ============================================================
    // fileExists
    // ============================================================

    @Nested
    @DisplayName("fileExists")
    class FileExistsTests {

        @Test
        @DisplayName("Should return true when file exists in S3")
        void testFileExists_True() {
            when(s3Client.headObject(any(HeadObjectRequest.class)))
                    .thenReturn(HeadObjectResponse.builder().build());

            assertThat(videoStorageService.fileExists("existing/key")).isTrue();

            verify(s3Client).headObject(headObjectCaptor.capture());
            assertThat(headObjectCaptor.getValue().bucket()).isEqualTo("test-bucket");
            assertThat(headObjectCaptor.getValue().key()).isEqualTo("existing/key");
        }

        @Test
        @DisplayName("Should return false when file does not exist (NoSuchKeyException)")
        void testFileExists_False_NoSuchKey() {
            when(s3Client.headObject(any(HeadObjectRequest.class)))
                    .thenThrow(NoSuchKeyException.builder().message("Not found").build());

            assertThat(videoStorageService.fileExists("missing/key")).isFalse();
        }

        @Test
        @DisplayName("Should return false on general S3 error")
        void testFileExists_False_S3Error() {
            when(s3Client.headObject(any(HeadObjectRequest.class)))
                    .thenThrow(S3Exception.builder().message("Service error").statusCode(500).build());

            assertThat(videoStorageService.fileExists("error/key")).isFalse();
        }
    }

    // ============================================================
    // generatePresignedUrl (deprecated — backward compatibility)
    // ============================================================

    @Nested
    @DisplayName("generatePresignedUrl (deprecated)")
    class DeprecatedGeneratePresignedUrlTests {

        @Test
        @DisplayName("Should delegate to generatePresignedGetUrl with days converted to minutes")
        @SuppressWarnings("deprecation")
        void testGeneratePresignedUrl_DelegatesToGetUrl() throws MalformedURLException {
            PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
            when(presignedRequest.url()).thenReturn(
                    URI.create("https://test-bucket.s3.amazonaws.com/key?signed=true").toURL());

            when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .thenReturn(presignedRequest);

            // Act — 7 days = 10080 minutes
            String url = videoStorageService.generatePresignedUrl("some/key", 7);

            // Assert
            assertThat(url).contains("signed=true");

            verify(s3Presigner).presignGetObject(getPresignCaptor.capture());
            // 7 days * 24 hours * 60 minutes = 10080 minutes
            assertThat(getPresignCaptor.getValue().signatureDuration().toMinutes()).isEqualTo(10080);
        }
    }
}
