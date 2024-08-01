package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.JwtToken.JwtTokenService;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/accounts/signup")
public class Signup {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    private final UserService userService;
    private JwtTokenService jwtToken;

    @Autowired
    public Signup(UserService userService, JwtTokenService jwtToken) {
        this.userService = userService;
        this.jwtToken = jwtToken;
    }

    @PostMapping("/")
    public ResponseEntity<Object> createUser(@RequestBody SignupRequestDto signupRequest) {
        LOGGER.info("Signup request received");

        UserDto userDto = userService.createUser(signupRequest);
        if (userDto == null) {
            String message = "Account already exists";
            Map<String, Object> response = new HashMap<>();
            response.put("user", null);
            response.put("message", message);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        String token = jwtToken.createJwtToken(userDto);
        String message = "Account created successfully, redirecting you to login";
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userDto);
        response.put("message", message);

        boolean isSubscriberAddedToTopic = userService.subscriberAddedToTopic(signupRequest, "UserAccountNotifications");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
