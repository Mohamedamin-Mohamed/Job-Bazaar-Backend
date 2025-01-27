package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.*;
import com.JobBazaar.Backend.Mappers.DynamoDbItemMapper;
import com.JobBazaar.Backend.Utils.PasswordUtils;
import com.JobBazaar.Backend.config.RedisConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final DynamoDbClient client;
    private final DynamoDbItemMapper itemMapper;
    private final PasswordUtils passwordUtils;
    private final RedisConfig redisConfig;

    private final String USERS = "Users";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);

    @Autowired
    public UserRepository(DynamoDbClient client, DynamoDbItemMapper itemMapper, PasswordUtils passwordUtils, RedisConfig redisConfig) {
        this.client = client;
        this.itemMapper = itemMapper;
        this.passwordUtils = passwordUtils;
        this.redisConfig = redisConfig;
    }

    public boolean addUser(AppUser user) {
        LOGGER.info("Adding user: {}", user.toString());

        Map<String, AttributeValue> item = itemMapper.toDynamoDbItemMap(user);
        PutItemRequest request = PutItemRequest.builder().tableName(USERS).item(item).build();

        try {
            PutItemResponse response = client.putItem(request);
            LOGGER.info("Created user with email {}", user.getEmail());
            return response.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.warn("User account couldn't be created {}", String.valueOf(exp));
            throw exp;
        }
    }

    public boolean updateUser(PasswordResetDto passwordResetDto) {
        LOGGER.info("Updating user with email {}", passwordResetDto.getEmail());

        //hash the users password
        String hashedPassword = passwordUtils.hashPassword(passwordResetDto.getPassword());

        Map<String, AttributeValue> key = itemMapper.toDynamoDbItemMap(passwordResetDto, hashedPassword);

        PutItemRequest req = PutItemRequest.builder().tableName(USERS).item(key).build();
        try {
            PutItemResponse resp = client.putItem(req);
            LOGGER.info("Changed password for user with email {}", passwordResetDto.getEmail());
            return resp.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.warn(exp.toString());
            throw exp;
        }
    }

    public boolean passwordMatches(RequestDto requestDto) {
        LOGGER.info("Checking password for user with email {}", requestDto.getEmail());

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(requestDto.getEmail()).build());
        String hashedPassword;

        GetItemRequest request = GetItemRequest.builder().tableName(USERS).key(key).build();
        try {
            GetItemResponse response = client.getItem(request);
            Map<String, AttributeValue> item = response.item();
            if (item != null && !item.isEmpty()) {
                AttributeValue hashedPasswordAttr = item.get("hashedPassword");
                hashedPassword = hashedPasswordAttr.s();
                LOGGER.info("Comparing plainText password from the user with the stored hashed password");

                //now compare the hashedPassword retrieved with the plainText from the user
                return passwordUtils.checkPassword(requestDto.getPassword(), hashedPassword);
            } else return false;
        } catch (DynamoDbException exp) {
            LOGGER.warn(exp.toString());
            throw exp;
        }
    }

    public boolean userExists(RequestDto requestDto) {
        LOGGER.info("Checking if user with email {} exists", requestDto.getEmail());
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(requestDto.getEmail()).build());
        GetItemRequest req = GetItemRequest.builder().tableName(USERS).key(key).build();
        try {
            //it returns an object which can be checked if its null and if its empty
            GetItemResponse resp = client.getItem(req);
            Map<String, AttributeValue> item = resp.item();
            //check if the item exists and is not empty
            return item != null && !item.isEmpty();
        } catch (DynamoDbException exp) {
            LOGGER.warn(exp.toString());
            throw exp;
        }
    }

    public UserDto getUsersInfo(String email) {
        LOGGER.info("Grabbing {} info", email);
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", AttributeValue.builder().s(email).build());
        GetItemRequest req = GetItemRequest.builder().tableName(USERS).key(key).build();
        try {
            GetItemResponse resp = client.getItem(req);
            return getUserDto(email, resp);
        } catch (DynamoDbException exp) {
            LOGGER.warn(exp.toString());
            throw exp;
        }
    }

    private static UserDto getUserDto(String email, GetItemResponse resp) {
        Map<String, AttributeValue> item = resp.item();
        UserDto person = null;
        if (item != null && !item.isEmpty()) {
            person = new UserDto();

            String firstName = item.get("firstName").s();
            String lastName = item.get("lastName").s();
            String role = item.get("role").s();
            String createdAt = item.get("createdAt").s();

            person.setEmail(email);
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setRole(role);
            person.setCreatedAt(createdAt);
        }
        return person;
    }

    public boolean deleteAccount(String email, String role) {
        LOGGER.info("Deleting account with email: {} and role: {}", email, role);

        String[] tableNames = role.equalsIgnoreCase("Applicant") ?
                new String[]{"Users", "Applications", "Education", "Work", "Feedback", "Referrals"} :
                new String[]{"Users", "Jobs"};

        Map<String, String> mappings = createMappings();
        boolean success = true;

        try {
            List<TransactWriteItem> allTransactionItems = buildTransactionItems(tableNames, email, mappings);

            // process in batches of 100
            for (int i = 0; i < allTransactionItems.size(); i += 100) {
                int end = Math.min(i + 100, allTransactionItems.size());
                List<TransactWriteItem> batch = allTransactionItems.subList(i, end);

                TransactWriteItemsRequest request = TransactWriteItemsRequest.builder()
                        .transactItems(batch)
                        .build();

                try {
                    client.transactWriteItems(request);
                    LOGGER.info("Successfully processed batch {} for account: {}", i / 100 + 1, email);
                } catch (TransactionCanceledException e) {
                    success = false;
                    List<CancellationReason> reasons = e.cancellationReasons();
                    for (int j = 0; j < reasons.size(); j++) {
                        CancellationReason reason = reasons.get(j);
                        if (reason.code() != null) {
                            LOGGER.error("Batch {} - Item {} failed: {} - {}",
                                    i / 100 + 1, j, reason.code(), reason.message());
                        }
                    }
                }
            }
        } catch (DynamoDbException e) {
            LOGGER.error("Failed to delete account for email: {}", email, e);
            return false;
        }

        return success;
    }

    private List<TransactWriteItem> buildTransactionItems(String[] tableNames, String email, Map<String, String> mappings) {
        List<TransactWriteItem> transactionItems = new ArrayList<>();

        for (String tableName : tableNames) {
            String partitionKeyName = mappings.get(tableName);

            switch (tableName.toLowerCase()) {
                case "applications":
                case "jobs":
                case "referrals":
                case "feedback":
                    QueryRequest queryRequest = QueryRequest.builder()
                            .tableName(tableName)
                            .keyConditionExpression("#pk = :emailValue")
                            .expressionAttributeNames(Map.of("#pk", partitionKeyName))
                            .expressionAttributeValues(Map.of(":emailValue", AttributeValue.builder().s(email).build()))
                            .build();

                    try {
                        QueryResponse response = client.query(queryRequest);
                        for (Map<String, AttributeValue> item : response.items()) {
                            // create a new map with only the key attributes
                            Map<String, AttributeValue> keyMap = new HashMap<>();

                            // add partition key
                            keyMap.put(partitionKeyName, item.get(partitionKeyName));

                            // add correct sort key based on table
                            switch (tableName.toLowerCase()) {
                                case "applications":
                                case "jobs":
                                case "feedback":
                                    keyMap.put("jobId", item.get("jobId"));
                                    break;
                                case "referrals":
                                    keyMap.put("fileName", item.get("fileName"));
                                    break;
                            }

                            LOGGER.info("Table: {} - Using key structure: {}", tableName, keyMap);

                            Delete delete = Delete.builder()
                                    .tableName(tableName)
                                    .key(keyMap)
                                    .build();

                            transactionItems.add(TransactWriteItem.builder().delete(delete).build());
                        }
                    } catch (DynamoDbException e) {
                        LOGGER.error("Error querying table {} for email: {}", tableName, email, e);
                    }
                    break;

                default:
                    // for tables with just email as the key (Users, Education, Work)
                    Map<String, AttributeValue> key = new HashMap<>();
                    key.put(partitionKeyName, AttributeValue.builder().s(email).build());

                    Delete delete = Delete.builder()
                            .tableName(tableName)
                            .key(key)
                            .build();

                    transactionItems.add(TransactWriteItem.builder().delete(delete).build());
            }
        }

        // check for transaction limit
        if (transactionItems.size() > 100) {
            LOGGER.warn("Transaction items exceed DynamoDB limit of 100. Total items: {}", transactionItems.size());
            return transactionItems.subList(0, 100);
        }

        return transactionItems;
    }

    private Map<String, String> createMappings() {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("Users", "email");
        mappings.put("Applications", "applicantEmail");
        mappings.put("Jobs", "employerEmail");
        mappings.put("Education", "email");
        mappings.put("Work", "email");
        mappings.put("Referrals", "referrerEmail");
        mappings.put("Feedback", "applicantEmail");
        return mappings;
    }

    public void deleteCacheData(String email, String role) {
        LOGGER.info("Received request to delete cache data for email {}: ", email);
        // removal is required for applied jobs and referrals for users with role Applicant
        String appliedJobsKey = "appliedJobs:by:" + email;
        String referralsKey = "referrals:referredBy:" + email;
        String uploadedJobsKey = "jobs:all:" + email;


        try (Jedis jedis = redisConfig.connect()) {
            if (role.equalsIgnoreCase("Applicant")) {
                jedis.del(appliedJobsKey, referralsKey);
            } else {
                jedis.del(uploadedJobsKey);
                //now remove the list of jobs employer has uploaded from the cache
                deleteJobsFromCache(jedis, email, "jobs:all");
            }
            LOGGER.info("Deleted cache data for email {}: ", email);
        } catch (Exception exp) {
            LOGGER.error("Failed to delete cache data for email {}: {}", email, exp.getMessage());
            throw new RuntimeException("Failed to delete cache data for email " + email, exp);
        }
    }

    public void deleteJobsFromCache(Jedis jedis, String email, String redisKey) {
        List<Job> jobs;
        ObjectMapper objectMapper = new ObjectMapper();
        long CACHE_TTL_SECONDS = 3600;

        try {
            // fetch the cached job list from Redis
            String jobsString = jedis.get(redisKey);
            if (jobsString == null || jobsString.isEmpty()) {
                LOGGER.warn("Cache miss for Redis key {}", redisKey);
                return; // exit early if the cache is empty
            }
            jobs = objectMapper.readValue(jobsString, new TypeReference<List<Job>>() {});
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing Redis cached jobs to List for key {}: {}", redisKey, e.getMessage());
            throw new RuntimeException("Failed to parse jobs from Redis cache", e);
        }

        // filter out jobs with the specified employerEmail
        List<Job> updatedJobs = jobs.stream()
                .filter(job -> !job.getEmployerEmail().equals(email))
                .collect(Collectors.toList());

        if (updatedJobs.size() == jobs.size()) {
            LOGGER.info("No jobs to remove for employerEmail {} in Redis key {}", email, redisKey);
            return;
        }

        try {
            // replace the old list with the updated one in Redis
            jedis.setex(redisKey, CACHE_TTL_SECONDS, objectMapper.writeValueAsString(updatedJobs));
            LOGGER.info("Successfully updated jobs in Redis cache for key: {}", redisKey);
        } catch (Exception exp) {
            LOGGER.error("Error updating Redis key {}: {}", redisKey, exp.getMessage());
            throw new RuntimeException("Failed to replace Redis cache with updated list", exp);
        }
    }

}
