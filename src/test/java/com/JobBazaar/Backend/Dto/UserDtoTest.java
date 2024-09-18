package com.JobBazaar.Backend.Dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {

    @InjectMocks
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEmail() {
        userDto.setEmail("test@test.com");
        assertEquals("test@test.com", userDto.getEmail());
    }

    @Test
    void testSetEmail() {
        userDto.setEmail("test@test.com");
        assertEquals("test@test.com", userDto.getEmail());
    }

    @Test
    void testSetFirstName() {
        userDto.setFirstName("test");
        assertEquals("test", userDto.getFirstName());
    }

    @Test
    void testSetLastName() {
        userDto.setLastName("test");
        assertEquals("test", userDto.getLastName());
    }

    @Test
    void testGetFirstName() {
        userDto.setFirstName("test");
        assertEquals("test", userDto.getFirstName());
    }

    @Test
    void testGetLastName() {
        userDto.setLastName("test");
        assertEquals("test", userDto.getLastName());
    }

    @Test
    void testSetRole() {
        userDto.setRole("Employer");
        assertEquals("Employer", userDto.getRole());
    }

    @Test
    void testGetRole() {
        userDto.setRole("Employer");
        assertEquals("Employer", userDto.getRole());
    }

    @Test
    void testSeCreatedAt() {
        userDto.setCreatedAt("01-01-2024");
        assertEquals("01-01-2024", userDto.getCreatedAt());
    }

    @Test
    void testGetCreatedAt() {
        userDto.setCreatedAt("01-01-2024");
        assertEquals("01-01-2024", userDto.getCreatedAt());
    }
}