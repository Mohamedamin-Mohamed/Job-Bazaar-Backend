package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Controllers.Application;
import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.Map;

@Repository
public class ApplicationRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRepository.class);

    public final DynamoDbClient client;
    public final DynamoDbItemMapper dynamoDbItemMapper;

    private static final String APPLICATION = "Applicants";
    @Autowired
    public ApplicationRepository(DynamoDbClient client, DynamoDbItemMapper dynamoDbItemMapper) {
        this.client = client;
        this.dynamoDbItemMapper = dynamoDbItemMapper;
    }

    public boolean addApplication(ApplicationDto application, Map<String, Map<String, String>> fileUploadedToS3Info) {
        LOGGER.info("Adding application: {}", application);
        Map<String, AttributeValue> item = dynamoDbItemMapper.toDynamoDbItemMap(application, fileUploadedToS3Info);

        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(APPLICATION).item(item).build();
        try{
            PutItemResponse putItemResponse = client.putItem(putItemRequest);
            LOGGER.info("Successfully added application");
            return putItemResponse.sdkHttpResponse().isSuccessful();
        }
        catch(Exception exp){
            LOGGER.error("Couldn't add application: {}", exp.getMessage());
            throw exp;
        }
    }
}
