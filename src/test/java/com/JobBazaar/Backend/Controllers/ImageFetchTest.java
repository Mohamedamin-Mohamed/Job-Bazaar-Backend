package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Responses.ImageFetchResponse;
import com.JobBazaar.Backend.Services.ImageSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageFetchTest {
    @Mock
    ImageSearchService imageSearchService;

    @InjectMocks
    ImageFetch imageFetch;

    @Test
    void fetchImage_Successful() throws JsonProcessingException {
        String jsonString = "{\"size\": \"200\", \"data\": \"This is just a test\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonString);

        ImageFetchResponse imageFetchStubbing = new ImageFetchResponse(rootNode, "Data Fetched Successfully");
        when(imageSearchService.searchImage(anyString())).thenReturn(imageFetchStubbing);

        String query = "Just a test query";
        ResponseEntity<ImageFetchResponse> response = imageFetch.fetchImage(query);

        assertNotNull(response);
        ImageFetchResponse imageFetchResponse = response.getBody();

        assertNotNull(imageFetchResponse);
        assertTrue(imageFetchResponse.getMessage().startsWith("Data Fetched"));
        assertTrue(imageFetchResponse.getData() instanceof JsonNode);
    }

    @Test
    void fetchImage_Fails() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        ImageFetchResponse imageFetchStubbing = new ImageFetchResponse(jsonNode, "Image couldn't be fetched");
        when(imageSearchService.searchImage(anyString())).thenReturn(imageFetchStubbing);

        String query = "Just a test query";
        ResponseEntity<ImageFetchResponse> response = imageFetch.fetchImage(query);
        assertNotNull(response);

        assertEquals(404, response.getStatusCode().value());
    }
}