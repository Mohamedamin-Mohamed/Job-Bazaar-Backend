package com.example.Backend.Dto;
 /* This class is a DTO(Data Transfer Object) used to map the incoming JSON data from the http request to a Java object.
 So this class basically represents the data structure of the Signup request which contains the fields with name email
 and pass
  */
public class SignupRequest {
    private String email;
    private String pass;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
