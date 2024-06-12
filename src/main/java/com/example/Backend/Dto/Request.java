package com.example.Backend.Dto;
 /* This class is a DTO(Data Transfer Object) used to map the incoming JSON data from the http request to a Java object.
 So this class basically represents the data structure of the Signup/Login request which contains the fields with name email
 and pass
  */
public class Request {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPass(String password) {
        this.password = password;
    }
}
