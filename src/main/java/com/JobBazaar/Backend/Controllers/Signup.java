package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Services.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
public class Signup {
    private static final Logger LOGGER = Logger.getLogger(Signup.class.getName());

    private final UserService userService;

    Dotenv dotenv = Dotenv.load();
    String emailAddress = dotenv.get("EMAIL_ADDRESS");

    @Autowired
    public Signup(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/accounts/signup")
    public ResponseEntity<String> createUser(@RequestBody SignupRequestDto signupRequest) {
        LOGGER.info("Signup request received");

        boolean isUserCreated = userService.createUser(signupRequest);
        boolean isSubscriberAddedToTopic = userService.subscriberAddedToTopic(signupRequest, "UserAccountNotifications");

        String subject = "Welcome to JobBazaar! Your account has been created!";
        String bodyHTML = "<htmL>" + "<head></head>" + "<body>" + subject + "</body>" + "</htmL>";
       // boolean isWelcomeMessageSent = userService.sendWelcomeMessage(emailAddress, signupRequest.getEmail(), subject, bodyHTML);

        if (isUserCreated && isSubscriberAddedToTopic) {
            return new ResponseEntity<>("Account created successfully, Login to your account", HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>("Account already exists", HttpStatus.CONFLICT);
        }
    }

}
