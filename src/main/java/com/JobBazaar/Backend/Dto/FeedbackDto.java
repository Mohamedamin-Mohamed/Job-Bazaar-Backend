package com.JobBazaar.Backend.Dto;

public class FeedbackDto {
    private String applicantEmail;
    private String jobId;
    private String feedbackDate;
    private String feedback;
    private String status;

    public String getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(String feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getApplicantEmail() {
        return applicantEmail;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getStatus() {
        return status;
    }
}
