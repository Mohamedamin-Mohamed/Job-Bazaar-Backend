package com.JobBazaar.Backend.Services;

import com.JobBazaar.Backend.Controllers.Login;
import com.JobBazaar.Backend.Dto.Job;
import com.JobBazaar.Backend.Dto.UpdateJobStatusRequest;
import com.JobBazaar.Backend.Repositories.JobRepository;
import com.JobBazaar.Backend.Utils.ShortUUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JobService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);
    private final JobRepository jobRepository;
    private final ShortUUIDGenerator shortUUIDGenerator;

    @Autowired
    public JobService(JobRepository jobRepository, ShortUUIDGenerator shortUUIDGenerator) {
        this.jobRepository = jobRepository;
        this.shortUUIDGenerator = shortUUIDGenerator;
    }

    public boolean createJob(Job job) {
        try {
            if (job.getJobId() == null) {
                String jobId = shortUUIDGenerator.generateShortUUID();
                job.setJobId(jobId);
            }
            return jobRepository.saveJob(job);
        } catch (Exception exp) {
            LOGGER.error("Could not create job", exp);
            return false;
        }
    }

    public List<Map<String, String>> getAvailableJobs() {
        return jobRepository.getAvailableJobs();
    }

    public List<Map<String, String>> getJobsByEmployerEmail(String employerEmail) {

        return jobRepository.getJobsByEmployerEmail(employerEmail);
    }

    public Map<String, String> getJobsById(String employerEmail, String jobId) {
        return jobRepository.getJobsById(employerEmail, jobId);
    }

    public Map<String, Integer> countApplicantsByJobIds(List<String> jobIds) {
        return jobRepository.countApplicantsByJobIds(jobIds);
    }

    public boolean updateJob(String employerEmail, String jobId, UpdateJobStatusRequest updateJobStatusRequest) {
        return jobRepository.updateJob(employerEmail, jobId, updateJobStatusRequest);
    }

    public boolean jobExists(String employerEmail, String jobId) {
        return jobRepository.jobExists(employerEmail, jobId);
    }
}
