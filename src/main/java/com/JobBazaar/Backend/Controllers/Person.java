package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.Person;
import com.JobBazaar.Backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class User {
    private UserService userService;

    @Autowired
    public User (UserService userService) {
        this.userService = userService;
    }
    @GetMapping ("/person/{email}")
    public ResponseEntity<Person> getPerson(@PathVariable String email) {
        Person person = userService.getUsersInfo(email);
        return ResponseEntity.ok(person);
    }
}
