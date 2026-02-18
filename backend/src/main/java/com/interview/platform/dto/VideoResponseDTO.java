package com.interview.platform.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for submitting a video response to a question.
 */
public class VideoResponseDTO {

    private Long interviewId;
    private Long questionId;
    private MultipartFile videoFile;

    public VideoResponseDTO() {
    }

    // Getters and Setters
    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public MultipartFile getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(MultipartFile videoFile) {
        this.videoFile = videoFile;
    }
}
