package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.WorkDto;
import com.JobBazaar.Backend.Services.WorkService;
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
class WorkTest {
    @Mock
    WorkService workService;

    @InjectMocks
    Work work;

    @Test
    void saveWorkExperience_Successful() throws ParseException, JsonProcessingException {
        when(workService.saveWorkExperience(any(WorkDto.class))).thenReturn(true);
        ResponseEntity<String> response = work.saveWorkExperience(new WorkDto());

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Work experience was saved"));
    }

    @Test
    void saveWorkExperience_Failed() throws ParseException, JsonProcessingException {
        when(workService.saveWorkExperience(any(WorkDto.class))).thenReturn(false);
        ResponseEntity<String> response = work.saveWorkExperience(new WorkDto());

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Work experience could not"));
    }

    @Test
    void getWorkExperience_NotNull() {
        String email = "test@test.com";
        WorkDto workDto = new WorkDto();
        workDto.setEmail("test@test.com");
        workDto.setTittle("CEO");

        when(workService.getWorkExperience(anyString())).thenReturn(workDto);
        ResponseEntity<WorkDto> response = work.getWorkExperience(email);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getWorkExperience_Null() {
        String email = "test@test.com";

        when(workService.getWorkExperience(anyString())).thenReturn(null);
        ResponseEntity<WorkDto> response = work.getWorkExperience(email);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void updateWorkExperience_Updated() throws ParseException, JsonProcessingException {
        when(workService.updateWorkExperience(any(WorkDto.class))).thenReturn(true);
        ResponseEntity<String> response = work.updateWorkExperience(new WorkDto());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Work experience was updated"));
    }

    @Test
    void updateWorkExperience_NotUpdated() throws ParseException, JsonProcessingException {
        when(workService.updateWorkExperience(any(WorkDto.class))).thenReturn(false);
        ResponseEntity<String> response = work.updateWorkExperience(new WorkDto());

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Work experience could not"));
    }

    @Test
    void deleteWorkExperience_Deletes() {
        String email = "test@test.com";
        when(workService.deleteWorkExperience(anyString())).thenReturn(true);
        ResponseEntity<String> response = work.deleteWorkExperience(email);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Work experience deleted"));
    }

    @Test
    void deleteWorkExperience_Doesnt_Delete() {
        String email = "test@test.com";
        when(workService.deleteWorkExperience(anyString())).thenReturn(false);
        ResponseEntity<String> response = work.deleteWorkExperience(email);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).startsWith("Work experience could"));
    }
}