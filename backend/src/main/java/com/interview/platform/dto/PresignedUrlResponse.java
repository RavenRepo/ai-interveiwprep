package com.interview.platform.dto;

/**
 * Response DTO returned by the presigned upload URL endpoint.
 *
 * <p>Contains everything the frontend needs to upload a video response
 * directly to S3 via HTTP PUT:</p>
 * <ul>
 *   <li>{@code uploadUrl} — the presigned PUT URL to upload to</li>
 *   <li>{@code s3Key} — the S3 object key (sent back in confirm-upload)</li>
 *   <li>{@code expiresInSeconds} — how long the URL is valid</li>
 * </ul>
 *
 * <h3>Frontend Usage:</h3>
 * <pre>{@code
 * // 1. Request presigned URL
 * const { uploadUrl, s3Key } = await fetch('/api/interviews/123/upload-url?questionId=456');
 *
 * // 2. Upload directly to S3
 * await fetch(uploadUrl, {
 *   method: 'PUT',
 *   headers: { 'Content-Type': 'video/webm' },
 *   body: videoBlob
 * });
 *
 * // 3. Confirm upload
 * await fetch('/api/interviews/123/confirm-upload', {
 *   method: 'POST',
 *   body: JSON.stringify({ questionId: 456, s3Key })
 * });
 * }</pre>
 */
public class PresignedUrlResponse {

    private String uploadUrl;
    private String s3Key;
    private long expiresInSeconds;

    public PresignedUrlResponse() {
    }

    public PresignedUrlResponse(String uploadUrl, String s3Key, long expiresInSeconds) {
        this.uploadUrl = uploadUrl;
        this.s3Key = s3Key;
        this.expiresInSeconds = expiresInSeconds;
    }

    // Getters and Setters

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }
}
