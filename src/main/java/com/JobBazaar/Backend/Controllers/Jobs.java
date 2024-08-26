package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.JobPostRequest;
import com.JobBazaar.Backend.Dto.UpdateJobStatusRequest;
import com.JobBazaar.Backend.Services.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class Jobs {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jobs.class);

    private final JobService jobService;

    @Autowired
    public Jobs(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> createJob(@RequestBody JobPostRequest jobPostRequest) {
        LOGGER.info("Create job request received");

        boolean jobCreated = jobService.createJob(jobPostRequest);

        if (jobCreated) {
            return new ResponseEntity<>("Job created successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Couldn't create job", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/")
    public ResponseEntity<List<Map<String, String>>> getAvailableJobs() {
        LOGGER.info("Received request to retrieve all available jobs");

        List<Map<String, String>> availableJobs = jobService.getAvailableJobs();

        if (!availableJobs.isEmpty()) {
            return ResponseEntity.ok(availableJobs);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/employer/{employerEmail}")
    public ResponseEntity<List<Map<String, String>>> getUploadedJobsByEmployerEmail(@PathVariable String employerEmail) {
        LOGGER.info("Received request to get uploaded jobs by employer with email {}", employerEmail);
        List<Map<String, String>> jobsByEmployer = jobService.getJobsByEmployerEmail(employerEmail);

        if (!jobsByEmployer.isEmpty()) {

            return ResponseEntity.ok(jobsByEmployer);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{employerEmail}/{jobId}")
    public ResponseEntity<Map<String, String>> getJobsById(@PathVariable String employerEmail, @PathVariable String jobId) {
        LOGGER.info("Received request to fetch job by id: {}", jobId);

        Map<String, String> jobsMap = jobService.getJobsById(employerEmail, jobId);

        if (!jobsMap.isEmpty()) {
            return ResponseEntity.ok(jobsMap);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/applicants-count")
    public ResponseEntity<Map<String, Integer>> countApplicants(@RequestBody List<String> jobIds) {
        LOGGER.info("Received request to get applicants count");

        Map<String, Integer> applicantsCount = jobService.countApplicantsByJobIds(jobIds);

        if (!applicantsCount.isEmpty()) {
            return ResponseEntity.ok(applicantsCount);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/update/{employerEmail}/{jobId}")
    public boolean updateJob(@PathVariable String employerEmail,
                             @PathVariable String jobId,
                             @RequestBody UpdateJobStatusRequest updateJobStatusRequest) {
        LOGGER.info("Received request to update {} job of id: {}", employerEmail, jobId);
        return jobService.updateJob(employerEmail, jobId, updateJobStatusRequest);
    }

    @GetMapping("/exists/{employerEmail}/{jobId}")
    public boolean existsJob(@PathVariable String employerEmail, @PathVariable String jobId) {
        LOGGER.info("Received request to check if job exists: {}", jobId);
        return jobService.jobExists(employerEmail, jobId);
    }
}
