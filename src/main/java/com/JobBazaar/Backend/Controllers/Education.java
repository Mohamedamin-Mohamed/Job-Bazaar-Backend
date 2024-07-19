package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.EducationDto;
import com.JobBazaar.Backend.Services.EducationService;
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
@RequestMapping("/api/user-education/")
public class Education {
    private static final Logger LOGGER = Logger.getLogger(Education.class.getName());
    private EducationService educationService;

    @Autowired
    public Education(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping("/save")
    ResponseEntity<String> saveEducation(@RequestBody EducationDto educationDto) throws ParseException, JsonProcessingException {
        boolean addedEducation = educationService.saveEducation(educationDto);
        if (addedEducation) {
            return new ResponseEntity<>("Education saved succesfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Education could not be saved", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<EducationDto> getEducation(@PathVariable String email) {
        LOGGER.info("Received request to retrieve education");
        System.out.println(email);
        EducationDto educationDto = educationService.getEducation(email);
        if (educationDto != null) {
            return ResponseEntity.ok(educationDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateEducation(@RequestBody EducationDto educationDto) throws ParseException, JsonProcessingException {
        boolean updatedEducation = educationService.updateEducation(educationDto);

        if (updatedEducation) {
            return new ResponseEntity<>("Education updated succesfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Education could not be updated", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteEducation(@PathVariable String email) {
        boolean deletedEducation = educationService.deleteEducation(email);

        if (deletedEducation) {
            return new ResponseEntity<>("Education deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Education could not be deleted", HttpStatus.BAD_REQUEST);
        }
    }
}
