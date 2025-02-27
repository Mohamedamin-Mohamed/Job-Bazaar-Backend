package com.JobBazaar.Backend.Mappers;

import com.JobBazaar.Backend.Dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.text.ParseException;
import java.util.*;

public class DynamoDbItemMapper {

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
        item.put("startDate", AttributeValue.builder().s(workDto.getStartDate()).build());
        item.put("endDate", AttributeValue.builder().s(workDto.getEndDate()).build());

        return item;
    }

    public Map<String, AttributeValue> toDynamoDbItemMap(PasswordResetDto passwordResetDto, String hashedPassword) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", AttributeValue.builder().s(passwordResetDto.getEmail()).build());
        item.put("hashedPassword", AttributeValue.builder().s(hashedPassword).build());
        item.put("firstName", AttributeValue.builder().s(passwordResetDto.getFirstName()).build());
        item.put("lastName", AttributeValue.builder().s(passwordResetDto.getLastName()).build());
        item.put("role", AttributeValue.builder().s(passwordResetDto.getRole()).build());
        item.put("createdAt", AttributeValue.builder().s(passwordResetDto.getCreatedAt()).build());
        return item;
    }

    public Map<String, AttributeValue> toDynamoDbItemMap(Job job) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("employerEmail", AttributeValue.builder().s(job.getEmployerEmail()).build());
        item.put("jobId", AttributeValue.builder().s(job.getJobId()).build());
        item.put("company", AttributeValue.builder().s(job.getCompany()).build());
        item.put("position", AttributeValue.builder().s(job.getPosition()).build());
        item.put("workPlace", AttributeValue.builder().s(job.getWorkPlace()).build());
        item.put("location", AttributeValue.builder().s(job.getLocation()).build());
        item.put("jobFunction", AttributeValue.builder().s(job.getJobFunction()).build());
        item.put("jobType", AttributeValue.builder().s(job.getJobType()).build());
        item.put("description", AttributeValue.builder().s(job.getDescription()).build());
        item.put("requirements", AttributeValue.builder().s(job.getRequirements()).build());
        item.put("postedDate", AttributeValue.builder().s(job.getPostedDate()).build());
        item.put("jobStatus", AttributeValue.builder().s(job.getJobStatus()).build());
        return item;
    }

    public Map<String, AttributeValue> toDynamoDbItemMap(ApplicationDto applicationDto, Map<String, Map<String, String>> documentDetails) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("applicantEmail", AttributeValue.builder().s(applicationDto.getApplicantEmail()).build());
        item.put("employerEmail", AttributeValue.builder().s(applicationDto.getEmployerEmail()).build());
        item.put("jobId", AttributeValue.builder().s(applicationDto.getJobId()).build());
        item.put("position", AttributeValue.builder().s(applicationDto.getPosition()).build());
        item.put("resumeName", AttributeValue.builder().s(applicationDto.getResumeName()).build());
        item.put("country", AttributeValue.builder().s(applicationDto.getCountry()).build());
        item.put("city", AttributeValue.builder().s(applicationDto.getCity()).build());
        item.put("postalCode", AttributeValue.builder().s(applicationDto.getPostalCode()).build());
        item.put("gender", AttributeValue.builder().s(applicationDto.getGender()).build());
        item.put("nationality", AttributeValue.builder().s(applicationDto.getNationality()).build());

        if (applicationDto.getAdditionalDocName() != null) {
            item.put("additionalDocName", AttributeValue.builder().s(applicationDto.getAdditionalDocName()).build());
        }

        item.put("employerContact", AttributeValue.builder().s(applicationDto.getEmployerContact()).build());
        item.put("firstName", AttributeValue.builder().s(applicationDto.getFirstName()).build());
        item.put("lastName", AttributeValue.builder().s(applicationDto.getLastName()).build());
        item.put("applicationDate", AttributeValue.builder().s(applicationDto.getApplicationDate()).build());
        item.put("applicationStatus", AttributeValue.builder().s(applicationDto.getApplicationStatus()).build());
        item.put("isActive", AttributeValue.builder().s(applicationDto.getIsActive()).build());

        if (documentDetails != null) {
            if (documentDetails.containsKey("resume")) {
                item.put("resumeDetails", convertMapToDynamoDbMap(documentDetails.get("resume")));
            }
            if (documentDetails.containsKey("additionalDoc")) {
                item.put("additionalDocDetails", convertMapToDynamoDbMap(documentDetails.get("additionalDoc")));
            }
        }
        return item;
    }

    public AttributeValue convertMapToDynamoDbMap(Map<String, String> map) {
        Map<String, AttributeValue> dynamoDbMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            dynamoDbMap.put(entry.getKey(), AttributeValue.builder().s(entry.getValue()).build());
        }
        return AttributeValue.builder().m(dynamoDbMap).build();
    }

    public List<Map<String, String>> toDynamoDbItemMap(List<Map<String, AttributeValue>> listJobAppliedTo) {
        List<Map<String, String>> jobsAppliedTo = new ArrayList<>();

        for (Map<String, AttributeValue> map : listJobAppliedTo) {
            Map<String, String> mapToString = new HashMap<>();

            for (Map.Entry<String, AttributeValue> jobsApplied : map.entrySet()) {
                mapToString.put(jobsApplied.getKey(), jobsApplied.getValue().s());
            }
            jobsAppliedTo.add(mapToString);
        }
        return jobsAppliedTo;
    }

    public Map<String, String> toDynamoDbItemMap(Map<String, AttributeValue> map) {
        Map<String, String> jobByIdMap = new HashMap<>();

        for (Map.Entry<String, AttributeValue> entry : map.entrySet()) {
            jobByIdMap.put(entry.getKey(), entry.getValue().s());
        }
        return jobByIdMap;
    }

    public Map<String, AttributeValue> toDynamoDbItemMap(FeedbackDto feedbackDto) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("applicantEmail", AttributeValue.builder().s(feedbackDto.getApplicantEmail()).build());
        item.put("jobId", AttributeValue.builder().s(feedbackDto.getJobId()).build());
        item.put("feedbackDate", AttributeValue.builder().s(feedbackDto.getFeedbackDate()).build());
        item.put("feedback", AttributeValue.builder().s(feedbackDto.getFeedback()).build());
        item.put("status", AttributeValue.builder().s(feedbackDto.getStatus()).build());
        return item;
    }

    public Map<String, AttributeValue> toDynamoDbItemMap(ReferralDto referralDto, Map<String, Map<String, String>> documentDetails) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("referrerEmail", AttributeValue.builder().s(referralDto.getReferrerEmail()).build());
        item.put("referrerName", AttributeValue.builder().s(referralDto.getReferrerName()).build());
        item.put("fileName", AttributeValue.builder().s(referralDto.getFileName()).build());
        item.put("createdAt", AttributeValue.builder().s(referralDto.getCreatedAt()).build());

        if (documentDetails != null) {
            if (documentDetails.containsKey("refereeResumeFile")) {
                item.put("resumeDetails", convertMapToDynamoDbMap(documentDetails.get("refereeResumeFile")));
            }
        }
        return item;
    }

    public List<FeedbackDto> toDynamoDbFeedbackDto(List<Map<String, AttributeValue>> items) {
        List<FeedbackDto> feedbacks = new ArrayList<>();
        for (Map<String, AttributeValue> item : items) {
            FeedbackDto feedbackDto = new FeedbackDto();
            feedbackDto.setApplicantEmail(item.get("applicantEmail").s());
            feedbackDto.setJobId(item.get("jobId").s());
            feedbackDto.setFeedbackDate(item.get("feedbackDate").s());
            feedbackDto.setFeedback(item.get("feedback").s());
            feedbackDto.setStatus(item.get("status").s());
            feedbacks.add(feedbackDto);
        }
        return feedbacks;
    }

}
