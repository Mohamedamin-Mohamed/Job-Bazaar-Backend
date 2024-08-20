package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Services.ApplicationService;
import com.JobBazaar.Backend.Services.FilesUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications/")
public class Application {
    public static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private final ApplicationService applicationService;
    private final FilesUploadService filesUploadService;

    @Autowired
    public Application(ApplicationService applicationService, FilesUploadService filesUploadService) {
        this.applicationService = applicationService;
        this.filesUploadService = filesUploadService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addApplication(@ModelAttribute ApplicationDto applicationDto,
                                                 @RequestPart("resumeFile") MultipartFile resumeFile,
                                                 @RequestPart(value = "additionalDocFile", required = false) MultipartFile additionalDocFile
    ) throws IOException {
        LOGGER.info("Received request to add a new application");



        List<String> fileNames = new ArrayList<>();
        fileNames.add(resumeFile.getOriginalFilename());

        if(additionalDocFile != null) {
            fileNames.add(additionalDocFile.getOriginalFilename());
        }

        Map<String, byte[]> map = new HashMap<>();
        map.put("resume", resumeFile.getBytes());

        if(additionalDocFile != null) {
            map.put("additionalDoc", additionalDocFile.getBytes());
        }
        Map<String, Map<String, String>> fileUploadedToS3Info = filesUploadService.uploadFile(map, fileNames);
        boolean applicationAdded = applicationService.addApplication(applicationDto, fileUploadedToS3Info);


        if(applicationAdded){
            return new ResponseEntity<>("Application added successfully", HttpStatus.OK);
        }

        return new ResponseEntity<>("Something went wrong, please try again!!!", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/users/{applicantEmail}")
    public ResponseEntity<List<Map<String, String>>> getJobsAppliedTo(@PathVariable final String applicantEmail) {
        LOGGER.info("Received request t retrieve jobs applied to by {}", applicantEmail);

        List<Map<String, String>> jobsAppliedTo = applicationService.getJobsAppliedTo(applicantEmail);
        if (!jobsAppliedTo.isEmpty()) {
            return ResponseEntity.ok(jobsAppliedTo);
        }

        return new ResponseEntity<>(jobsAppliedTo, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/status")
    public boolean checkIfApplied(@RequestParam("applicantEmail") String applicantEmail, @RequestParam String jobId) {
        LOGGER.info("Received request to check if {} has applied to job with id {}", applicantEmail, jobId);
        return applicationService.hasApplied(applicantEmail, jobId);
    }

    @DeleteMapping("/delete/{applicantEmail}/{jobId}")
    public boolean deleteApplication(@PathVariable String applicantEmail, @PathVariable String jobId) {
        LOGGER.info("Received request to delete {} application with id {}", applicantEmail, jobId);
        return applicationService.deleteApplication(applicantEmail, jobId);
    }
}
