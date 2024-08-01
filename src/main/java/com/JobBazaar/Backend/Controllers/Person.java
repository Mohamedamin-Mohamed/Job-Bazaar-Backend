package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
public class Person {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);
    private final UserService userService;

    @Autowired
    public Person(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{email}/")
    public ResponseEntity<UserDto> getPerson(@PathVariable String email) {
        LOGGER.info("Getting person with email: " + email);
        UserDto names = userService.getUsersInfo(email);
        if (names != null) {
            LOGGER.info("Found: " + names.getFirstName() + names.getLastName());
            return ResponseEntity.ok(names);
        }
        return ResponseEntity.notFound().build();
    }
}
