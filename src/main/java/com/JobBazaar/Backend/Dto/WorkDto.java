package com.JobBazaar.Backend.Dto;

public class WorkDto {
    private String email;
    private String title;
    private String Company;
    private String description;
    private String location;
    private String startDate;
    private String endDate;

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return Company;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEmail() {
        return email;
    }

    public void setTittle(String tittle) {
        this.title = tittle;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
