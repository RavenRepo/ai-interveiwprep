package com.interview.platform.dto;

import java.time.LocalDateTime;

public class ResumeResponse {

    private Long id;
    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private String message;

    public ResumeResponse() {
    }

    public ResumeResponse(Long id, String fileName, String fileUrl, LocalDateTime uploadedAt, String message) {
        this.id = id;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploadedAt = uploadedAt;
        this.message = message;
    }

    // Builder pattern
    public static ResumeResponseBuilder builder() {
        return new ResumeResponseBuilder();
    }

    public static class ResumeResponseBuilder {
        private Long id;
        private String fileName;
        private String fileUrl;
        private LocalDateTime uploadedAt;
        private String message;

        public ResumeResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResumeResponseBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public ResumeResponseBuilder fileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        public ResumeResponseBuilder uploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public ResumeResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ResumeResponse build() {
            return new ResumeResponse(id, fileName, fileUrl, uploadedAt, message);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
