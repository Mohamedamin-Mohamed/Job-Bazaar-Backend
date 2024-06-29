package com.JobBazaar.Backend.Dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

class RequestDtoTest {

    @InjectMocks
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEmail(){
        requestDto.setEmail("test@test.com");
        assertEquals("test@test.com", requestDto.getEmail());
    }

    @Test
    void testSetEmail(){
        requestDto.setEmail("test@test.com");
        assertEquals("test@test.com", requestDto.getEmail());
    }

    @Test
    void testGetPassword(){
        requestDto.setPass("password");
        assertEquals("password", requestDto.getPassword());
    }

    @Test
    void testSetPassword(){
        requestDto.setPass("password");
        assertEquals("password", requestDto.getPassword());
    }

}