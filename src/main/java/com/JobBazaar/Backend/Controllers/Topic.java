package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

@RestController
@RequestMapping ("/createTopic")
public class Topic {

    private final SnsClient snsClient;
    private final UserService userService;
    @Autowired
    public Topic(SnsClient snsClient, UserService userService) {
        this.snsClient = snsClient;
        this.userService = userService;
    }
    
}
