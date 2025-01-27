package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Services.EmailService;
import com.JobBazaar.Backend.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/person")
public class Person {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public Person(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/{email}/")
    public ResponseEntity<UserDto> getPerson(@PathVariable String email) {
        LOGGER.info("Getting person with email: {}", email);
        UserDto names = userService.getUsersInfo(email);
        if (names != null) {
            LOGGER.info("Found: {}{}", names.getFirstName(), names.getLastName());
            return ResponseEntity.ok(names);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteAccount(@PathVariable String email, @RequestParam String role) throws IOException {
        LOGGER.info("Received request to delete account with email {}", email);

        UserDto user = userService.getUsersInfo(email);
        String fullName = user.getFirstName() + " " + user.getLastName();

        boolean accountDeleted = userService.deleteAccount(email, role);

        if (accountDeleted) {
            //now remove cache related data for the user
            userService.deleteCacheData(email, role);

            //then send email notifying the user of their account deletion
            emailService.sendAccountDeletionEmail(email, fullName);
            return new ResponseEntity<>("Account successfully deleted.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Account deletion failed. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
