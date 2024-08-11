package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.JobPostRequest;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JobRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobRepository.class);
    private final String JOBS = "Jobs";

    private final DynamoDbClient client;
    private final DynamoDbItemMapper dynamoDbItemMapper;

    @Autowired
    public JobRepository(DynamoDbClient client, DynamoDbItemMapper dynamoDbItemMapper) {
        this.client = client;
        this.dynamoDbItemMapper = dynamoDbItemMapper;
    }

    public boolean saveJob(JobPostRequest jobPostRequest) {
        LOGGER.info("Saving job with employer email {}", jobPostRequest.getEmployerEmail());

        Map<String, AttributeValue> item = dynamoDbItemMapper.toDynamoDbItemMap(jobPostRequest);

        PutItemRequest putItemRequest = PutItemRequest.builder().item(item).tableName(JOBS).build();

        try {
            PutItemResponse putItemResponse = client.putItem(putItemRequest);
            LOGGER.info("Job saved successfully");
            return putItemResponse.sdkHttpResponse().isSuccessful();
        } catch (Exception exp) {
            LOGGER.error("Couldn't save new job {}", exp.toString());
            throw exp;
        }
    }

    public List<Map<String, String>> getAvailableJobs(){
        LOGGER.info("Retrieving available jobs");

        ScanRequest scanRequest = ScanRequest.builder().tableName(JOBS).build();

        try{
            ScanResponse scanResponse = client.scan(scanRequest);
            LOGGER.info("Retrieved available jobs");

            if(!scanResponse.items().isEmpty()){
                return dynamoDbItemMapper.toDynamoDbItemMap(scanResponse.items());
            }
            return new ArrayList<>();
        }
        catch (Exception exp){
            LOGGER.error("Couldn't retrieve available jobs {}", exp.toString());
            throw exp;
        }
    }

    public List<Map<String, String>> getJobsByEmployerEmail(String employerEmail) {
        LOGGER.info("Getting jobs uploaded by employer with email {}", employerEmail);

        String keyConditionExpression = "employerEmail=:empEmail";
        Map<String, AttributeValue> key = new HashMap<>();
        String attributeValue = ":empEmail";

        key.put(attributeValue, AttributeValue.builder().s(employerEmail).build());

        QueryRequest queryRequest = QueryRequest.builder().keyConditionExpression(keyConditionExpression).
                expressionAttributeValues(key).tableName(JOBS).build();

        try {
            QueryResponse queryResponse = client.query(queryRequest);
            if(!queryResponse.items().isEmpty()){
                return dynamoDbItemMapper.toDynamoDbItemMap(queryResponse.items());
            }
            return new ArrayList<>();
        } catch (Exception exp) {
            LOGGER.error("Couldn't retrieve uploaded jobs {}", exp.toString());
            throw exp;
        }
    }

    public Map<String, String> getJobsById(String employerEmail, String jobId) {
        LOGGER.info("Retrieving job related to job id {}", jobId);

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("employerEmail", AttributeValue.builder().s(employerEmail).build());
        key.put("jobId", AttributeValue.builder().s(jobId).build());

        GetItemRequest getItemRequest = GetItemRequest.builder().tableName(JOBS).key(key).build();

        try {
            GetItemResponse getItemResponse = client.getItem(getItemRequest);
            LOGGER.info("Retrieved the job with job id {}", jobId);

            if(!getItemResponse.item().isEmpty()) {
                return dynamoDbItemMapper.toDynamoDbItemMap(getItemResponse.item());
            }
            return new HashMap<>();
        } catch (Exception exp) {
            LOGGER.error("Couldn't retrieve the job with id {}", jobId + exp.toString());
            throw exp;
        }
    }
}
