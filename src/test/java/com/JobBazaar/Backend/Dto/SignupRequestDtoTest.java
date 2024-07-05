package com.JobBazaar.Backend.Dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

class SignupRequestDtoTest {
    @InjectMocks
    SignupRequestDto signupRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void setEmail(){
        signupRequestDto.setEmail("email");
        assertEquals("email", signupRequestDto.getEmail());
    }

    @Test
    void setPassword(){
        signupRequestDto.setPassword("password");
        assertEquals("password", signupRequestDto.getPassword());
    }

    @Test
    void setFirstName(){
        signupRequestDto.setFirstName("firstName");
        assertEquals("firstName", signupRequestDto.getFirstName());
    }

    @Test
    void setLastName(){
        signupRequestDto.setLastName("lastName");
        assertEquals("lastName", signupRequestDto.getLastName());
    }

    @Test
    void getEmail(){
        signupRequestDto.setEmail("email");
        assertEquals("email", signupRequestDto.getEmail());
    }

    @Test
    void getPassword(){
        signupRequestDto.setPassword("password");
        assertEquals("password", signupRequestDto.getPassword());
    }

    @Test
    void getFirstName(){
        signupRequestDto.setFirstName("firstName");
        assertEquals("firstName", signupRequestDto.getFirstName());
    }

    @Test
    void getLastName(){
        signupRequestDto.setLastName("lastName");
        assertEquals("lastName", signupRequestDto.getLastName());
    }
}