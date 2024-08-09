package com.JobBazaar.Backend.Dto;

public class ApplicationDto {
    private String resumeName;
    private String additionalDocName;
    private String city;
    private String country;
    private String employerContact;
    private String gender;
    private String nationality;
    private String postalCode;
    private String applicationDate;
    private String applicantEmail;
    private String jobId;
    private String firstName;
    private String lastName;

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public String getJobId() {
        return jobId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getResumeName() {
        return resumeName;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setResumeName(String resumeName) {
        this.resumeName = resumeName;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public void setAdditionalDocName(String additionalDocName) {
        this.additionalDocName = additionalDocName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setEmployerContact(String employerContact) {
        this.employerContact = employerContact;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAdditionalDocName() {
        return additionalDocName;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getEmployerContact() {
        return employerContact;
    }

    public String getGender() {
        return gender;
    }

    public String getNationality() {
        return nationality;
    }

    public String getPostalCode() {
        return postalCode;
    }
}
