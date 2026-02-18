package com.interview.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for confirming a direct-to-S3 video upload.
 *
 * <p>After the frontend uploads a video response directly to S3 using a
 * presigned PUT URL, it calls the confirm-upload endpoint with this DTO.
 * The backend then verifies the object exists in S3, creates the Response
 * entity, and triggers async transcription.</p>
 *
 * <h3>Confirm Upload Flow:</h3>
 * <ol>
 *   <li>Frontend obtains presigned PUT URL via {@code GET /api/interviews/{id}/upload-url}</li>
 *   <li>Frontend uploads video directly to S3 using HTTP PUT</li>
 *   <li>Frontend sends this DTO to {@code POST /api/interviews/{id}/confirm-upload}</li>
 *   <li>Backend verifies S3 object existence via HEAD request</li>
 *   <li>Backend creates {@code Response} entity with the S3 key</li>
 *   <li>Backend triggers async transcription (AssemblyAI)</li>
 * </ol>
 */
public class ConfirmUploadRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "S3 key is required")
    private String s3Key;

    /**
     * Optional content type of the uploaded file.
     * Defaults to "video/webm" if not provided.
     */
    private String contentType;

    /**
     * Optional video duration in seconds.
     * The frontend can compute this from the MediaRecorder output.
     */
    private Integer videoDuration;

    public ConfirmUploadRequest() {
    }

    public ConfirmUploadRequest(Long questionId, String s3Key, String contentType, Integer videoDuration) {
        this.questionId = questionId;
        this.s3Key = s3Key;
        this.contentType = contentType;
        this.videoDuration = videoDuration;
    }

    // Getters and Setters

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Integer videoDuration) {
        this.videoDuration = videoDuration;
    }
}
