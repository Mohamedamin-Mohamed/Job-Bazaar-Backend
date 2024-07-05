package com.JobBazaar.Backend.Controllers;
import com.JobBazaar.Backend.Dto.SignupRequestDto;
import com.JobBazaar.Backend.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignupTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private Signup signup;

    private SignupRequestDto requestDto;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        requestDto = new SignupRequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPassword("password");
        requestDto.setFirstName("test");
        requestDto.setLastName("com");
    }

    @Test
    @DisplayName("Test to check if the users account has been created successfully")
    void testCreateUser_True() {
        when(userService.createUser(any(SignupRequestDto.class))).thenReturn(true);
        when(userService.subscriberAddedToTopic(any(SignupRequestDto.class), anyString())).thenReturn(true);

        ResponseEntity<String> response = signup.createUser(requestDto);

        assertEquals(201, response.getStatusCode().value());
        verify(userService, times(1)).createUser(any(SignupRequestDto.class));
    }

    @Test
    @DisplayName("Test to check if the users account hasn't been created")
    void testCreateUser_False(){
        when(userService.createUser(any(SignupRequestDto.class))).thenReturn(false);

        ResponseEntity<String> response = signup.createUser(requestDto);

        assertEquals(409, response.getStatusCode().value());
        verify(userService, times(1)).createUser(any(SignupRequestDto.class));
    }
}