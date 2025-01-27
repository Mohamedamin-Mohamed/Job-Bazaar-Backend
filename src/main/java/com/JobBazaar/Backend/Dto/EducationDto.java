package com.JobBazaar.Backend.Dto;

public class EducationDto {
    private String email;
    private String school;
    private String major;
    private String degree;
    private String startDate;
    private String endDate;

    public String getEmail() {
        return email;
    }

    public String getSchool() {
        return school;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }


    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMajor() {
        return major;
    }

    public String getDegree() {
        return degree;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
