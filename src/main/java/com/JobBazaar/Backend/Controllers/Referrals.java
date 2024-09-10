package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.ReferralDto;
import com.JobBazaar.Backend.Services.FilesUploadService;
import com.JobBazaar.Backend.Services.ReferralsService;
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
@RequestMapping("/api/referrals")
public class Referrals {
    Logger LOGGER = LoggerFactory.getLogger(Referrals.class.getName());

    private final ReferralsService referralsService;
    private final FilesUploadService filesUploadService;

    @Autowired
    public Referrals(ReferralsService referralsService, FilesUploadService filesUploadService) {
        this.referralsService = referralsService;
        this.filesUploadService = filesUploadService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addReferral(@ModelAttribute ReferralDto referralDto,
                                              @RequestPart("refereeResumeFile") MultipartFile file) throws IOException {
        LOGGER.info("Received request to add referral");

        List<String> fileNames = new ArrayList<>();
        fileNames.add(file.getOriginalFilename());

        Map<String, byte[]> map = new HashMap<>();
        map.put("refereeResumeFile", file.getBytes());

        Map<String, Map<String, String>> fileUploadedToS3Info = filesUploadService.uploadFile(map, fileNames);
        boolean referralAdded = referralsService.addReferral(referralDto, fileUploadedToS3Info);

        if (referralAdded) {
            return new ResponseEntity<>("Referral details added successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Something went wrong, please try again!!!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/get-referrals/{referrerEmail}")
    public ResponseEntity<List<Map<String, Object>>> getReferrals(@PathVariable("referrerEmail") String referrerEmail) {
        LOGGER.info("Received request to retrieve referrals for {}", referrerEmail);
        List<Map<String, Object>> referrals = referralsService.retrieveReferrals(referrerEmail);

        if (!referrals.isEmpty()) {
            return ResponseEntity.ok(referrals);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<Map<String, Object>>> getAllReferrals() {
        LOGGER.info("Received request to get all available referrals");
        List<Map<String, Object>> referrals = referralsService.getAllReferrals();
        if (!referrals.isEmpty()) {
            return ResponseEntity.ok(referrals);
        }
        return ResponseEntity.notFound().build();
    }
}
