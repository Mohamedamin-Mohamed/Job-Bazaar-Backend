package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Services.SnsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicTest {
    @Mock
    SnsClient snsClient;

    @Mock
    SnsService snsService;

    @InjectMocks
    Topic topic;

    @Test
    void createTopicSuccessful() {
        String topicName = "Notifications";
        SdkHttpResponse httpResponse = SdkHttpResponse.builder().statusCode(200).build();
        CreateTopicResponse topicResponse = (CreateTopicResponse) CreateTopicResponse.builder().
                topicArn("topic-arn").sdkHttpResponse(httpResponse).build();

        when(snsClient.createTopic(any(CreateTopicRequest.class))).thenReturn(topicResponse);
        doNothing().when(snsService).saveTopicArn(anyString(), anyString());

        String result = topic.createTopic(topicName);

        assertNotNull(result);
        assertTrue(result.contains(topicResponse.topicArn()));
        verify(snsClient).createTopic(any(CreateTopicRequest.class));
    }

    @Test
    void createTopicFailed() {
        String topicName = "Notifications";
        SdkHttpResponse httpResponse = SdkHttpResponse.builder().statusCode(500).build();
        CreateTopicResponse topicResponse = (CreateTopicResponse) CreateTopicResponse.builder().
                topicArn("topic-arn").sdkHttpResponse(httpResponse).build();

        when(snsClient.createTopic(any(CreateTopicRequest.class))).thenReturn(topicResponse);

        ResponseStatusException statusException = assertThrows(ResponseStatusException.class, ()-> topic.createTopic(topicName));

        assertEquals(500, statusException.getStatusCode().value());
        assertEquals("Cannot create topic Notifications", statusException.getReason());
    }
}