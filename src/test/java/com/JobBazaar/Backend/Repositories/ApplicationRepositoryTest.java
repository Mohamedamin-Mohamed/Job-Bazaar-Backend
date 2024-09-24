package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationRepositoryTest {
    @Mock
    DynamoDbClient client;

    @Mock
    DynamoDbItemMapper dbItemMapper;

    @Mock
    S3Client s3Client;

    @InjectMocks
    ApplicationRepository applicationRepository;

    @Test
    void addApplication_Successful() {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("applicantEmail", AttributeValue.builder().s("test@test.com").build());
        map.put("gender", AttributeValue.builder().s("Male").build());
        map.put("isActive", AttributeValue.builder().s("true").build());

        when(dbItemMapper.toDynamoDbItemMap(any(ApplicationDto.class), anyMap())).thenReturn(map);
        SdkHttpResponse httpResponse = SdkHttpResponse.builder().statusCode(200).build();
        PutItemResponse putItemResponse = (PutItemResponse) PutItemResponse.builder().sdkHttpResponse(httpResponse).build();
        when(client.putItem(any(PutItemRequest.class))).thenReturn(putItemResponse);
        boolean applicationAdded = applicationRepository.addApplication(new ApplicationDto(), new HashMap<>());

        assertTrue(applicationAdded);
    }

    @Test
    void addApplication_Fails() {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("applicantEmail", AttributeValue.builder().s("test@test.com").build());
        map.put("gender", AttributeValue.builder().s("Male").build());
        map.put("isActive", AttributeValue.builder().s("true").build());

        when(dbItemMapper.toDynamoDbItemMap(any(ApplicationDto.class), anyMap())).thenReturn(map);
        SdkHttpResponse httpResponse = SdkHttpResponse.builder().statusCode(500).build();
        PutItemResponse putItemResponse = (PutItemResponse) PutItemResponse.builder().sdkHttpResponse(httpResponse).build();
        when(client.putItem(any(PutItemRequest.class))).thenReturn(putItemResponse);
        boolean applicationAdded = applicationRepository.addApplication(new ApplicationDto(), new HashMap<>());

        assertFalse(applicationAdded);
    }

    @Test
    void addApplication_Throws_Exception() {
        when(client.putItem(any(PutItemRequest.class))).thenThrow(DynamoDbException.class);
        assertThrows(DynamoDbException.class, ()-> applicationRepository.addApplication(new ApplicationDto(), new HashMap<>()));
    }

}