package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.UserNames;
import com.JobBazaar.Backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class Person {
    private static final Logger LOGGER = Logger.getLogger(Person.class.getName());
    private UserService userService;

    @Autowired
    public Person(UserService userService) {
        this.userService = userService;
    }
    @GetMapping ("/person/{email}")
    public ResponseEntity<UserNames> getPerson(@PathVariable String email) {
        LOGGER.info("Getting person with email: " + email);
        UserNames names = userService.getUsersInfo(email);
        if(names != null) {
            LOGGER.info("Found person with email: " + names.getFirstName() + names.getLastName());
            return ResponseEntity.ok(names);
        }
        return ResponseEntity.notFound().build();
    }
}
