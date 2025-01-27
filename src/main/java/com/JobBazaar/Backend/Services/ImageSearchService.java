package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Responses.ImageFetchResponse;
import com.JobBazaar.Backend.config.RedisConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

@Service
public class ImageSearchService {

    @Autowired
    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(ImageSearchService.class);

    @Value("${external.api.key}")
    String apiKey;

    @Value("${external.cse.key}")
    String cseKey;

    @Value("${query.queryApi}")
    String queryApi;

    private final RedisConfig redisConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ImageSearchService(RestTemplate restTemplate, RedisConfig redisConfig) {
        this.restTemplate = restTemplate;
        this.redisConfig = redisConfig;
    }

    public ImageFetchResponse searchImage(String query) {
        LOGGER.info("Checking for query results from cache");

        try (Jedis jedis = redisConfig.connect()) {
            if (jedis.exists(query)) {
                LOGGER.info("Cache hit for image fetch based on query");
                String cachedResult = jedis.get(query);
                JsonNode jsonNode = objectMapper.readValue(cachedResult, new TypeReference<>() {
                });
                return new ImageFetchResponse(jsonNode, "Data Fetched Successfully");
            }
        } catch (Exception exp) {
            LOGGER.error("Unable to retrieve query response from cache");
            throw new RuntimeException(exp);
        }

        LOGGER.info("Cache miss, scraping google for the images requested");

        String url = String.format("%sq=%s&cx=%s&key=%s&searchType=image", queryApi, query, cseKey, apiKey);
        JsonNode response = null;
        try {
            response = restTemplate.getForObject(url, JsonNode.class);

            var imageFetchResponse = new ImageFetchResponse(response, "Data Fetched Successfully");
            cacheResponse(imageFetchResponse, query); //cache the image response

            return imageFetchResponse;
        } catch (Exception e) {
            return new ImageFetchResponse(response, "Image couldn't be fetched");
        }
    }

    public void cacheResponse(ImageFetchResponse imageFetchResponse, String redisKey) {
        LOGGER.info("Caching query for image");

        try (Jedis jedis = redisConfig.connect()) {
            String jsonData = objectMapper.writeValueAsString(imageFetchResponse.getData());
            int CACHE_TTL_SECONDS = 21600;
            jedis.setex(redisKey, CACHE_TTL_SECONDS, jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
