package com.example.Backend.Controllers;

import com.example.Backend.Dto.Request;
import com.example.Backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Signup {

    @Autowired
    private UserService userService;

    @PostMapping("/accounts/signup")
    public ResponseEntity<String> createUser(@RequestBody Request signupRequest){
        String email = signupRequest.getEmail();
        String password = signupRequest.getPassword();

        boolean isUserCreated = userService.createUser(email, password);
        if(isUserCreated){
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>("Account already exists", HttpStatus.CONFLICT);
        }
    }

}
