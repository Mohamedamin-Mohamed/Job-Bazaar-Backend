package com.example.Backend.Controllers;

import com.example.Backend.Dto.Request;
import com.example.Backend.Services.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping ("/accounts/login")
public class Login {
    private String email;
    private String pass;
    private static Logger LOGGER = Logger.getLogger(Login.class.getName());

    @Autowired
    private UserService userService;
    @PostMapping("/")
    public ResponseEntity<String> checkCredentials(@RequestBody Request loginRequest ) {
        LOGGER.info("Login request received");

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        //first check if there is a user with the above email
        boolean userExists = userService.userExists(email);
        //if true check if the password matches with the users email
        if (userExists) {
            //now check if the password matches the email
            boolean passwordMatch = userService.passwordMatches(email, password);
            if (passwordMatch) {
                return new ResponseEntity<>("Login Successful", HttpStatus.OK);
            }
            //password didn't match
            return new ResponseEntity<>("Incorrect Password", HttpStatus.UNAUTHORIZED);
        }
        //user doesn't exist so return incorrect email
        return new ResponseEntity<>("Incorrect Email Address", HttpStatus.NOT_FOUND);
    }
    @GetMapping ("/{email}/email-lookup/")
    public ResponseEntity<String> emailLookup(@PathVariable  String email){
        LOGGER.info("Email lookup request received");
        boolean userExists = userService.userExists(email);
        if (!userExists) {
            return new ResponseEntity<>("We cannot find your email, please make an account first!", HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<>("Email address found, reset your password", HttpStatus.OK);
        }
    }
    @PostMapping ("/password-reset/")
    public ResponseEntity<String> resetPassword(@RequestBody Request passwordResetRequest){
        LOGGER.info("Password Reset request received");
        String email = passwordResetRequest.getEmail();
        String password = passwordResetRequest.getPassword();
        boolean passwordChanged = userService.changePassword(email, password);
        if(passwordChanged){
            return new ResponseEntity<>("Password Reset Successful, redirecting you to Login", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("User Account not found, Log in", HttpStatus.UNAUTHORIZED);
        }
    }

}
