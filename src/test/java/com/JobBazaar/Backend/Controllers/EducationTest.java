package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.EducationDto;
import com.JobBazaar.Backend.Services.EducationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EducationTest {
    @Mock
    EducationService educationService;

    @InjectMocks
    Education education;

    @Test
    void saveEducation_Success() throws ParseException, JsonProcessingException {
        when(educationService.saveEducation(any(EducationDto.class))).thenReturn(true);
        ResponseEntity<String> response = education.saveEducation(new EducationDto());

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Education saved"));
    }

    @Test
    void saveEducation_Fails() throws ParseException, JsonProcessingException {
        when(educationService.saveEducation(any(EducationDto.class))).thenReturn(false);
        ResponseEntity<String> response = education.saveEducation(new EducationDto());

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Education could"));
    }

    @Test
    void getEducation_Not_Null() {
        String email = "test@test.com";
        EducationDto educationDto = new EducationDto();
        educationDto.setEmail(email);
        educationDto.setStartDate("01-01-2024");
        when(educationService.getEducation(anyString())).thenReturn(educationDto);

        ResponseEntity<EducationDto> response = education.getEducation(email);
        assertNotNull(response);

        EducationDto responseDto = response.getBody();
        assertNotNull(responseDto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("01-01-2024", responseDto.getStartDate());
    }

    @Test
    void getEducation_Null() {
        String email = "test@test.com";

        when(educationService.getEducation(anyString())).thenReturn(null);
        ResponseEntity<EducationDto> response = education.getEducation(email);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void updateEducation_Updates() throws ParseException, JsonProcessingException {
        when(educationService.updateEducation(any(EducationDto.class))).thenReturn(true);

        ResponseEntity<String> response = education.updateEducation(new EducationDto());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Education updated"));
    }

    @Test
    void updateEducation_Fails() throws ParseException, JsonProcessingException {
        when(educationService.updateEducation(any(EducationDto.class))).thenReturn(false);

        ResponseEntity<String> response = education.updateEducation(new EducationDto());

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Education could not"));
    }

    @Test
    void deleteEducation_Deletes() {
        String email = "2024jejune";
        when(educationService.deleteEducation(anyString())).thenReturn(true);

        ResponseEntity<String> response = education.deleteEducation(email);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Education deleted"));
    }

    @Test
    void deleteEducation_Doesnt_Delete() {
        String email = "2024jejune";
        when(educationService.deleteEducation(anyString())).thenReturn(false);

        ResponseEntity<String> response = education.deleteEducation(email);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Education could not"));
    }

}