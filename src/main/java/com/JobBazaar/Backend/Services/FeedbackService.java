package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.FeedbackDto;
import com.JobBazaar.Backend.Repositories.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public boolean addFeedBack(FeedbackDto feedbackDto) {
        return feedbackRepository.addFeedBack(feedbackDto);
    }

    public List<FeedbackDto> getFeedbacks(String applicantEmail){
        return feedbackRepository.getFeedbacks(applicantEmail);
    }

}
