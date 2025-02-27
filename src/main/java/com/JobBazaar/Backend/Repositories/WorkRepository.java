package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.WorkDto;
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
public class WorkRepository {

    private final DynamoDbClient client;
    private final DynamoDbItemMapper dynamoDbItemMapper;

    private final String WORK = "Work";
    private final Logger LOGGER = LoggerFactory.getLogger(WorkRepository.class);

    public WorkRepository(DynamoDbClient client, DynamoDbItemMapper dynamoDbItemMapper) {
        this.client = client;
        this.dynamoDbItemMapper = dynamoDbItemMapper;
    }

    public boolean saveWorkExperience(WorkDto workDto) throws ParseException, JsonProcessingException {
        LOGGER.info("Saving work experience");
        Map<String, AttributeValue> item = dynamoDbItemMapper.toDynamoDbItemMap(workDto);
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(WORK).item(item).build();
        try {
            PutItemResponse putItemResponse = client.putItem(putItemRequest);
            LOGGER.info("Added new work experience for {}", workDto.getEmail());
            return putItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error("Work experience couldn't be created {}", exp.getMessage());
            throw exp;
        }
    }

    public boolean updateWorkExperience(WorkDto workDto) throws ParseException, JsonProcessingException {
        LOGGER.info("Updating work experience");
        Map<String, AttributeValue> key;
        key = dynamoDbItemMapper.toDynamoDbItemMap(workDto);

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder().tableName(WORK).key(key).build();
        LOGGER.info("Updating work experience{}", updateItemRequest.toString());
        try {
            UpdateItemResponse updateItemResponse = client.updateItem(updateItemRequest);
            LOGGER.info("Updated work experience: {}", updateItemResponse.toString());
            return updateItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error("Work experience couldn't be updated {}", exp.getMessage());
            throw exp;
        }
    }

    public boolean deleteWorkExperience(String email) {
        LOGGER.info("Deleting work experience");
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(email).build());

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder().key(key).tableName(WORK).build();
        try {
            DeleteItemResponse deleteItemResponse = client.deleteItem(deleteItemRequest);
            LOGGER.info("Deleted work experience: {}", deleteItemResponse.toString());
            return deleteItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error("Work experience couldn't be deleted {}", exp.getMessage());
            throw exp;
        }
    }

    public WorkDto getWorkExperience(String email) {
        LOGGER.info("Retrieving work experience");
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(email).build());

        GetItemRequest getItemRequest = GetItemRequest.builder().key(key).tableName(WORK).build();

        try {
            GetItemResponse getItemResponse = client.getItem(getItemRequest);
            LOGGER.info("Retrieved work experience for {}", email);
            return getWorkDto(getItemResponse);
        } catch (DynamoDbException exp) {
            LOGGER.error("Work experience couldn't be retrieved {}", exp.getMessage());
            throw exp;
        }
    }

    private static WorkDto getWorkDto(GetItemResponse getItemResponse) {
        Map<String, AttributeValue> items = getItemResponse.item();
        if (items.isEmpty()) return null;

        WorkDto workDto = new WorkDto();
        for (Map.Entry<String, AttributeValue> entrySet : items.entrySet()) {
            String key = entrySet.getKey();
            AttributeValue attributeValue = entrySet.getValue();
            String value = attributeValue.s();
            if (!value.isEmpty()) {
                switch (key) {
                    case "title":
                        workDto.setTittle(value);
                        break;
                    case "company":
                        workDto.setCompany(value);
                        break;
                    case "location":
                        workDto.setLocation(value);
                        break;
                    case "startDate":
                        workDto.setStartDate(value);
                        break;
                    case "endDate":
                        workDto.setEndDate(value);
                        break;
                }
            }
        }
        return workDto;
    }
}
