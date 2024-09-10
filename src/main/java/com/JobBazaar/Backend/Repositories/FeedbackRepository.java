package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.FeedbackDto;
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
public class FeedbackRepository {
    private final Logger LOGGER = LoggerFactory.getLogger(FeedbackRepository.class);

    private static final String FEEDBACK = "Feedback";
    private final DynamoDbClient client;
    private final DynamoDbItemMapper dynamoDbItemMapper;

    @Autowired
    public FeedbackRepository(DynamoDbClient client, DynamoDbItemMapper dynamoDbItemMapper) {
        this.client = client;
        this.dynamoDbItemMapper = dynamoDbItemMapper;
    }

    public boolean addFeedBack(FeedbackDto feedbackDto) {
        LOGGER.info("Adding feedback for {}", feedbackDto.getApplicantEmail());

        Map<String, AttributeValue> item = dynamoDbItemMapper.toDynamoDbItemMap(feedbackDto);
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(FEEDBACK).item(item).build();

        try {
            PutItemResponse putItemResponse = client.putItem(putItemRequest);
            return putItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            LOGGER.error(exp.getMessage());
            throw new RuntimeException(exp);
        }
    }

    public List<FeedbackDto> getFeedbacks(String applicantEmail) {
        LOGGER.info("Getting feedbacks for {}", applicantEmail);

        List<FeedbackDto> feedbacks = new ArrayList<>();

        String keyConditionExpression = "applicantEmail = :appEmail";

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":appEmail", AttributeValue.builder().s(applicantEmail).build());

        QueryRequest queryRequest = QueryRequest.builder().tableName(FEEDBACK).
                keyConditionExpression(keyConditionExpression).
                expressionAttributeValues(expressionAttributeValues).build();

        try {
            QueryResponse queryResponse = client.query(queryRequest);
            List<Map<String, AttributeValue>> items = queryResponse.items();
            if (!items.isEmpty()) {
                return dynamoDbItemMapper.toDynamoDbFeedbackDto(items);
            }
            return feedbacks;
        } catch (DynamoDbException exp) {
            LOGGER.error("Failed to retrieve feedbacks {}", exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            LOGGER.error("Unknown error occurred {}", exp.getMessage());
            throw new RuntimeException(exp);
        }
    }

}
