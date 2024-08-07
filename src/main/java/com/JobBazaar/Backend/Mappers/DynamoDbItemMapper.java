package com.JobBazaar.Backend.Mappers;

import com.JobBazaar.Backend.Dto.EducationDto;
import com.JobBazaar.Backend.Dto.AppUser;
import com.JobBazaar.Backend.Dto.JobPostRequest;
import com.JobBazaar.Backend.Dto.PasswordResetDto;
import com.JobBazaar.Backend.Dto.WorkDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DynamoDbItemMapper {

    private static final Logger LOGGER = Logger.getLogger(DynamoDbItemMapper.class.getName());

    public Map<String, AttributeValue> toDynamoDbItemMap(AppUser user) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", AttributeValue.builder().s(user.getEmail()).build());
        item.put("hashedPassword", AttributeValue.builder().s(user.getHashedPassword()).build());
        item.put("firstName", AttributeValue.builder().s(user.getFirstName()).build());
        item.put("lastName", AttributeValue.builder().s(user.getLastName()).build());
        item.put("role", AttributeValue.builder().s(user.getRole()).build());
        item.put("createdAt", AttributeValue.builder().s(String.valueOf(new Date())).build());
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

    public  Map<String, AttributeValue> toDynamoDbItemMap(PasswordResetDto passwordResetDto, String hashedPassword){
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", AttributeValue.builder().s(passwordResetDto.getEmail()).build());
        item.put("hashedPassword", AttributeValue.builder().s(hashedPassword).build());
        item.put("firstName", AttributeValue.builder().s(passwordResetDto.getFirstName()).build());
        item.put("lastName", AttributeValue.builder().s(passwordResetDto.getLastName()).build());
        item.put("role", AttributeValue.builder().s(passwordResetDto.getRole()).build());
        item.put("createdAt", AttributeValue.builder().s(passwordResetDto.getCreatedAt()).build());
        return item;
    }

        public Map<String, AttributeValue> toDynamoDbItemMap(JobPostRequest jobPostRequest){
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("employerEmail", AttributeValue.builder().s(jobPostRequest.getEmployerEmail()).build());
            item.put("jobId", AttributeValue.builder().s(jobPostRequest.getJobId()).build());
            item.put("company", AttributeValue.builder().s(jobPostRequest.getCompany()).build());
            item.put("position", AttributeValue.builder().s(jobPostRequest.getPosition()).build());
            item.put("workPlace", AttributeValue.builder().s(jobPostRequest.getWorkPlace()).build());
            item.put("location", AttributeValue.builder().s(jobPostRequest.getLocation()).build());
            item.put("jobFunction", AttributeValue.builder().s(jobPostRequest.getJobFunction()).build());
            item.put("jobType", AttributeValue.builder().s(jobPostRequest.getJobType()).build());
            item.put("description", AttributeValue.builder().s(jobPostRequest.getDescription()).build());
            item.put("requirements", AttributeValue.builder().s(jobPostRequest.getRequirements()).build());
        return item;
    }
}
