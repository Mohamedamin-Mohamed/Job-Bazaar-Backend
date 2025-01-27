package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.EducationDto;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class EducationRepository {

    private final DynamoDbClient client;
    private final DynamoDbItemMapper dynamoDbItemMapper;

    private final String EDUCATION = "Education";
    private final Logger LOGGER = LoggerFactory.getLogger(EducationRepository.class);

    public EducationRepository(DynamoDbClient client, DynamoDbItemMapper dynamoDbItemMapper) {
        this.client = client;
        this.dynamoDbItemMapper = dynamoDbItemMapper;
    }

    public boolean saveEducation(EducationDto educationDto) throws ParseException, JsonProcessingException {
        LOGGER.info("Saving education");
        Map<String, AttributeValue> item = new HashMap<>();
        item = dynamoDbItemMapper.toDynamoDbItemMap(educationDto);
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(EDUCATION).item(item).build();
        try {
            PutItemResponse putItemResponse = client.putItem(putItemRequest);
            LOGGER.info("Added new education: {}", putItemResponse.toString());
            return putItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error("Education couldn't be created: {}", exp.getMessage());
            throw exp;
        }
    }

    public boolean updateEducation(EducationDto educationDto) throws ParseException, JsonProcessingException {
        LOGGER.info("Updating education");
        Map<String, AttributeValue> key;
        key = dynamoDbItemMapper.toDynamoDbItemMap(educationDto);

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder().tableName(EDUCATION).key(key).build();
        LOGGER.info("Updating {}", updateItemRequest.toString());
        try {
            UpdateItemResponse updateItemResponse = client.updateItem(updateItemRequest);
            LOGGER.info("Updated education: {}", updateItemResponse.toString());
            return updateItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error("Education couldn't be updated: {}", exp.getMessage());
            throw exp;
        }
    }

    public boolean deleteEducation(String email) {
        LOGGER.info("Deleting education");
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(email).build());

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder().key(key).tableName(EDUCATION).build();
        try {
            DeleteItemResponse deleteItemResponse = client.deleteItem(deleteItemRequest);
            LOGGER.info("Deleted education: {}", deleteItemResponse.toString());
            return deleteItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error("Education couldn't be deleted {}", exp.getMessage());
            throw exp;
        }
    }

    public EducationDto getEducation(String email) {
        LOGGER.info("Retrieving education");
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(email).build());

        GetItemRequest getItemRequest = GetItemRequest.builder().key(key).tableName(EDUCATION).build();
        try {
            GetItemResponse getItemResponse = client.getItem(getItemRequest);
            LOGGER.info("Retrieved education for {}", email);
            return getEducationDto(getItemResponse);
        } catch (DynamoDbException exp) {
            LOGGER.error("Education couldn't be retrieved: {}", exp.getMessage());
            throw exp;
        }
    }

    private static EducationDto getEducationDto(GetItemResponse getItemResponse) {
        Map<String, AttributeValue> items = getItemResponse.item();
        if (items.isEmpty()) return null;

        EducationDto educationDto = new EducationDto();
        for (Map.Entry<String, AttributeValue> entrySet : items.entrySet()) {
            String key = entrySet.getKey();
            AttributeValue attributeValue = entrySet.getValue();
            String value = attributeValue.s();
            if (!value.isEmpty()) {
                switch (key) {
                    case "email":
                        educationDto.setEmail(value);
                        break;
                    case "school":
                        educationDto.setSchool(value);
                        break;
                    case "major":
                        educationDto.setMajor(value);
                        break;
                    case "degree":
                        educationDto.setDegree(value);
                        break;
                    case "startDate":
                        educationDto.setStartDate(value);
                        break;
                    case "endDate":
                        educationDto.setEndDate(value);
                        break;
                }
            }
        }
        return educationDto;
    }
}
