package com.JobBazaar.Backend.Repositories;

import com.JobBazaar.Backend.Dto.Job;
import com.JobBazaar.Backend.Dto.UpdateJobStatusRequest;
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
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Repository
public class JobRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobRepository.class);
    private final String JOBS = "Jobs";

    private final DynamoDbClient client;
    private final DynamoDbItemMapper dynamoDbItemMapper;

    private final RedisConfig redisConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final int CACHE_TTL_SECONDS = 3600;

    @Autowired
    public JobRepository(DynamoDbClient client, DynamoDbItemMapper dynamoDbItemMapper, RedisConfig redisConfig) {
        this.client = client;
        this.dynamoDbItemMapper = dynamoDbItemMapper;
        this.redisConfig = redisConfig;
    }

    public boolean saveJob(Job job) {
        LOGGER.info("Saving job with employer email {}", job.getEmployerEmail());

        Map<String, AttributeValue> item = dynamoDbItemMapper.toDynamoDbItemMap(job);
        /* since sometimes we are uploading an updated job, we also need to update our cache, meaning replace the object
        of the previous job with the new updated job */

        PutItemRequest putItemRequest = PutItemRequest.builder().item(item).tableName(JOBS).build();
        try {
            PutItemResponse putItemResponse = client.putItem(putItemRequest);
            LOGGER.info("Job saved successfully");

            // now get the formatted json representation of the job
            try (Jedis jedis = redisConfig.connect()) {
                String updatedJobJson = objectMapper.writeValueAsString(job);
                String redisKey = "jobs:all";
                String employerKey = redisKey + ":" + job.getEmployerEmail();

                LOGGER.info("Updating the cache for redis keys {}, {}", redisKey, employerKey);

                updateJobInRedisList(jedis, redisKey, job.getJobId(), updatedJobJson);
                updateJobInRedisList(jedis, employerKey, job.getJobId(), updatedJobJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return putItemResponse.sdkHttpResponse().isSuccessful();
        } catch (Exception exp) {
            LOGGER.error("Error serializing job data: {}", exp.getMessage());
            throw new RuntimeException("Failed to save job", exp);
        }
    }

    public void updateJobInRedisList(Jedis jedis, String redisKey, String jobId, String updatedJsonData) {
        List<Job> jobs;
        try {
            // fetch the cached job list from Redis
            String jobsString = jedis.get(redisKey);
            if (jobsString == null || jobsString.isEmpty()) {
                LOGGER.warn("Cache miss for Redis key {}", redisKey);
                jobs = new ArrayList<>(); // initialize if the list doesn't exist
            } else {
                jobs = objectMapper.readValue(jobsString, new TypeReference<>() {});
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing Redis cached jobs to List: {}", e.getMessage());
            throw new RuntimeException("Failed to parse jobs from Redis cache", e);
        }

        List<Job> updatedJobs = new ArrayList<>();
        boolean jobUpdated = false;

        // update the job in the list
        for (Job job : jobs) {
            if (job.getJobId().equals(jobId)) {
                try {
                    // replace with the updated job data
                    Job updatedJob = objectMapper.readValue(updatedJsonData, Job.class);
                    updatedJobs.add(updatedJob);
                    jobUpdated = true;
                } catch (JsonProcessingException exp) {
                    LOGGER.error("Error processing job during Redis update: {}", exp.getMessage());
                    throw new RuntimeException("Failed to update job in Redis cache", exp);
                }
            } else {
                updatedJobs.add(job);
            }
        }

        // add a new job if it wasn't found in the list
        if (!jobUpdated) {
            try {
                Job newJob = objectMapper.readValue(updatedJsonData, Job.class);
                updatedJobs.add(newJob);
            } catch (JsonProcessingException exp) {
                LOGGER.error("Error adding new job during Redis update: {}", exp.getMessage());
                throw new RuntimeException("Failed to add new job to Redis cache", exp);
            }
        }

        try {
            // replace the old list with the updated one in Redis
            jedis.setex(redisKey, CACHE_TTL_SECONDS, objectMapper.writeValueAsString(updatedJobs));
            LOGGER.info("Successfully updated job in Redis cache for key: {}", redisKey);
        } catch (Exception exp) {
            LOGGER.error("Error updating Redis key {}: {}", redisKey, exp.getMessage());
            throw new RuntimeException("Failed to replace Redis cache with updated list", exp);
        }
    }


    public List<Map<String, String>> getAvailableJobs() {
        LOGGER.info("Checking if available jobs exist in cache");

        String redisKey = "jobs:all";
        try (Jedis jedis = redisConfig.connect()) {
            // Check if the data is in Redis
            if (jedis.exists(redisKey)) {
                LOGGER.info("Cache hit for available jobs");
                String cachedData = jedis.get(redisKey);
                return objectMapper.readValue(cachedData, new TypeReference<>() {
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error retrieving available jobs from cache: {}", e.getMessage());
        }

        // fetch from DynamoDB if not in cache
        LOGGER.info("Cache miss. Retrieving available jobs from DynamoDB");
        try {
            ScanRequest scanRequest = ScanRequest.builder().tableName(JOBS).build();
            ScanResponse scanResponse = client.scan(scanRequest);

            if (!scanResponse.items().isEmpty()) {
                List<Map<String, String>> availableJobs = dynamoDbItemMapper.toDynamoDbItemMap(scanResponse.items());
                cacheJobs(availableJobs, redisKey); // Cache the result
                return availableJobs;
            } else {
                LOGGER.warn("No available jobs found in DynamoDB");
            }
        } catch (Exception e) {
            LOGGER.error("Error retrieving available jobs from DynamoDB: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve available jobs", e);
        }

        // return empty list if no jobs are found or errors occur
        return new ArrayList<>();
    }

    public List<Map<String, String>> getJobsByEmployerEmail(String employerEmail) {
        LOGGER.info("Checking if jobs uploaded by employer exists in cache");

        String redisKey = "jobs:all:" + employerEmail;
        try (Jedis jedis = redisConfig.connect()) {
            if (jedis.exists(redisKey)) {
                LOGGER.info("Cache hit for uploaded jobs");
                String cachedData = jedis.get(redisKey);
                return objectMapper.readValue(cachedData, new TypeReference<>() {
                });
            }
        } catch (Exception exp) {
            LOGGER.error("Error retrieving available jobs from cache {}", exp.getMessage());
        }

        LOGGER.info("Cache miss, getting jobs uploaded by employer with email {} from DynamoDB:", employerEmail);

        String keyConditionExpression = "employerEmail=:empEmail";
        Map<String, AttributeValue> key = new HashMap<>();
        String attributeValue = ":empEmail";

        key.put(attributeValue, AttributeValue.builder().s(employerEmail).build());

        QueryRequest queryRequest = QueryRequest.builder().keyConditionExpression(keyConditionExpression).
                expressionAttributeValues(key).tableName(JOBS).build();

        try {
            QueryResponse queryResponse = client.query(queryRequest);
            if (!queryResponse.items().isEmpty()) {
                List<Map<String, String>> uploadedJobs = dynamoDbItemMapper.toDynamoDbItemMap(queryResponse.items());
                cacheJobs(uploadedJobs, redisKey);
                return uploadedJobs;
            }
        } catch (Exception exp) {
            LOGGER.error("Couldn't retrieve uploaded jobs {}", exp.toString());
            throw new RuntimeException(exp);
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

    public Map<String, String> getJobsById(String employerEmail, String jobId) {
        LOGGER.info("Retrieving job related to job id {}", jobId);

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("employerEmail", AttributeValue.builder().s(employerEmail).build());
        key.put("jobId", AttributeValue.builder().s(jobId).build());

        GetItemRequest getItemRequest = GetItemRequest.builder().tableName(JOBS).key(key).build();

        try {
            GetItemResponse getItemResponse = client.getItem(getItemRequest);
            LOGGER.info("Retrieved the job with job id {}", jobId);

            if (!getItemResponse.item().isEmpty()) {
                return dynamoDbItemMapper.toDynamoDbItemMap(getItemResponse.item());
            }
            return new HashMap<>();
        } catch (Exception exp) {
            LOGGER.error("Couldn't retrieve the job with id {}", jobId + exp);
            throw exp;
        }
    }

    public Map<String, Integer> countApplicantsByJobIds(List<String> jobIds) {
        LOGGER.info("Retrieving applicants count from cache");

        String redisKey = "jobs:all:applicantByJobIds";
        try (Jedis jedis = redisConfig.connect()) {
            if (jedis.exists(redisKey)) {
                String cachedData = jedis.get(redisKey);
                return objectMapper.readValue(cachedData, new TypeReference<Map<String, Integer>>() {
                });
            }
        } catch (Exception exception) {
            LOGGER.error("Unable to retrieve applicants count per job {}", exception.getMessage());
        }

        LOGGER.info("Cache miss, retrieving applicants count per job from database");

        Map<String, Integer> applicantsCount = new HashMap<>();
        for (String jobId : jobIds) {
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":jobId", AttributeValue.builder().s(jobId).build());

            final String APPLICATIONS = "Applications";
            ScanRequest scanRequest = ScanRequest.builder().tableName(APPLICATIONS).filterExpression("jobId = :jobId")
                    .expressionAttributeValues(expressionValues).build();

            try {
                ScanResponse scanResponse = client.scan(scanRequest);
                int count = scanResponse.count();
                applicantsCount.put(jobId, count);
            } catch (Exception exp) {
                LOGGER.error("Couldn't retrieve applicants count {}", exp.toString());
            }
        }
        cacheApplicantCounts(applicantsCount, redisKey);
        return applicantsCount;
    }

    public void cacheApplicantCounts(Map<String, Integer> applicantCounts, String redisKey) {
        try (Jedis jedis = redisConfig.connect()) {
            String jsonData = objectMapper.writeValueAsString(applicantCounts);
            jedis.setex(redisKey, CACHE_TTL_SECONDS, jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateJob(String employerEmail, String jobId, UpdateJobStatusRequest updateJobStatusRequest) {
        LOGGER.info("Updating job with employer email {} job id {}", employerEmail, jobId);

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("employerEmail", AttributeValue.builder().s(employerEmail).build());
        key.put("jobId", AttributeValue.builder().s(jobId).build());

        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#jobStatus", "jobStatus");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":status", AttributeValue.builder().s(updateJobStatusRequest.getJobStatus()).build());


        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder().
                tableName(JOBS).
                key(key).
                updateExpression("Set #jobStatus = :status").
                expressionAttributeNames(expressionAttributeNames).
                expressionAttributeValues(expressionAttributeValues).build();
        try {
            UpdateItemResponse updateItemResponse = client.updateItem(updateItemRequest);
            return updateItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException exp) {
            LOGGER.error("Couldn't update job with id {}", jobId, exp);
            throw exp;
        } catch (Exception exp) {
            LOGGER.error("Unknown error occurred ", exp);
            throw exp;
        }
    }

    public boolean jobExists(String employerEmail, String jobId) {
        LOGGER.info("Checking if job with employer email {} and job id {} exists", employerEmail, jobId);

        String keyConditionExpression = "employerEmail = :empEmail and jobId = :jId";
        String filterExpression = "jobStatus = :jStatus";

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":empEmail", AttributeValue.builder().s(employerEmail).build());
        expressionAttributeValues.put(":jId", AttributeValue.builder().s(jobId).build());
        expressionAttributeValues.put(":jStatus", AttributeValue.builder().s("active").build());

        QueryRequest queryRequest = QueryRequest.builder().
                tableName(JOBS).
                expressionAttributeValues(expressionAttributeValues).
                keyConditionExpression(keyConditionExpression).
                filterExpression(filterExpression).build();
        try {
            QueryResponse queryResponse = client.query(queryRequest);
            return !queryResponse.items().isEmpty();
        } catch (DynamoDbException exp) {
            LOGGER.error("Couldn't retrieve job with id {}", jobId);
            throw exp;
        } catch (Exception exp) {
            LOGGER.error("Unknown error occurred", exp);
            throw exp;
        }
    }
}
