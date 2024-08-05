package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.JobPostRequest;
import com.JobBazaar.Backend.Services.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class Jobs {
    private static Logger LOGGER = LoggerFactory.getLogger(Login.class);

    private final JobService jobService;

    @Autowired
    public Jobs(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/")
    public ResponseEntity<String> createJob(@RequestBody JobPostRequest jobPostRequest) {
        LOGGER.info("Create job request received");

        boolean jobCreated = jobService.createJob(jobPostRequest);

        if (jobCreated) {
            return new ResponseEntity<>("Job created successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Couldn't create job", HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/")
    public ResponseEntity<List<Map<String, String>>> getAvailableJobs(){
        LOGGER.info("Received request to retrieve all available jobs");
        List<Map<String, String>> availableJobs = new ArrayList<>();

        List<Map<String, AttributeValue>> jobsMap = jobService.getAvailableJobs();
        if(jobsMap != null && !jobsMap.isEmpty()){
            for(Map<String, AttributeValue> jobsAttributes : jobsMap){
                Map<String, String> jobMap = new HashMap<>();
                for (Map.Entry<String, AttributeValue> entry : jobsAttributes.entrySet()) {
                    jobMap.put(entry.getKey(), entry.getValue().s());
                }
                availableJobs.add(jobMap);
            }
            return ResponseEntity.ok(availableJobs);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/employer/{employerEmail}")
    public ResponseEntity<List<Map<String, String>>> getUploadedJobsByEmployerEmail(@PathVariable String employerEmail){
        LOGGER.info("Received request to get uploaded jobs by employer with email {}", employerEmail);
        List<Map<String, String>> jobsByEmployer = new ArrayList<>();

        List<Map<String, AttributeValue>> jobsMap = jobService.getJobsByEmployerEmail(employerEmail);

        if (jobsMap != null && !jobsMap.isEmpty()) {
            for (Map<String, AttributeValue> jobAttributes : jobsMap) {
                Map<String, String> jobMap = new HashMap<>();
                for (Map.Entry<String, AttributeValue> entry : jobAttributes.entrySet()) {
                    jobMap.put(entry.getKey(), entry.getValue().s());
                }
                jobsByEmployer.add(jobMap);
        }
            return ResponseEntity.ok(jobsByEmployer);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{employerEmail}/{jobId}")
    public ResponseEntity<Map<String, String>> getJobsById(@PathVariable String employerEmail, @PathVariable String jobId){
        LOGGER.info("Received request to fetch job by id: {}", jobId);

        Map<String, String> map = new HashMap<>();

        Map<String, AttributeValue> jobsMap = jobService.getJobsById(employerEmail, jobId);
        if(jobsMap != null && !jobsMap.isEmpty()) {
            for (Map.Entry<String, AttributeValue> entry : jobsMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().s();
                map.put(key, value);
            }
            return ResponseEntity.ok(map);
        }
        return ResponseEntity.badRequest().build();
    }

}
