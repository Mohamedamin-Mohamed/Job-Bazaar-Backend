package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
public class Signup {
    private static final Logger LOGGER = Logger.getLogger(Signup.class.getName());

    private final UserService userService;

    @Autowired
    public Signup(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/accounts/signup")
    public ResponseEntity<String> createUser(@RequestBody SignupRequestDto signupRequest){
        LOGGER.info("Signup request received");

        boolean isUserCreated = userService.createUser(signupRequest);
        boolean isSubscriberAddedToTopic = userService.subscriberAddedToTopic(signupRequest, "UserAccountNotifications");

        if(isUserCreated && isSubscriberAddedToTopic){
            return new ResponseEntity<>("Account created successfully, Login to your account", HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>("Account already exists", HttpStatus.CONFLICT);
        }
    }

}
