package com.JobBazaar.Backend.Mappers;

import com.JobBazaar.Backend.Dto.EducationDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Dto.WorkDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DynamoDbItemMapper {

    private static Logger LOGGER = Logger.getLogger(DynamoDbItemMapper.class.getName());

    public Map<String, AttributeValue> toDynamoDbItemMap(UserDto user) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", AttributeValue.builder().s(user.getEmail()).build());
        item.put("hashedPassword", AttributeValue.builder().s(user.getHashedPassword()).build());
        item.put("firstName", AttributeValue.builder().s(user.getFirstName()).build());
        item.put("lastName", AttributeValue.builder().s(user.getLastName()).build());
        return item;
    }

    public Map<String, AttributeValue> toDynamoDbItemMap(EducationDto educationDto) throws ParseException, JsonProcessingException {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", AttributeValue.builder().s(educationDto.getEmail()).build());
        item.put("school", AttributeValue.builder().s(educationDto.getSchool()).build());
        item.put("major", AttributeValue.builder().s(educationDto.getMajor()).build());
        item.put("degree", AttributeValue.builder().s(educationDto.getDegree()).build());
        item.put("description", AttributeValue.builder().s(educationDto.getDescription()).build());
        item.put("startDate", AttributeValue.builder().s(educationDto.getStartDate()).build());
        item.put("endDate", AttributeValue.builder().s(educationDto.getEndDate()).build());

        return item;
    }

    public Map<String, AttributeValue> toDynamoDbItemMap(WorkDto workDto) throws ParseException, JsonProcessingException {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", AttributeValue.builder().s(workDto.getEmail()).build());
        item.put("title", AttributeValue.builder().s(workDto.getTitle()).build());
        item.put("company", AttributeValue.builder().s(workDto.getCompany()).build());
        item.put("location", AttributeValue.builder().s(workDto.getLocation()).build());
        item.put("description", AttributeValue.builder().s(workDto.getDescription()).build());
        item.put("startDate", AttributeValue.builder().s(workDto.getStartDate()).build());
        item.put("endDate", AttributeValue.builder().s(workDto.getEndDate()).build());

        return item;
    }
}
