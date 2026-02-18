package com.interview.platform.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request body for starting a new interview.
 */
public class StartInterviewRequest {

    @NotNull(message = "Resume ID is required")
    private Long resumeId;

    @NotNull(message = "Job Role ID is required")
    private Long jobRoleId;

    public StartInterviewRequest() {
    }

    public StartInterviewRequest(Long resumeId, Long jobRoleId) {
        this.resumeId = resumeId;
        this.jobRoleId = jobRoleId;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getJobRoleId() {
        return jobRoleId;
    }

    public void setJobRoleId(Long jobRoleId) {
        this.jobRoleId = jobRoleId;
    }
}
