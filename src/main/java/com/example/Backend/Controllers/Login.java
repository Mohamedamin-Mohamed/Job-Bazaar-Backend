package com.example.Backend.Controllers;

import com.example.Backend.Dto.Request;
import com.example.Backend.Services.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Login {
    private String email;
    private String pass;

    @Autowired
    private UserService userService;

    @PostMapping("/accounts/login")
    public ResponseEntity<String> checkCredentials(@RequestBody Request loginRequest ) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        //first check if there is a user with the above email
        boolean userExists = userService.userExists(email);
        //if true check if the password matches with the users email
        if (userExists) {
            //now check if the password matches the email
            boolean passwordMatch = userService.passwordMatches(email, password);
            if (passwordMatch) {
                return new ResponseEntity<>("Credentials match", HttpStatus.OK);
            }
            //password didn't match
            return new ResponseEntity<>("Incorrect Password", HttpStatus.UNAUTHORIZED);
        }
        //user doesn't exist so return incorrect email
        return new ResponseEntity<>("Incorrect Email Address", HttpStatus.NOT_FOUND);
    }

}
