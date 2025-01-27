package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Dto.UpdateApplicationStatusRequest;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import com.JobBazaar.Backend.config.RedisConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ApplicationRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRepository.class);

    private final DynamoDbClient client;
    private final DynamoDbItemMapper dynamoDbItemMapper;
    private final S3Client s3Client;
    private final RedisConfig redisConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String APPLICATIONS = "Applications";
    private final int CACHE_TTL_SECONDS = 3600;

    @Autowired
    public ApplicationRepository(DynamoDbClient client, DynamoDbItemMapper dynamoDbItemMapper, S3Client s3Client, RedisConfig redisConfig) {
        this.client = client;
        this.dynamoDbItemMapper = dynamoDbItemMapper;
        this.s3Client = s3Client;
        this.redisConfig = redisConfig;
    }

    public boolean addApplication(ApplicationDto application, Map<String, Map<String, String>> fileUploadedToS3Info) {
        LOGGER.info("Adding application: {}", application);
        Map<String, AttributeValue> item = dynamoDbItemMapper.toDynamoDbItemMap(application, fileUploadedToS3Info);
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(APPLICATIONS).item(item).build();

        try {
            PutItemResponse putItemResponse = client.putItem(putItemRequest);
            LOGGER.info("Successfully added application");
            return putItemResponse.sdkHttpResponse().isSuccessful();
        } catch (Exception exp) {
            LOGGER.error("Couldn't add application: {}", exp.getMessage());
            throw exp;
        }
    }

    public List<Map<String, String>> getJobsAppliedTo(String applicantEmail) {
        LOGGER.info("Retrieving all jobs applied by {} from cache", applicantEmail);

        String redisKey = "appliedJobs:by:" + applicantEmail;
        try (Jedis jedis = redisConfig.connect()) {
            if (jedis.exists(redisKey)) {
                LOGGER.info("Cache hit for applied jobs by {}", applicantEmail);
                String cachedData = jedis.get(redisKey);
                return objectMapper.readValue(cachedData, new TypeReference<>() {
                });
            }
        } catch (Exception exp) {
            LOGGER.error("Unable to retrieve jobs applied by {} from cache", applicantEmail);
            throw new RuntimeException(exp);
        }

        LOGGER.info("Cache miss, retrieving jobs applied by {} from database", applicantEmail);
        String keyConditionExpression = "applicantEmail=:appEmail";
        String attributeValue = ":appEmail";

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(attributeValue, AttributeValue.builder().s(applicantEmail).build());

        QueryRequest queryRequest = QueryRequest.builder().tableName(APPLICATIONS).
                keyConditionExpression(keyConditionExpression).
                expressionAttributeValues(key).build();

        try {
            QueryResponse queryResponse = client.query(queryRequest);

            if (queryResponse != null && !queryResponse.items().isEmpty()) {
                LOGGER.info("Retrieved all jobs applied by {}", applicantEmail);
                List<Map<String, String>> appliedJobs = dynamoDbItemMapper.toDynamoDbItemMap(queryResponse.items());
                cacheJobs(appliedJobs, redisKey);
                return appliedJobs;
            }
        } catch (DynamoDbException exp) {
            LOGGER.error("Couldn't retrieve jobs applied by {}", applicantEmail);
            throw exp;
        } catch (Exception exp) {
            LOGGER.error("Unknown error occurred {}", exp.toString());
            throw exp;
        }
        return new ArrayList<>();
    }

    private void cacheJobs(List<Map<String, String>> jobs, String redisKey) {
        try (Jedis jedis = redisConfig.connect()) {
            String jsonData = objectMapper.writeValueAsString(jobs);
            jedis.setex(redisKey, CACHE_TTL_SECONDS, jsonData);
            LOGGER.info("Cached available jobs in Redis with key: {}", redisKey);
        } catch (Exception e) {
            LOGGER.error("Error caching available jobs in Redis: {}", e.getMessage());
        }
    }

    public boolean hasApplied(String applicantEmail, String jobId) {
        LOGGER.info("Checking if {} has applied to {}", applicantEmail, jobId);

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("applicantEmail", AttributeValue.builder().s(applicantEmail).build());
        key.put("jobId", AttributeValue.builder().s(jobId).build());

        GetItemRequest getItemRequest = GetItemRequest.builder().tableName(APPLICATIONS).key(key).build();

        try {
            GetItemResponse getItemResponse = client.getItem(getItemRequest);
            return getItemResponse.hasItem();
        } catch (DynamoDbException exp) {
            LOGGER.info("Couldn't check if {} has applied to {}", applicantEmail, jobId);
            return false;
        } catch (Exception exp) {
            LOGGER.error("Unknown error occurred {}", exp.toString());
            return false;
        }
    }

    public boolean deleteApplication(String applicantEmail, String jobId) {
        LOGGER.info("Deleting {} application with id {}:", applicantEmail, jobId);

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("applicantEmail", AttributeValue.builder().s(applicantEmail).build());
        key.put("jobId", AttributeValue.builder().s(jobId).build());

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder().tableName(APPLICATIONS).key(key).build();

        try {
            DeleteItemResponse deleteItemResponse = client.deleteItem(deleteItemRequest);
            return deleteItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error("Couldn't delete {} application with id {}", applicantEmail, jobId);
            return false;
        } catch (Exception exp) {
            LOGGER.error("Unknown error occurred {}", exp.toString());
            return false;
        }
    }

    public List<Map<String, Object>> getJobsAppliedToUsers(String jobId) {
        LOGGER.info("Retrieving all applicants applied to {} from cache", jobId);

        String redisKey = "appliedTo:jobId:" + jobId;
        try (Jedis jedis = redisConfig.connect()) {
            if (jedis.exists(redisKey)) {
                LOGGER.info("Cache hit for applicants that applied to job with job id : {}", jobId);
                String cachedResult = jedis.get(redisKey);
                return objectMapper.readValue(cachedResult, new TypeReference<>() {
                });
            }
        } catch (Exception exp) {
            LOGGER.error("Unable to retrieve all applicant that applied to {} from cache", jobId);
            throw new RuntimeException(exp);
        }

        LOGGER.info("Cache miss, retrieving all applicants that applied to job with job id {} from database", jobId);

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":jobId", AttributeValue.builder().s(jobId).build());

        ScanRequest scanRequest = ScanRequest.builder().tableName(APPLICATIONS).filterExpression("jobId = :jobId").
                expressionAttributeValues(expressionValues).build();

        try {
            ScanResponse scanResponse = client.scan(scanRequest);

            List<Map<String, AttributeValue>> items = scanResponse.items();
            List<Map<String, Object>> mapList = streamAppliedTo(items);
            jobApplicantsCacheList(mapList, redisKey);

            return mapList;

        } catch (Exception e) {
            LOGGER.error("Error retrieving jobs applied to users: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void jobApplicantsCacheList(List<Map<String, Object>> mapList, String redisKey) {
        LOGGER.info("Caching job applicants");

        try (Jedis jedis = redisConfig.connect()) {
            String jsonData = objectMapper.writeValueAsString(mapList);
            jedis.setex(redisKey, CACHE_TTL_SECONDS, jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> streamAppliedTo(List<Map<String, AttributeValue>> items) {
        return items.stream().map(item -> {
            Map<String, Object> stringObjectMap = new HashMap<>();

            item.forEach((key, value) -> {
                if (key.equals("resumeDetails") || key.equals("additionalDocDetails")) {
                    handleDocumentDetails(key, value, stringObjectMap);
                } else {
                    String stringValue = value != null && !value.s().isEmpty() ? value.s() : "";
                    stringObjectMap.put(key, stringValue);
                }
            });
            return stringObjectMap;
        }).collect(Collectors.toList());
    }

    public void handleDocumentDetails(String key, AttributeValue attributeValue, Map<String, Object> map) {
        if (attributeValue.m() != null) {
            Map<String, AttributeValue> detailsMap = attributeValue.m();
            AttributeValue s3KeyValue = detailsMap.get("S3Key");

            if (s3KeyValue != null) {
                String s3Key = s3KeyValue.s();
                try {
                    byte[] file = retrieveFile(s3Key);
                    map.put(key.equals("resumeDetails") ? "resume" : "additionalDoc", file);
                } catch (IOException e) {
                    LOGGER.error("Error retrieving file from S3: {}", e.getMessage());
                }
            }
        }
    }

    public byte[] retrieveFile(String keyName) throws IOException {
        String BUCKET = "userfilesuploads";
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(BUCKET).key(keyName).build();
        try {
            ResponseInputStream<GetObjectResponse> getObjectResponse = s3Client.getObject(getObjectRequest);
            return getObjectResponse.readAllBytes();

        } catch (S3Exception exp) {
            LOGGER.error("Couldn't retrieve file {}", exp.getMessage());
            throw exp;
        } catch (IOException exp) {
            LOGGER.error("IO error occurred {}", exp.getMessage());
            throw exp;
        }
    }

    public boolean updateApplicationStatus(String applicantEmail, String jobId, UpdateApplicationStatusRequest statusRequest) {
        LOGGER.info("Updating applicationStatus for {} of job id {}", applicantEmail, jobId);

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("applicantEmail", AttributeValue.builder().s(applicantEmail).build());
        key.put("jobId", AttributeValue.builder().s(jobId).build());

        boolean isDeclined = statusRequest.getApplicationStatus().equals("Declined") || statusRequest.getApplicationStatus().equals("Candidate Withdrew Interest");

        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#status", "applicationStatus");

        if (isDeclined) {
            expressionAttributeNames.put("#active", "isActive");
        }

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newStatus", AttributeValue.builder().s(statusRequest.getApplicationStatus()).build());

        if (isDeclined) {
            expressionAttributeValues.put(":newActive", AttributeValue.builder().s("false").build());
        }
        //build the update expression
        String updateExpression = "Set #status = :newStatus";
        if (isDeclined) {
            updateExpression += ", #active = :newActive";
        }

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder().tableName(APPLICATIONS).
                key(key).updateExpression(updateExpression).
                expressionAttributeNames(expressionAttributeNames).
                expressionAttributeValues(expressionAttributeValues).
                build();

        try {
            UpdateItemResponse updateItemResponse = client.updateItem(updateItemRequest);
            return updateItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error("Couldn't update applicationStatus for {} of job id {}", applicantEmail, jobId);
            throw exp;
        } catch (Exception exp) {
            LOGGER.error("Unknown error occurred", exp);
            throw exp;
        }
    }

}
