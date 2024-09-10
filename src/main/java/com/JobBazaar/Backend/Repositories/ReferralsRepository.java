package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.ReferralDto;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReferralsRepository {
    private final Logger LOGGER = LoggerFactory.getLogger(ReferralsRepository.class.getName());

    private final DynamoDbClient client;
    private final DynamoDbItemMapper dynamoDbItemMapper;
    private final S3Client s3Client;
    private final String REFERRALS = "Referrals";

    @Autowired
    public ReferralsRepository(DynamoDbClient client, DynamoDbItemMapper mapper, S3Client s3Client) {
        this.client = client;
        this.dynamoDbItemMapper = mapper;
        this.s3Client = s3Client;
    }

    public boolean addReferral(ReferralDto referralDto, Map<String, Map<String, String>> fileUploadedToS3Info) throws IOException {
        LOGGER.info("Adding referral made by {}", referralDto.getReferrerEmail());
        Map<String, AttributeValue> item = dynamoDbItemMapper.toDynamoDbItemMap(referralDto, fileUploadedToS3Info);
        System.out.println(item);
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(REFERRALS).item(item).build();

        try {
            PutItemResponse putItemResponse = client.putItem(putItemRequest);
            LOGGER.info("Successfully added application");
            return putItemResponse.sdkHttpResponse().isSuccessful();
        } catch (Exception exp) {
            LOGGER.error("Couldn't add application: {}", exp.getMessage());
            throw exp;
        }
    }

    public List<Map<String, Object>> getReferrals(String referrerEmail) {
        LOGGER.info("Retrieving referrals for {}", referrerEmail);
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":email", AttributeValue.builder().s(referrerEmail).build());
        String filterExpression = "referrerEmail = :email";
        ScanRequest scanRequest = ScanRequest.builder().tableName(REFERRALS).
                filterExpression(filterExpression).
                expressionAttributeValues(expressionValues).build();

        try {
            ScanResponse scanResponse = client.scan(scanRequest);
            List<Map<String, AttributeValue>> items = scanResponse.items();

            return items.stream().map(item -> {
                Map<String, Object> objectMap = new HashMap<>();

                item.forEach((key, value) -> {
                    if (key.equals("resumeDetails")) {
                        handleResume(value, objectMap);
                    } else {
                        objectMap.put(key, value.s());
                    }
                });
                return objectMap;
            }).collect(Collectors.toList());
        } catch (Exception exp) {
            LOGGER.error("Couldn't retrieve referrals: {}", exp.getMessage());
            throw new RuntimeException(exp);
        }
    }

    public void handleResume(AttributeValue value, Map<String, Object> objectMap) {
        Map<String, AttributeValue> map = value.m();
        String s3KeyName = map.get("S3Key").s();
        try {
            byte[] file = retrieveFile(s3KeyName);
            objectMap.put("resume", file);
        } catch (IOException e) {
            LOGGER.error("Error retrieving file from S3: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unknown error occurred {}", e.getMessage());
        }
    }

    public byte[] retrieveFile(String s3KeyName) throws IOException {
        String BUCKET = "userfilesuploads";
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(BUCKET).key(s3KeyName).build();
        try {
            ResponseInputStream<GetObjectResponse> responseEntity = s3Client.getObject(getObjectRequest);
            if (responseEntity.response().sdkHttpResponse().isSuccessful()) {
                return responseEntity.readAllBytes();
            } else {
                return null;
            }
        } catch (S3Exception exp) {
            LOGGER.error("Couldn't retrieve file {}", exp.getMessage());
            throw exp;
        } catch (IOException exp) {
            LOGGER.error("IO error occurred {}", exp.getMessage());
            throw exp;
        }
    }

    public List<Map<String, Object>> getAllReferrals() {
        LOGGER.info("Getting all available referrals");

        ScanRequest scanRequest = ScanRequest.builder().tableName(REFERRALS).build();

        try {
            ScanResponse scanResponse = client.scan(scanRequest);
            List<Map<String, AttributeValue>> items = scanResponse.items();

            return items.stream().map(item -> {
                Map<String, Object> objectMap = new HashMap<>();

                item.forEach((key, value) -> {
                    if (key.equals("resumeDetails")) {
                        handleResume(value, objectMap);
                    } else {
                        objectMap.put(key, value.s());
                    }
                });
                return objectMap;
            }).collect(Collectors.toList());
        } catch (Exception exp) {
            LOGGER.error("Couldn't retrieve referrals: {}", exp.getMessage());
            throw new RuntimeException(exp);
        }
    }
}
