package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Dto.UpdateApplicationStatusRequest;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
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
    private final String BUCKET = "userfilesuploads";
    private static final String APPLICATIONS = "Applications";

    @Autowired
    public ApplicationRepository(DynamoDbClient client, DynamoDbItemMapper dynamoDbItemMapper, S3Client s3Client) {
        this.client = client;
        this.dynamoDbItemMapper = dynamoDbItemMapper;
        this.s3Client = s3Client;
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
        LOGGER.info("Retrieving all jobs applied by {}", applicantEmail);

        String keyConditionExpression = "applicantEmail=:appEmail";
        String attributeValue = ":appEmail";

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(attributeValue, AttributeValue.builder().s(applicantEmail).build());

        QueryRequest queryRequest = QueryRequest.builder().keyConditionExpression(keyConditionExpression).

                expressionAttributeValues(key).tableName(APPLICATIONS).build();

        try {
            QueryResponse queryResponse = client.query(queryRequest);

            if (queryResponse != null && !queryResponse.items().isEmpty()) {
                LOGGER.info("Retrieved all jobs applied by {}", applicantEmail);
                return dynamoDbItemMapper.toDynamoDbItemMap(queryResponse.items());
            }
            return new ArrayList<>();
        } catch (DynamoDbException exp) {
            LOGGER.error("Couldn't retrieve jobs applied by {}", applicantEmail);
            throw exp;
        } catch (Exception exp) {
            LOGGER.error("Unknown error occurred {}", exp.toString());
            throw exp;
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
        LOGGER.info("Retrieving all jobs applied to {}", jobId);

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":jobId", AttributeValue.builder().s(jobId).build());

        List<Map<String, Object>> mapList = new ArrayList<>();
        ScanRequest scanRequest = ScanRequest.builder().tableName(APPLICATIONS).filterExpression("jobId = :jobId").
                expressionAttributeValues(expressionValues).build();

        try {
            ScanResponse scanResponse = client.scan(scanRequest);
            List<Map<String, AttributeValue>> items = scanResponse.items();

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

        } catch (Exception e) {
            LOGGER.error("Error retrieving jobs applied to users: {}", e.getMessage());
            throw new RuntimeException(e);
        }
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

        Map<String, String > expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#status", "applicationStatus");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newStatus", AttributeValue.builder().s(statusRequest.getApplicationStatus()).build());


        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder().tableName(APPLICATIONS).
                                                key(key).updateExpression("Set #status = :newStatus").
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
