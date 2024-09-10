package com.JobBazaar.Backend.Dto;

public class ReferralDto {
    private String referrerEmail;
    private String referrerName;
    private String fileName;
    private String createdAt;

    public String getReferrerName() {
        return referrerName;
    }

    public void setReferrerName(String referrerName) {
        this.referrerName = referrerName;
    }

    public String getReferrerEmail() {
        return referrerEmail;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setReferrerEmail(String referrerEmail) {
        this.referrerEmail = referrerEmail;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
