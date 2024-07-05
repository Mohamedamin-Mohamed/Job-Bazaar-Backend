package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TopicTest {
    @Mock
    SnsClient snsClient;

    @Mock
    UserService userService;

    @InjectMocks
    Topic topic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTopicSuccessful() {
        SdkHttpResponse httpResponse = SdkHttpResponse.builder().statusCode(200).build();

        CreateTopicResponse response = (CreateTopicResponse) CreateTopicResponse.builder().topicArn("topic-arn").
                sdkHttpResponse(httpResponse).build();

        when(snsClient.createTopic(any(CreateTopicRequest.class))).thenReturn(response);
        String result = topic.createTopic("topic-arn");

        assertNotNull(result);
        assertTrue(result.contains("topic-arn"));
        verify(snsClient).createTopic(any(CreateTopicRequest.class));
    }

    @Test
    void createTopicExceptionThrown() {
        SdkHttpResponse httpResponse = SdkHttpResponse.builder().statusCode(500).build();
        CreateTopicResponse response = (CreateTopicResponse) CreateTopicResponse.builder().topicArn("topic-arn").
                sdkHttpResponse(httpResponse).build();

        when(snsClient.createTopic(any(CreateTopicRequest.class))).thenReturn(response);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> topic.createTopic("topic-arn"));
        assertEquals(500, exception.getStatusCode().value());
        assertEquals("Cannot create topic topic-arn", exception.getReason());
    }

}