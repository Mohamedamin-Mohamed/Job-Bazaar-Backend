package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.FeedbackDto;
import com.JobBazaar.Backend.Services.FeedbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackTest {
    @Mock
    FeedbackService feedbackService;

    @InjectMocks
    Feedback feedback;

    @Test
    void addFeedback_Adds() {
        when(feedbackService.addFeedBack(any(FeedbackDto.class))).thenReturn(true);

        ResponseEntity<String> response = feedback.addFeedback(new FeedbackDto());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Successfully"));
    }

    @Test
    void addFeedback_Failss() {
        when(feedbackService.addFeedBack(any(FeedbackDto.class))).thenReturn(false);

        ResponseEntity<String> response = feedback.addFeedback(new FeedbackDto());

        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void getFeedbacks_Not_Empty() {
        String applicantEmail = "test@test.com";
        List<FeedbackDto> feedbackDtoList = new ArrayList<>();
        FeedbackDto feedbackDto1 = new FeedbackDto();
        feedbackDto1.setApplicantEmail("test@test.com");
        feedbackDto1.setJobId("2024eek");
        feedbackDto1.setStatus("InReview");

        FeedbackDto feedbackDto2 = new FeedbackDto();
        feedbackDto2.setApplicantEmail("test@test.com");
        feedbackDto2.setJobId("2024kiddy");
        feedbackDto2.setStatus("Rejected");

        feedbackDtoList.add(feedbackDto1);
        feedbackDtoList.add(feedbackDto2);

        when(feedbackService.getFeedbacks(anyString())).thenReturn(feedbackDtoList);

        ResponseEntity<List<FeedbackDto>> response = feedback.getFeedbacks(applicantEmail);
        assertNotNull(response);

        List<FeedbackDto> list = response.getBody();
        assert list != null;

        FeedbackDto feedback1 = list.get(0);
        FeedbackDto feedback2 = list.get(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("InReview", feedback1.getStatus());
        assertEquals("2024kiddy", feedback2.getJobId());
    }

    @Test
    void getFeedbacks_Empty() {
        String applicantEmail = "test@test.com";

        when(feedbackService.getFeedbacks(anyString())).thenReturn(new ArrayList<>());

        ResponseEntity<List<FeedbackDto>> response = feedback.getFeedbacks(applicantEmail);
        assertNotNull(response);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}