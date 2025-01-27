package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Responses.ImageFetchResponse;
import com.JobBazaar.Backend.Services.ImageSearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/fetch-image")
public class ImageFetch {

    @Value("${external.api.key}")
    String apiKey;

    @Value("${external.cse.key}")
    String cseKey;

    private final ImageSearchService imageSearchService;
    private final Logger LOGGER = Logger.getLogger(ImageFetch.class.getName());

    public ImageFetch(ImageSearchService imageSearchService) {
        this.imageSearchService = imageSearchService;
    }

    @GetMapping("/imageUrl")
    public ResponseEntity<ImageFetchResponse> fetchImage(@RequestParam String query) {
        LOGGER.info("Received request to search image based on the query");
        ImageFetchResponse imageFetchResponse = imageSearchService.searchImage(query);
        if (imageFetchResponse.getData() != null) {
            return ResponseEntity.ok(imageFetchResponse);
        }
        return ResponseEntity.notFound().build();
    }
}
