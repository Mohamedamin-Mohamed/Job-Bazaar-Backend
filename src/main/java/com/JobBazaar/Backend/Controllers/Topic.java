package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Services.SnsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

@RestController
@RequestMapping("/createTopic")
public class Topic {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);
    private final SnsClient snsClient;
    private final SnsService snsService;

    @Autowired
    public Topic(SnsClient snsClient, SnsService snsService) {
        this.snsClient = snsClient;
        this.snsService = snsService;
    }

    @GetMapping("/")
    public String createTopic(@RequestParam String topic) {
        final CreateTopicRequest createTopicRequest = CreateTopicRequest.builder().name(topic).build();
        final CreateTopicResponse topicResponse = snsClient.createTopic(createTopicRequest);
        if (topicResponse.sdkHttpResponse().isSuccessful()) {
            LOGGER.info("Topic created successfully!");
            snsService.saveTopicArn(topic, topicResponse.topicArn());
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, topicResponse.sdkHttpResponse().statusText().orElse("Cannot create topic " + topic));
        }
        snsClient.close();
        return "Topic ARN: " + topicResponse.topicArn();
    }
}
