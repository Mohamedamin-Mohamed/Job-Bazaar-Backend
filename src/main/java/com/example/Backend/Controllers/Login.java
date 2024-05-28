package com.example.Backend.Controllers;

import com.example.Backend.Dto.Request;
import com.example.Backend.Services.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/accounts/login")
public class Login {
    private String email;
    private String pass;

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<String> checkCredentials(@RequestBody Request loginRequest ){
        String email = loginRequest.getEmail();
        String password = loginRequest.getPass();

        //first check if there is a user with the above email
        boolean userExists = userService.userExists(email);
        //if true check if the password matches with the users email
        if(userExists){
            //now check if the password matches the email
            boolean passwordMatch = userService.passwordMatches(email, password);
        }
        //user doesn't exist so return incorrect email
        return new ResponseEntity<>("Incorrect email address", HttpStatus.NOT_FOUND);
    }
}
