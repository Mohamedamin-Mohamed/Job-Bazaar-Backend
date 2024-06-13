package com.example.Backend.Controllers;

import com.example.Backend.Dto.RequestDto;
import com.example.Backend.Services.UserService;
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

    private final UserService userService;

    @Autowired
    public Login(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/")
    public ResponseEntity<String> checkCredentials(@RequestBody RequestDto loginRequest ) {
        LOGGER.info("Login request received");

        //first check if there is a user with the above email
        boolean userExists = userService.userExists(loginRequest);

        //if true check if the password matches with the users email
        if (userExists) {
            //now check if the password matches the email
            boolean passwordMatch = userService.passwordMatches(email, loginRequest.getPassword());
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

        //just make an object because method needs an object of type RequestDto
        RequestDto requestDto = new RequestDto();
        requestDto.setEmail(email);
        requestDto.setPass("");
        boolean userExists = userService.userExists(requestDto);
        System.out.println(userExists);
        if (!userExists) {
            return new ResponseEntity<>("We cannot find your email, please make an account first!", HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<>("Email address found, reset your password", HttpStatus.OK);
        }
    }
    @PostMapping ("/password-reset/")
    public ResponseEntity<String> resetPassword(@RequestBody RequestDto passwordResetRequest){
        LOGGER.info("Password Reset request received");

        boolean passwordChanged = userService.updateUser(passwordResetRequest);
        if(passwordChanged){
            return new ResponseEntity<>("Password Reset Successful, redirecting you to Login", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("User Account not found, Log in", HttpStatus.UNAUTHORIZED);
        }
    }

}
