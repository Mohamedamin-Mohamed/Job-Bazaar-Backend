package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Beans.AppConfig;
import com.JobBazaar.Backend.Dto.SignupRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import software.amazon.awssdk.core.client.builder.SdkDefaultClientBuilder;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SnsRepositoryTest {
    @Mock
    SnsClient snsClient;

    @Mock
    DynamoDbClient client;

    @Spy
    SnsRepository snsRepository;

    @BeforeEach
    void setUp() {
        snsRepository = new SnsRepository(client, snsClient);
    }

    @Test
    void safeTopicArnSuccessful() {
        String topic = "topic";
        String arn = "arn";
        PutItemResponse response = (PutItemResponse) PutItemResponse.builder().sdkHttpResponse(null).build();

        when(client.putItem(any(PutItemRequest.class))).thenReturn(response);
        snsRepository.saveTopicArn(topic, arn);

        verify(client).putItem(any(PutItemRequest.class));
    }

    @Test
    void saveTopicArnFailed_ExceptionThrown() {
        String topic = "topic";
        String arn = "arn";

        when(client.putItem(any(PutItemRequest.class))).thenThrow(DynamoDbException.class);

        assertThrows(DynamoDbException.class, () -> snsRepository.saveTopicArn(topic, arn));
        verify(client).putItem(any(PutItemRequest.class));
    }

    @Test
    void getTopicArnSuccessful() {
        String topic = "topic";
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("topicArn", AttributeValue.builder().s("blah-blah-blah").build());

        GetItemResponse response = GetItemResponse.builder().item(item).build();

        when(client.getItem(any(GetItemRequest.class))).thenReturn(response);
        String result = snsRepository.getTopicArn(topic);

        assertEquals(14, result.length());
        assertEquals("blah-blah-blah", result);
        verify(client).getItem(any(GetItemRequest.class));
    }

    @Test
    void getTopicArnFailed() {
        String topic = "topic";

        when(client.getItem(any(GetItemRequest.class))).thenReturn(GetItemResponse.builder().item(new HashMap<>()).build());
        String result = snsRepository.getTopicArn(topic);

        assertNull(result);
        verify(client).getItem(any(GetItemRequest.class));
    }

    @Test
    void getTopicArnFailed_ExceptionThrown() {
        String topic = "topic";

        when(client.getItem(any(GetItemRequest.class))).thenThrow(DynamoDbException.class);
        assertThrows(DynamoDbException.class, () -> snsRepository.getTopicArn(topic));
    }

    @Test
    void addSubscriberToTopicSuccessful() {
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setEmail("test@test.com");
        String topic = "topic";

        doReturn(topic).when(snsRepository).getTopicArn(any());
        SdkHttpResponse httpResponse = (SdkHttpResponse) SdkHttpResponse.builder().statusCode(200);
        SubscribeResponse response = (SubscribeResponse) SubscribeResponse.builder().sdkHttpResponse(httpResponse).build();

        when(snsClient.subscribe(any(SubscribeRequest.class))).thenReturn(response);

        boolean result = snsRepository.addSubscriberToTopic(signupRequestDto, topic);

        assertTrue(result);
        verify(snsClient).subscribe(any(SubscribeRequest.class));
    }
}