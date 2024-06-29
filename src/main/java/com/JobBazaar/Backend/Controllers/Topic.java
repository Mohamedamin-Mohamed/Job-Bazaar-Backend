package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.http.HttpStatusCode;
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
    @GetMapping ("/")
    public String createTopic(@RequestParam String topic) {
        final CreateTopicRequest createTopicRequest = CreateTopicRequest.builder().name(topic).build();
        final CreateTopicResponse topicResponse = snsClient.createTopic(createTopicRequest);
        if(topicResponse.sdkHttpResponse().isSuccessful()){
            System.out.println("Topic created successfully");
            userService.saveTopicArn(topic, topicResponse.topicArn());
            System.out.println("Topic ARN: " + topicResponse.topicArn());
        }
        else{
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, topicResponse.sdkHttpResponse().statusText().orElse("Cannot create topic " + topic)
            );
        }
        snsClient.close();
        return "Topic ARN: " + topicResponse.topicArn();
    }
}
