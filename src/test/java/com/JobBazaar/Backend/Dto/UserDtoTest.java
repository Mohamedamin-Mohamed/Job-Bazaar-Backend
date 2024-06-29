package com.JobBazaar.Backend.Dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @InjectMocks
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEmail(){
        userDto.setEmail("test@test.com");
        assertEquals("test@test.com", userDto.getEmail());
    }

    @Test
    void testSetEmail(){
        userDto.setEmail("test@test.com");
        assertEquals("test@test.com", userDto.getEmail());
    }

    @Test
    void testSetHashedPassword(){
        userDto.setHashedPassword("password");
        assertEquals("password", userDto.getHashedPassword());
    }

    @Test
    void testGetHashedPassword(){
        userDto.setHashedPassword("password");
        assertEquals("password", userDto.getHashedPassword());
    }
    @Test
    void testSetFirstName(){
        userDto.setFirstName("test");
        assertEquals("test", userDto.getFirstName());
    }
    @Test
    void testSetLastName(){
        userDto.setLastName("test");
        assertEquals("test", userDto.getLastName());
    }

    @Test
    void testGetFirstName(){
        userDto.setFirstName("test");
        assertEquals("test", userDto.getFirstName());
    }

    @Test
    void testGetLastName(){
        userDto.setLastName("test");
        assertEquals("test", userDto.getLastName());
    }
}