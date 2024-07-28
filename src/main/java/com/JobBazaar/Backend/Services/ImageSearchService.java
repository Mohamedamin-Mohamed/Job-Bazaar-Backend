package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Responses.ImageFetchResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Service
public class ImageSearchService {

    @Autowired
    private final RestTemplate restTemplate;

    private final Logger LOGGER = Logger.getLogger(ImageSearchService.class.getName());

    @Value("${external.api.key}")
    String apiKey;

    @Value("${external.cse.key}")
    String cseKey;

    public ImageSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ImageFetchResponse searchImage(String query) {
        LOGGER.info("Scraping google for the images requested");
        String url = String.format("https://www.googleapis.com/customsearch/v1?q=%s&cx=%s&key=%s&searchType=image", query, cseKey, apiKey);
        JsonNode response = null;
        try {
            response = restTemplate.getForObject(url, JsonNode.class);
            return new ImageFetchResponse(response, "Data Fetched Successfully");
        } catch (Exception e) {
            return new ImageFetchResponse(response, "Image couldn't be fetched");
        }
    }
}
