package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Repositories.SnsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnsService {
    private final SnsRepository snsRepository;

    @Autowired
    public SnsService(SnsRepository snsRepository) {
        this.snsRepository = snsRepository;
    }

    public void saveTopicArn(String topic, String arn) {
        snsRepository.saveTopicArn(topic, arn);
    }

    public void addSubscriberTopic(SignupRequestDto signupRequest, String topicName) {
        snsRepository.addSubscriberToTopic(signupRequest, topicName);
    }
}
