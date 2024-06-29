package com.JobBazaar.Backend.Dto;
/* this class is annotated to map to the DynamoDB table and includes the below fields email and hashedPassword
So basically it represents the structure of the user data that is stored in DynamoDB */

public class UserDto {
    private String email;
    private String hashedPassword;
    private String firstName;
    private String lastName;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
