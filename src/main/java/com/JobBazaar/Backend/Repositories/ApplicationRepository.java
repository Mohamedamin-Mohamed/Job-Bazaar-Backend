package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ApplicationRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRepository.class);

    public final DynamoDbClient client;
    public final DynamoDbItemMapper dynamoDbItemMapper;

    private static final String APPLICATIONS = "Applications";

    @Autowired
    public ApplicationRepository(DynamoDbClient client, DynamoDbItemMapper dynamoDbItemMapper) {
        this.client = client;
        this.dynamoDbItemMapper = dynamoDbItemMapper;
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

    public boolean hasApplied(String applicantEmail, String jobId){
        LOGGER.info("Checking if {} has applied to {}", applicantEmail, jobId);

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("applicantEmail", AttributeValue.builder().s(applicantEmail).build());
        key.put("jobId", AttributeValue.builder().s(jobId).build());

        GetItemRequest getItemRequest = GetItemRequest.builder().tableName(APPLICATIONS).key(key).build();

        try{
            GetItemResponse getItemResponse = client.getItem(getItemRequest);
            return  getItemResponse.hasItem();
        }
        catch (DynamoDbException exp) {
            LOGGER.info("Couldn't check if {} has applied to {}", applicantEmail, jobId);
            return false;
        }
        catch (Exception exp) {
            LOGGER.error("Unknown error occurred {}", exp.toString());
            return false;
        }
    }
}
