package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.JwtToken.JwtTokenService;
import com.JobBazaar.Backend.Services.EmailService;
import com.JobBazaar.Backend.Services.SnsService;
import com.JobBazaar.Backend.Services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignupTest {
    @Mock
    UserService userService;

    @Mock
    SnsService snsService;

    @Mock
    JwtTokenService jwtTokenService;

    @Mock
    EmailService emailService;

    @InjectMocks
    Signup signup;

    @Test
    void createUserExists() throws IOException {
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setFirstName("Test");
        signupRequestDto.setLastName("Com");
        signupRequestDto.setEmail("test@test.com");
        signupRequestDto.setPassword("Password");
        signupRequestDto.setRole("Applicant");

        when(userService.createUser(any(SignupRequestDto.class))).thenReturn(null);

        ResponseEntity<Object> response = signup.createUser(signupRequestDto);

        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = (Map<String, Object>) response.getBody();

        assertNotNull(responseMap);
        assertEquals(409, response.getStatusCode().value());
        assertNull(responseMap.get("user"));
        assertEquals(responseMap, response.getBody());
        verify(userService, times(1)).createUser(any(SignupRequestDto.class));
    }

    @Test
    void createUserSuccessful() throws IOException {
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setFirstName("Test");
        signupRequestDto.setLastName("Com");
        signupRequestDto.setEmail("test@test.com");
        signupRequestDto.setPassword("Password");
        signupRequestDto.setRole("Applicant");

        UserDto userDto = new UserDto();
        userDto.setFirstName("Test");
        userDto.setLastName("Com");
        userDto.setEmail("test@test.com");
        userDto.setRole("Employer");
        userDto.setCreatedAt("01-01-2024");

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJKb2IgQmF6YWFyIEFQSSIsInN1YiI6Im1vaGFtZWRhbWluMjA0MDgwQGdtYWlsLmNvbSIsInJvbGUiOiJFbXBsb3llciIsImV4cCI6MTcyNjQ4NTM2MywiaWF0IjoxNzI2NDQyMTYzfQ.D2gMU0_O5PbeKPwE0dTyqxdX0X7KwYkOLc2lyHKes_I";

        when(userService.createUser(any(SignupRequestDto.class))).thenReturn(userDto);
        when(jwtTokenService.createJwtToken(any(UserDto.class))).thenReturn(token);
        doNothing().when(snsService).addSubscriberTopic(any(SignupRequestDto.class), anyString());
        doNothing().when(emailService).sendWelcomeEmail(anyString(), anyString());

        ResponseEntity<Object> response = signup.createUser(signupRequestDto);

        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = (Map<String, Object>) response.getBody();

        assertNotNull(responseMap);
        assertEquals(responseMap, response.getBody());
        assertEquals(token, responseMap.get("token"));
        assertEquals(userDto, responseMap.get("user"));
        assertEquals(201, response.getStatusCode().value());
        verify(userService).createUser(any(SignupRequestDto.class));
    }

}