package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.Job;
import com.JobBazaar.Backend.Dto.UpdateJobStatusRequest;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Services.EmailService;
import com.JobBazaar.Backend.Services.JobService;
import com.JobBazaar.Backend.Services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobsTest {
    @Mock
    JobService jobService;

    @Mock
    UserService userService;

    @Mock
    EmailService emailService;

    @InjectMocks
    Jobs jobs;

    @Test
    void createJob_Successful() throws IOException {
        UserDto userDto = new UserDto();
        userDto.setFirstName("Test");
        userDto.setLastName("test");
        userDto.setRole("Employer");

        when(userService.getUsersInfo(null)).thenReturn(userDto);
        when(jobService.createJob(any(Job.class))).thenReturn(true);

        ResponseEntity<String> response = jobs.createJob(new Job());
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Job created"));
    }


    @Test
    void createJob_Fails() throws IOException {
        when(jobService.createJob(any(Job.class))).thenReturn(false);

        ResponseEntity<String> response = jobs.createJob(new Job());
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Couldn't create"));
    }

    @Test
    void getAvailableJobs_Not_Empty() {
        List<Map<String, String>> availableJobsList = getMaps();

        when(jobService.getAvailableJobs()).thenReturn(availableJobsList);

        ResponseEntity<List<Map<String, String>>> response = jobs.getAvailableJobs();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertFalse(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    private static List<Map<String, String>> getMaps() {
        List<Map<String, String>> availableJobsList = new ArrayList<>();

        Map<String, String> job1 = new HashMap<>();
        job1.put("employerEmail", "test1@test.com");
        job1.put("jobId", "2024red");
        job1.put("jobStatus", "active");

        Map<String, String> job2 = new HashMap<>();
        job2.put("employerEmail", "test3@test.com");
        job2.put("jobId", "2024dmed");
        job2.put("jobStatus", "inActive");

        availableJobsList.add(job1);
        availableJobsList.add(job2);
        return availableJobsList;
    }

    @Test
    void getAvailableJobs_Empty() {
        when(jobService.getAvailableJobs()).thenReturn(new ArrayList<>());

        ResponseEntity<List<Map<String, String>>> response = jobs.getAvailableJobs();
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void getUploadedJobsByEmployerEmail_Not_Empty() {
        String employerEmail = "employer@test.com";
        List<Map<String, String>> uploadedjobs = getMaps();

        when(jobService.getJobsByEmployerEmail(anyString())).thenReturn(uploadedjobs);
        ResponseEntity<List<Map<String, String>>> response = jobs.getUploadedJobsByEmployerEmail(employerEmail);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertFalse(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void getUploadedJobsByEmployerEmail_Empty() {
        String employerEmail = "employer@test.com";
        when(jobService.getJobsByEmployerEmail(anyString())).thenReturn(new ArrayList<>());

        ResponseEntity<List<Map<String, String>>> response = jobs.getUploadedJobsByEmployerEmail(employerEmail);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void getJobsById_Not_Empty() {
        String employerEmail = "employer@test.com";
        String jobId = "2024dmed";

        Map<String, String> job = new HashMap<>();
        job.put("employerEmail", "test1@test.com");
        job.put("jobId", "2024red");
        job.put("jobStatus", "active");

        when(jobService.getJobsById(anyString(), anyString())).thenReturn(job);
        ResponseEntity<Map<String, String>> response = jobs.getJobsById(employerEmail, jobId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("active", Objects.requireNonNull(response.getBody()).get("jobStatus"));
    }

    @Test
    void getJobsById_Empty() {
        String employerEmail = "employer@test.com";
        String jobId = "2024dmed";
        when(jobService.getJobsById(anyString(), anyString())).thenReturn(new HashMap<>());
        ResponseEntity<Map<String, String>> response = jobs.getJobsById(employerEmail, jobId);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void countApplicants_Not_Empty() {
        Map<String, Integer> integerMap = new HashMap<>();
        integerMap.put("2024djd", 3);
        integerMap.put("2024dkd", 4);
        integerMap.put("2024eas", 1);

        when(jobService.countApplicantsByJobIds(anyList())).thenReturn(integerMap);
        ResponseEntity<Map<String, Integer>> response = jobs.countApplicants(new ArrayList<>());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(4, Objects.requireNonNull(response.getBody()).get("2024dkd"));
    }

    @Test
    void countApplicants_Empty() {
        when(jobService.countApplicantsByJobIds(anyList())).thenReturn(new HashMap<>());

        ResponseEntity<Map<String, Integer>> response = jobs.countApplicants(new ArrayList<>());
        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void updateJob() {
        String employerEmail = "employer@test.com";
        String jobId = "2024dmed";
        UpdateJobStatusRequest jobStatusRequest = new UpdateJobStatusRequest();

        when(jobService.updateJob(anyString(), anyString(), any(UpdateJobStatusRequest.class))).thenReturn(true);
        boolean updated = jobs.updateJob(employerEmail, jobId, jobStatusRequest);

        assertTrue(updated);
    }

    @Test
    void existsJob() {
        String employerEmail = "employer@test.com";
        String jobId = "2024dmed";

        when(jobService.jobExists(anyString(), anyString())).thenReturn(true);
        boolean exists = jobs.existsJob(employerEmail, jobId);

        assertTrue(exists);
    }

}