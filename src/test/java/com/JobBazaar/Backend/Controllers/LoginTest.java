package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.PasswordResetDto;
import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Dto.UserDto;
import com.JobBazaar.Backend.JwtToken.JwtTokenService;
import com.JobBazaar.Backend.Services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginTest {
    @Mock
    UserService userService;

    @Mock
    JwtTokenService jwtTokenService;

    @InjectMocks
    Login login;

    @Test
    void checkCredentialsValid() {
        RequestDto requestDto = new RequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPass("password");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setFirstName("Test");
        userDto.setLastName("Com");
        userDto.setRole("Employer");
        userDto.setCreatedAt("01-01-2024");

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJKb2IgQmF6YWFyIEFQSSIsInN1YiI6Im1vaGFtZWRhbWluMjA0MDgwQGdtYWlsLmNvbSIsInJvbGUiOiJFbXBsb3llciIsImV4cCI6MTcyNjQ4NTM2MywiaWF0IjoxNzI2NDQyMTYzfQ.D2gMU0_O5PbeKPwE0dTyqxdX0X7KwYkOLc2lyHKes_I";
        String message = "Login Successful";

        when(userService.userExists(any(RequestDto.class))).thenReturn(true);
        when(userService.passwordMatches(any(RequestDto.class))).thenReturn(true);
        when(userService.getUsersInfo(anyString())).thenReturn(userDto);
        when(jwtTokenService.createJwtToken(any(UserDto.class))).thenReturn(token);


        ResponseEntity<Object> response = login.checkCredentials(requestDto);

        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = (Map<String, Object>) response.getBody();

        assertNotNull(responseMap);
        assertEquals(3, responseMap.size());
        assertEquals(token, responseMap.get("token"));
        assertEquals(userDto, responseMap.get("user"));
        assertEquals(message, responseMap.get("message"));
        assertEquals(201, response.getStatusCode().value());

        verify(userService, times(1)).userExists(any(RequestDto.class));
        verify(jwtTokenService, times(1)).createJwtToken(any(UserDto.class));
    }

    @Test
    void checkCredentialsUserDoesntExist() {
        RequestDto requestDto = new RequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPass("password");

        when(userService.userExists(any(RequestDto.class))).thenReturn(false);

        ResponseEntity<Object> response = login.checkCredentials(requestDto);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("Incorrect Email Address", response.getBody());
    }

    @Test
    void checkCredentialsUserExistsButBadCredentials() {
        RequestDto requestDto = new RequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPass("password");

        when(userService.userExists(any(RequestDto.class))).thenReturn(true);
        when(userService.passwordMatches(any(RequestDto.class))).thenReturn(false);

        ResponseEntity<Object> response = login.checkCredentials(requestDto);

        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        assertEquals("Incorrect Password", response.getBody());
    }

    @Test
    void emailLookupUserExists() {
        String email = "test@test.com";

        when(userService.userExists(any(RequestDto.class))).thenReturn(true);

        ResponseEntity<String> response = login.emailLookup(email);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Email address found, reset your password", response.getBody());
    }

    @Test
    void emailLookupUserDoesntExist() {
        String email = "test@test.com";

        when(userService.userExists(any(RequestDto.class))).thenReturn(false);

        ResponseEntity<String> response = login.emailLookup(email);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("We couldn't find your email, please make an account first!", response.getBody());
    }

    @Test
    void resetPasswordSuccessful() {
        PasswordResetDto passwordResetDto = new PasswordResetDto();
        passwordResetDto.setFirstName("Test");
        passwordResetDto.setLastName("Com");
        passwordResetDto.setPassword("Password");
        passwordResetDto.setRole("Applicant");
        passwordResetDto.setCreatedAt("01-01-2024");

        when(userService.updateUser(any(PasswordResetDto.class))).thenReturn(true);

        ResponseEntity<String> response = login.resetPassword(passwordResetDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(userService, times(1)).updateUser(any(PasswordResetDto.class));
    }

    @Test
    void resetPasswordUnsuccessful() {
        PasswordResetDto passwordResetDto = new PasswordResetDto();
        passwordResetDto.setFirstName("Test");
        passwordResetDto.setLastName("Com");
        passwordResetDto.setPassword("Password");
        passwordResetDto.setRole("Applicant");
        passwordResetDto.setCreatedAt("01-01-2024");

        when(userService.updateUser(any(PasswordResetDto.class))).thenReturn(false);

        ResponseEntity<String> response = login.resetPassword(passwordResetDto);

        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        verify(userService).updateUser(any(PasswordResetDto.class));
    }
}