package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.FeedbackDto;
import com.JobBazaar.Backend.Services.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class Feedback {
    private final Logger LOGGER = LoggerFactory.getLogger(Feedback.class.getName());
    private final FeedbackService feedbackService;

    @Autowired
    public Feedback(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("provide-feedback/")
    public ResponseEntity<String> addFeedback(@RequestBody FeedbackDto feedbackDto) {
        LOGGER.info("Received request to add feedback");

        boolean feedbackAdded = feedbackService.addFeedBack(feedbackDto);

        if (feedbackAdded) {
            return ResponseEntity.ok("Successfully added feedback");
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("feedbacks/applicant/{applicantEmail}")
    public ResponseEntity<List<FeedbackDto>> getFeedbacks(@PathVariable("applicantEmail") String applicantEmail) {
        LOGGER.info("Received request to get feedbacks for {}", applicantEmail);
        List<FeedbackDto> feedbacks = feedbackService.getFeedbacks(applicantEmail);
        if (!feedbacks.isEmpty()) {
            return ResponseEntity.ok(feedbacks);
        }
        return ResponseEntity.notFound().build();
    }

}
