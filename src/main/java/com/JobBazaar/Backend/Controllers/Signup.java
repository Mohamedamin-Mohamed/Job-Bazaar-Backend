package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.JwtToken.JwtTokenService;
import com.JobBazaar.Backend.Services.EmailService;
import com.JobBazaar.Backend.Services.SnsService;
import com.JobBazaar.Backend.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/accounts/signup")
public class Signup {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);
    private final UserService userService;
    private final SnsService snsService;
    private final EmailService emailService;

    @Autowired
    public Signup(UserService userService, SnsService snsService, JwtTokenService jwtToken, EmailService emailService) {
        this.userService = userService;
        this.snsService = snsService;
        this.emailService = emailService;
    }

    @PostMapping("/")
    public ResponseEntity<Object> createUser(@RequestBody SignupRequestDto signupRequest) throws IOException {
        LOGGER.info("Signup request received");

        UserDto userDto = userService.createUser(signupRequest);
        if (userDto == null) {
            String message = "Account already exists";
            Map<String, Object> response = new HashMap<>();
            response.put("user", null);
            response.put("message", message);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        String message = "Account created successfully";
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);
        response.put("message", message);

        //subscribe the user to the topic and send a welcome email
        snsService.addSubscriberTopic(signupRequest, "UserAccountNotifications");

        String recipientName = userDto.getFirstName() + " " + userDto.getLastName();
        emailService.sendWelcomeOrResetEmail(userDto.getEmail(), recipientName, "welcome_email");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
