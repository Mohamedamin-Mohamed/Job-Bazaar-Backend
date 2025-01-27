package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.Job;
import com.JobBazaar.Backend.Dto.UpdateJobStatusRequest;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Services.EmailService;
import com.JobBazaar.Backend.Services.JobService;
import com.JobBazaar.Backend.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class Jobs {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jobs.class);

    private final JobService jobService;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public Jobs(JobService jobService, UserService userService, EmailService emailService) {
        this.jobService = jobService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> createJob(@RequestBody Job job) throws IOException {
        LOGGER.info("Create job request received");
        boolean isNewJob = (job.getJobId() == null);
        boolean jobCreated = jobService.createJob(job);

        if (jobCreated) {
            UserDto user = userService.getUsersInfo(job.getEmployerEmail());

            String recipientEmail = job.getEmployerEmail();
            String fullName = user.getFirstName() + " " + user.getLastName();

            emailService.sendJobRelatedEmail(recipientEmail, fullName, user.getRole(), isNewJob, job.getPosition());
            return new ResponseEntity<>(isNewJob ? "Job created successfully" : "Job updated successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>(isNewJob ? "Couldn't create job" : "Couldn't update job", HttpStatus.INTERNAL_SERVER_ERROR);
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
