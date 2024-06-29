package com.JobBazaar.Backend.Controllers;
import com.JobBazaar.Backend.Dto.RequestDto;
import com.JobBazaar.Backend.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private Login login;

    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestDto = new RequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPass("password");
    }

    @Test
    @DisplayName("Test to check when the users credentials is correct")
    void testCheckCredentials_True_PasswordMatches(){
        when(userService.userExists(requestDto)).thenReturn(true);
        when(userService.passwordMatches(requestDto)).thenReturn(true);

        ResponseEntity<String> response = login.checkCredentials(requestDto);

        assertEquals(200, response.getStatusCode().value());
        verify(userService, times(1)).userExists(any(RequestDto.class));
        verify(userService, times(1)).passwordMatches(any(RequestDto.class));
    }

    @Test
    @DisplayName("Test to check when the users email is correct but password is incorrect")
    void testCheckCredentials_True_PasswordDoesNotMatch(){
        when(userService.userExists(requestDto)).thenReturn(true);
        when(userService.passwordMatches(requestDto)).thenReturn(false);

        ResponseEntity<String> response = login.checkCredentials(requestDto);

        assertEquals(401, response.getStatusCode().value());
        verify(userService, times(1)).userExists(any(RequestDto.class));
        verify(userService, times(1)).passwordMatches(any(RequestDto.class));
    }

    @Test
    @DisplayName("Test to check when the users email doesn't exist")
    void testCheckCredentials_False_UserDoesNotExist(){
        when(userService.userExists(requestDto)).thenReturn(false);

        ResponseEntity<String> response = login.checkCredentials(requestDto);

        assertEquals(404, response.getStatusCode().value());
        verify(userService, times(1)).userExists(any(RequestDto.class));
    }

    @Test
    @DisplayName("Test to check when the users email doesn't exist")
    void testEmailLookup_UserDoesNotExist(){
        when(userService.userExists(any(RequestDto.class))).thenReturn(false);

        ResponseEntity<String> response = login.emailLookup(requestDto.getEmail());

        assertEquals(404, response.getStatusCode().value());
        verify(userService, times(1)).userExists(any(RequestDto.class));
    }

    @Test
    @DisplayName("Test to check when the users email exists")
    void testEmailLookup_UserExists(){
        when(userService.userExists(any(RequestDto.class))).thenReturn(true);

        ResponseEntity<String> response = login.emailLookup(requestDto.getEmail());

        assertEquals(200, response.getStatusCode().value());
        verify(userService, times(1)).userExists(any(RequestDto.class));
    }

    @Test
    @DisplayName("Test to check when the users password has been reset successful")
    void testPasswordResetSuccessful(){
        when(userService.updateUser(any(RequestDto.class))).thenReturn(true);

        ResponseEntity<String> response = login.resetPassword(requestDto);

        assertEquals(200, response.getStatusCode().value());
        verify(userService, times(1)).updateUser(any(RequestDto.class));
    }

    @Test
    @DisplayName("Test to check when the users password reset has failed")
    void testPasswordResetFailure(){
        when(userService.updateUser(any(RequestDto.class))).thenReturn(false);

        ResponseEntity<String> response = login.resetPassword(requestDto);

        assertEquals(401, response.getStatusCode().value());
        verify(userService, times(1)).updateUser(any(RequestDto.class));
    }

}