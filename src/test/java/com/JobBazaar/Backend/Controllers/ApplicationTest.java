package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Dto.UpdateApplicationStatusRequest;
import com.JobBazaar.Backend.Services.ApplicationService;
import com.JobBazaar.Backend.Services.EmailService;
import com.JobBazaar.Backend.Services.FilesUploadService;
import com.mailjet.client.resource.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {
    @Mock
    ApplicationService applicationService;

    @Mock
    FilesUploadService filesUploadService;

    @Mock
    EmailService emailService;

    @InjectMocks
    Application application;

    @Test
    void addApplication_Succeeds() throws IOException {
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setResumeName("Test.pdf");
        applicationDto.setEmployerEmail("test@test.com");
        applicationDto.setIsActive("true");

        MockMultipartFile resumeFile = new MockMultipartFile("resumeFile", new byte[]{});
        MockMultipartFile additionalDocFile = new MockMultipartFile("additionalDocFile", new byte[]{});

        Map<String, Map<String, String>> stringMapHashMap = new HashMap<>();
        Map<String, String> resumeMap = new HashMap<>();
        resumeMap.put("resumeS3Key", "resumeTestKey");
        resumeMap.put("resumeEtag", "resumeTestTag");

        Map<String, String> additionalDocMap = new HashMap<>();
        additionalDocMap.put("additionalDocS3Key", "additionalDocTestKey");
        additionalDocMap.put("additionalDocEtag", "additionalDocTestTag");

        stringMapHashMap.put("resume", resumeMap);
        stringMapHashMap.put("additionalDoc", additionalDocMap);

        when(filesUploadService.uploadFile(anyMap(), anyList())).thenReturn(stringMapHashMap);
        when(applicationService.addApplication(any(ApplicationDto.class), anyMap())).thenReturn(true);

        ResponseEntity<String> response = application.addApplication(applicationDto, resumeFile, additionalDocFile);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Application added"));
    }

    @Test
    void addApplication_AdditionalDocFile_Is_NULL() throws IOException {
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setResumeName("Test.pdf");
        applicationDto.setEmployerEmail("test@test.com");
        applicationDto.setIsActive("true");

        MockMultipartFile resumeFile = new MockMultipartFile("resumeFile", new byte[]{});

        Map<String, Map<String, String>> stringMapHashMap = new HashMap<>();
        Map<String, String> resumeMap = new HashMap<>();
        resumeMap.put("resumeS3Key", "resumeTestKey");
        resumeMap.put("resumeEtag", "resumeTestTag");

        stringMapHashMap.put("resume", resumeMap);

        when(filesUploadService.uploadFile(anyMap(), anyList())).thenReturn(stringMapHashMap);
        when(applicationService.addApplication(any(ApplicationDto.class), anyMap())).thenReturn(true);

        ResponseEntity<String> response = application.addApplication(applicationDto, resumeFile, null);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Application added"));
    }

    @Test
    void addApplication_Fails() throws IOException {
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setResumeName("Test.pdf");
        applicationDto.setEmployerEmail("test@test.com");
        applicationDto.setIsActive("true");

        MockMultipartFile resumeFile = new MockMultipartFile("resumeFile", new byte[]{});
        MockMultipartFile additionalDocFile = new MockMultipartFile("additionalDocFile", new byte[]{});

        Map<String, Map<String, String>> stringMapHashMap = new HashMap<>();
        Map<String, String> resumeMap = new HashMap<>();
        resumeMap.put("resumeS3Key", "resumeTestKey");
        resumeMap.put("resumeEtag", "resumeTestTag");

        Map<String, String> additionalDocMap = new HashMap<>();
        additionalDocMap.put("additionalDocS3Key", "additionalDocTestKey");
        additionalDocMap.put("additionalDocEtag", "additionalDocTestTag");

        stringMapHashMap.put("resume", resumeMap);
        stringMapHashMap.put("additionalDoc", additionalDocMap);

        when(filesUploadService.uploadFile(anyMap(), anyList())).thenReturn(stringMapHashMap);
        when(applicationService.addApplication(any(ApplicationDto.class), anyMap())).thenReturn(false);

        ResponseEntity<String> response = application.addApplication(applicationDto, resumeFile, additionalDocFile);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Something went "));
    }

    @Test
    void getsJobsAppliedTo_Not_Empty() {
        String applicantEmail = "test@test.com";

        List<Map<String, String>> jobsAppliedTo = new ArrayList<>();

        Map<String, String> application1 = new HashMap<>();
        application1.put("applicantEmail", "test@test.com");
        application1.put("applicationStatus", "InReview");
        application1.put("isActive", "true");

        Map<String, String> application2 = new HashMap<>();
        application2.put("applicantEmail", "test@test.com");
        application2.put("applicationStatus", "Rejected");
        application2.put("isActive", "false");

        jobsAppliedTo.add(application1);
        jobsAppliedTo.add(application2);

        when(applicationService.getJobsAppliedTo(anyString())).thenReturn(jobsAppliedTo);
        ResponseEntity<List<Map<String, String>>> response = application.getJobsAppliedTo(applicantEmail);

        assertNotNull(response);
        List<Map<String, String>> jobs = response.getBody();

        assertNotNull(jobs);
        Map<String, String> job1 = jobs.get(0);
        Map<String, String> job2 = jobs.get(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("test@test.com", job1.get("applicantEmail"));
        assertEquals("false", job2.get("isActive"));
    }

    @Test
    void getsJobsAppliedTo_Empty() {
        String applicantEmail = "test@test.com";

        when(applicationService.getJobsAppliedTo(anyString())).thenReturn(new ArrayList<>());
        ResponseEntity<List<Map<String, String>>> response = application.getJobsAppliedTo(applicantEmail);

        assertNotNull(response);
        List<Map<String, String>> jobs = response.getBody();

        assert jobs != null;
        assertTrue(jobs.isEmpty());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void checkIfApplied() {
        String applicantEmail = "test@test.com";
        String jobId = "2024eject";

        when(applicationService.hasApplied(anyString(), anyString())).thenReturn(true);
        boolean hasApplied = application.checkIfApplied(applicantEmail, jobId);

        assertTrue(hasApplied);
    }

    @Test
    void deleteApplication() {
        String applicantEmail = "test@test.com";
        String jobId = "2024eject";

        when(applicationService.deleteApplication(anyString(), anyString())).thenReturn(false);
        boolean deletedApplication = application.deleteApplication(applicantEmail, jobId);

        assertFalse(deletedApplication);
    }

    @Test
    void getJobsAppliedToUsers_Not_Empty() {
        String jobId = "2024eject";

        List<Map<String, Object>> jobsAppliedTo = new ArrayList<>();

        Map<String, Object> job1 = new HashMap<>();
        job1.put("applicantEmail", "test@test.com");
        job1.put("applicationStatus", "InReview");
        job1.put("isActive", "true");

        Map<String, Object> job2 = new HashMap<>();
        job2.put("applicantEmail", "test@test.com");
        job2.put("applicationStatus", "Rejected");
        job2.put("isActive", "false");

        jobsAppliedTo.add(job1);
        jobsAppliedTo.add(job2);

        when(applicationService.getJobsAppliedToUsers(anyString())).thenReturn(jobsAppliedTo);

        List<Map<String, Object>> list = application.getJobsAppliedToUsers(jobId);
        assertNotNull(list);

        Map<String, Object> j1 = list.get(0);
        Map<String, Object> j2 = list.get(1);

        assertEquals("true", j1.get("isActive"));
        assertEquals("Rejected", j2.get("applicationStatus"));
    }

    @Test
    void getJobsAppliedToUsers_Empty() {
        String jobId = "2024eject";

        when(applicationService.getJobsAppliedToUsers(anyString())).thenReturn(new ArrayList<>());
        List<Map<String, Object>> list = application.getJobsAppliedToUsers(jobId);

        assertTrue(list.isEmpty());
    }

    @Test
    void updateApplicationStatus_Updates() {
        String applicantEmail = "test@test.com";
        String jobId = "2024assign";
        UpdateApplicationStatusRequest statusRequest = new UpdateApplicationStatusRequest();

        when(applicationService.updateApplicationStatus(anyString(), anyString(), any(UpdateApplicationStatusRequest.class))).thenReturn(true);

        ResponseEntity<String> response = application.updateApplicationStatus(applicantEmail, jobId, statusRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void updateApplicationStatus_Update_Fails() {
        String applicantEmail = "test@test.com";
        String jobId = "2024assign";
        UpdateApplicationStatusRequest statusRequest = new UpdateApplicationStatusRequest();

        when(applicationService.updateApplicationStatus(anyString(), anyString(), any(UpdateApplicationStatusRequest.class))).thenReturn(false);

        ResponseEntity<String> response = application.updateApplicationStatus(applicantEmail, jobId, statusRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
    }
}