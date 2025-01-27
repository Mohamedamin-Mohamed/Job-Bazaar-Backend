package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.PasswordResetDto;
import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.JwtToken.JwtTokenService;
import com.JobBazaar.Backend.Services.EmailService;
import com.JobBazaar.Backend.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/accounts/login")
public class Login {

    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    private final UserService userService;
    private final JwtTokenService jwtToken;
    private final EmailService emailService;

    @Autowired
    public Login(UserService userService, JwtTokenService jwtToken, EmailService emailService) {
        this.userService = userService;
        this.jwtToken = jwtToken;
        this.emailService = emailService;
    }

    @PostMapping("/")
    public ResponseEntity<Object> checkCredentials(@RequestBody RequestDto loginRequest) {
        LOGGER.info("Login request received");
        //first check if there is a user with the above email
        boolean userExists = userService.userExists(loginRequest);

        //if true, check if the password matches with the users email
        if (userExists) {
            //now check if the password matches the email
            boolean passwordMatch = userService.passwordMatches(loginRequest);
            if (passwordMatch) {
                UserDto userDto = userService.getUsersInfo(loginRequest.getEmail());
                String token = jwtToken.createJwtToken(userDto);
                String message = "Login Successful";
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", userDto);
                response.put("message", message);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
            //password didn't match
            return new ResponseEntity<>("Incorrect Password", HttpStatus.UNAUTHORIZED);
        }
        //user doesn't exist so return incorrect email
        return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{email}/email-lookup/")
    public ResponseEntity<String> emailLookup(@PathVariable String email) {
        LOGGER.info("Email lookup request received");

        //just make an object because method needs an object of type RequestDto
        RequestDto requestDto = new RequestDto();
        requestDto.setEmail(email);
        requestDto.setPass("");
        boolean userExists = userService.userExists(requestDto);
        if (!userExists) {
            return new ResponseEntity<>("Email not found. Please create an account.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Email address found. Reset your password.", HttpStatus.OK);
    }

    @PostMapping("/password-reset/")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDto passwordResetDto) throws IOException {
        LOGGER.info("Password Reset request received");

        boolean passwordChanged = userService.updateUser(passwordResetDto);
        if (passwordChanged) {
            String recipientEmail = passwordResetDto.getEmail();
            String fullName = passwordResetDto.getFirstName() + " " + passwordResetDto.getLastName();
            emailService.sendWelcomeOrResetEmail(recipientEmail, fullName, "forgot_password");
            return new ResponseEntity<>("Password reset. Redirecting.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Account not found. Sign up.", HttpStatus.UNAUTHORIZED);
    }
}