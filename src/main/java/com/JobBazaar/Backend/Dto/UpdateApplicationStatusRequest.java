package com.JobBazaar.Backend.Dto;

public class UpdateApplicationStatusRequest {
    private String applicationStatus;

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }
}
