package com.JobBazaar.Backend.Dto;
/* this class is annotated to map to the DynamoDB table and includes the below fields email and hashedPassword
So basically it represents the structure of the user data that is stored in DynamoDB */

public class UserDto {
    private String email;
    private String hashedPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
