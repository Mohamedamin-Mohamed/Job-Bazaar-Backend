package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Dto.JobPostRequest;
import com.JobBazaar.Backend.Repositories.JobRepository;
import com.JobBazaar.Backend.Utils.ShortUUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ShortUUIDGenerator shortUUIDGenerator;

    @Autowired
    public JobService(JobRepository jobRepository, ShortUUIDGenerator shortUUIDGenerator){
        this.jobRepository = jobRepository;
        this.shortUUIDGenerator = shortUUIDGenerator;
    }

    public boolean createJob(JobPostRequest jobPostRequest){
        if(jobPostRequest.getJobId().isEmpty()) {
            String jobId = shortUUIDGenerator.generateShortUUID();
            jobPostRequest.setJobId(jobId);
        }
        return jobRepository.saveJob(jobPostRequest);
    }

    public List<Map<String, AttributeValue>> getJobsByEmployerEmail(String employerEmail){
        return jobRepository.getJobsByEmployerEmail(employerEmail);
    }

    public Map<String, AttributeValue> getJobsById(String employerEmail, String jobId){
        return jobRepository.getJobsById(employerEmail, jobId);
    }
}
