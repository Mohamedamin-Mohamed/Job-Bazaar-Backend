package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.WorkDto;
import com.JobBazaar.Backend.Services.WorkService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/work-experience")
public class Work {
    private static final Logger LOGGER = Logger.getLogger(Work.class.getName());
    private final WorkService workService;

    @Autowired
    public Work(WorkService workService) {
        this.workService = workService;
    }

    @PostMapping("/save")
    ResponseEntity<String> saveWorkExperience(@RequestBody WorkDto workDto) throws ParseException, JsonProcessingException {
        boolean addedWorkExperience = workService.saveWorkExperience(workDto);
        if (addedWorkExperience) {
            return new ResponseEntity<>("Work experience was saved successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Work experience could not be saved", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<WorkDto> getWorkExperience(@PathVariable String email) {
        LOGGER.info("Received request to retrieve work experience");
        System.out.println(email);
        WorkDto workDto = workService.getWorkExperience(email);
        if (workDto != null) {
            return ResponseEntity.ok(workDto);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/update")
    ResponseEntity<String> updateWorkExperience(@RequestBody WorkDto workDto) throws ParseException, JsonProcessingException {
        boolean workUpdated = workService.updateWorkExperience(workDto);

        if (workUpdated) {
            return new ResponseEntity<>("Work experience was updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Work experience could not be updated", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteWorkExperience(@PathVariable String email) {
        boolean deletedWorkExperience = workService.deleteWorkExperience(email);

        if (deletedWorkExperience) {
            return new ResponseEntity<>("Work experience deleted successfully", HttpStatus.OK);
        }

        return new ResponseEntity<>("Work experience could not be deleted", HttpStatus.BAD_REQUEST);
    }
}
