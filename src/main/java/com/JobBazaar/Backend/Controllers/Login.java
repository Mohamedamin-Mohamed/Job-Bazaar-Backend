package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.PasswordResetDto;
import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.JwtToken.JwtTokenService;
import com.JobBazaar.Backend.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/accounts/login")
public class Login {

    private static Logger LOGGER = LoggerFactory.getLogger(Login.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtToken;

    @Autowired
    public Login(UserService userService, AuthenticationManager authenticationManager, JwtTokenService jwtToken) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtToken = jwtToken;
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
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            //password didn't match
            return new ResponseEntity<>("Incorrect Password", HttpStatus.UNAUTHORIZED);
        }
        //user doesn't exist so return incorrect email
        return new ResponseEntity<>("Incorrect Email Address", HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>("We couldn't find your email, please make an account first!", HttpStatus.NOT_FOUND);
        }
            return new ResponseEntity<>("Email address found, reset your password", HttpStatus.OK);
    }

    @PostMapping("/password-reset/")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        LOGGER.info("Password Reset request received");

        boolean passwordChanged = userService.updateUser(passwordResetDto);
        if (passwordChanged) {
            return new ResponseEntity<>("Password Reset Successful, redirecting you to Login", HttpStatus.OK);
        }
            return new ResponseEntity<>("User Account not found, Sign up", HttpStatus.UNAUTHORIZED);
    }
}