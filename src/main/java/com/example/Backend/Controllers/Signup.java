package com.example.Backend.Controllers;

import com.example.Backend.Dto.Request;
import com.example.Backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/accounts/signup")
public class Signup {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody Request signupRequest){
        String email = signupRequest.getEmail();
        String password = signupRequest.getPass();

        boolean isUserCreated = userService.createUser(email, password);
        if(isUserCreated){
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
        }
    }

}
