package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Controllers.Login;
import com.JobBazaar.Backend.Dto.JobPostRequest;
import com.JobBazaar.Backend.Repositories.JobRepository;
import com.JobBazaar.Backend.Utils.ShortUUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

@Service
public class JobService {
    private static Logger LOGGER = LoggerFactory.getLogger(Login.class);
    private final JobRepository jobRepository;
    private final ShortUUIDGenerator shortUUIDGenerator;

    @Autowired
    public JobService(JobRepository jobRepository, ShortUUIDGenerator shortUUIDGenerator) {
        this.jobRepository = jobRepository;
        this.shortUUIDGenerator = shortUUIDGenerator;
    }

    public boolean createJob(JobPostRequest jobPostRequest) {
        System.out.println("Job id " + jobPostRequest.getJobId());
        try {
            if (jobPostRequest.getJobId() == null) {
                String jobId = shortUUIDGenerator.generateShortUUID();
                jobPostRequest.setJobId(jobId);
            }
            return jobRepository.saveJob(jobPostRequest);
        } catch (Exception exp) {
            LOGGER.error("Couldn't create job" + exp);
            return false;
        }
    }
  
    public List<Map<String, String>> getAvailableJobs(){
        return jobRepository.getAvailableJobs();
    }
    public List<Map<String, String>> getJobsByEmployerEmail(String employerEmail) {

        return jobRepository.getJobsByEmployerEmail(employerEmail);
    }

    public Map<String, String> getJobsById(String employerEmail, String jobId) {
        return jobRepository.getJobsById(employerEmail, jobId);
    }
}
