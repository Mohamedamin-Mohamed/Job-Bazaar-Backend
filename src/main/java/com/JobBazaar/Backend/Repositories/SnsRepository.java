package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.SignupRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Repository
public class SnsRepository {
    private final String STORE_TOPIC_ARN = "Store_Topic_Arn";
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());
    private final DynamoDbClient client;
    private final SnsClient snsClient;

    @Autowired
    public SnsRepository(DynamoDbClient client, SnsClient snsClient) {
        this.client = client;
        this.snsClient = snsClient;
    }

    public void saveTopicArn(String topic, String arn) {
        LOGGER.info("Saving topic arn " + topic);
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("topicName", AttributeValue.builder().s(topic).build());
        key.put("topicArn", AttributeValue.builder().s(arn).build());

        PutItemRequest req = PutItemRequest.builder().tableName(STORE_TOPIC_ARN).item(key).build();
        try {
            PutItemResponse itemResponse = client.putItem(req);
            LOGGER.info("Created topic arn " + topic);
        } catch (DynamoDbException exp) {
            LOGGER.warning(exp.toString());
            throw (exp);
        }
    }

    public boolean addSubscriberToTopic(SignupRequestDto signupRequest, String topicName) {
        LOGGER.info("Getting TopicArn for subscriber");
        String topicArn = getTopicArn(topicName);
        if (topicArn == null) return false;
        else {
            final SubscribeRequest subscribeRequest = SubscribeRequest.builder().topicArn(topicArn).protocol("email")
                    .endpoint(signupRequest.getEmail()).build();
            try {
                SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);
                LOGGER.info(subscribeResponse.toString());
                if (subscribeResponse.sdkHttpResponse().isSuccessful()) {
                    LOGGER.info("Subscriber creation successful");
                    snsClient.close();
                    return true;
                } else {
                    LOGGER.warning("Subscriber creation failed");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, subscribeResponse.sdkHttpResponse().statusText().get());
                }
            } catch (Exception exp) {
                LOGGER.warning(exp.toString());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exp.toString());
            }
        }
    }

    public String getTopicArn(String topicName) {
        LOGGER.info("Getting topic arn for topic : " + topicName);
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("topicName", AttributeValue.builder().s(topicName).build());
        GetItemRequest getItemRequest = GetItemRequest.builder().tableName(STORE_TOPIC_ARN).key(key).build();
        try {
            GetItemResponse itemResponse = client.getItem(getItemRequest);
            Map<String, AttributeValue> item = itemResponse.item();
            if (item != null && !item.isEmpty()) {
                LOGGER.info("Successful retrieval of topic : " + topicName);
                return item.get("topicArn").s();
            }
        } catch (DynamoDbException exp) {
            LOGGER.warning(exp.toString());
            throw exp;
        }
        LOGGER.info("Couldn't retrieve topic arn");
        return null;
    }
}
