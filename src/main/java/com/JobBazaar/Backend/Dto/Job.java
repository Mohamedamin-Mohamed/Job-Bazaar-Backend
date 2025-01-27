package com.JobBazaar.Backend.Dto;

public class Job {
    private String employerEmail;
    private String jobId;
    private String position;
    private String company;
    private String workPlace;
    private String location;
    private String jobFunction;
    private String jobType;
    private String description;
    private String requirements;
    private String postedDate;
    private String jobStatus;

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public void setEmployerEmail(String employerEmail) {
        this.employerEmail = employerEmail;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setJobFunction(String jobFunction) {
        this.jobFunction = jobFunction;
    }

    public void setJopType(String jobType) {
        this.jobType = jobType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public String getPosition() {
        return position;
    }

    public String getCompany() {
        return company;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public String getLocation() {
        return location;
    }

    public String getJobFunction() {
        return jobFunction;
    }

    public String getJobType() {
        return jobType;
    }

    public String getDescription() {
        return description;
    }

    public String getRequirements() {
        return requirements;
    }
}
