package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.EducationDto;
import com.JobBazaar.Backend.Services.EducationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/user-education")
public class Education {
    private static final Logger LOGGER = Logger.getLogger(Education.class.getName());
    private final EducationService educationService;

    @Autowired
    public Education(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping("/save")
    ResponseEntity<String> saveEducation(@RequestBody EducationDto educationDto) throws ParseException, JsonProcessingException {
        boolean addedEducation = educationService.saveEducation(educationDto);
        if (addedEducation) {
            return new ResponseEntity<>("Education saved successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Education could not be saved", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<EducationDto> getEducation(@PathVariable String email) {
        LOGGER.info("Received request to retrieve education");
        EducationDto educationDto = educationService.getEducation(email);

        if (educationDto != null) {
            return ResponseEntity.ok(educationDto);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateEducation(@RequestBody EducationDto educationDto) throws ParseException, JsonProcessingException {
        LOGGER.info("Received request to update education");
        boolean educationUpdated = educationService.updateEducation(educationDto);

        if (educationUpdated) {
            return new ResponseEntity<>("Education updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Education could not be updated", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteEducation(@PathVariable String email) {
        boolean deletedEducation = educationService.deleteEducation(email);

        if (deletedEducation) {
            return new ResponseEntity<>("Education deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Education could not be deleted", HttpStatus.BAD_REQUEST);
    }
}
