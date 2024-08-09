package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.ApplicationDto;
import com.JobBazaar.Backend.Services.ApplicationService;
import com.JobBazaar.Backend.Services.FilesUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        return ResponseEntity.ok("Hey there");
    }
}
