package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.Services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonTest {
    @Mock
    UserService userService;

    @InjectMocks
    Person person;

    @Test
    void getPersonSucceeds() {
        String email = "test@test.com";

        UserDto userDto = new UserDto();
        userDto.setFirstName("Test");
        userDto.setLastName("Com");
        userDto.setEmail("test@test.com");
        userDto.setRole("Employer");
        userDto.setCreatedAt("01-01-2024");

        when(userService.getUsersInfo(anyString())).thenReturn(userDto);

        ResponseEntity<UserDto> response = person.getPerson(email);
        UserDto userDtoResponse = response.getBody();

        assertNotNull(userDtoResponse);
        assertEquals("test@test.com", userDtoResponse.getEmail());
        assertEquals("Employer", userDtoResponse.getRole());
        verify(userService, times(1)).getUsersInfo(anyString());
    }

    @Test
    void getPersonFails() {
        String email = "test@test.com";
        when(userService.getUsersInfo(anyString())).thenReturn(null);

        ResponseEntity<UserDto> response = person.getPerson(email);
        UserDto userDto = response.getBody();

        assertNull(userDto);
        assertEquals(404, response.getStatusCode().value());
    }
}