package com.JobBazaar.Backend.Dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class UserNamesTest {
    @InjectMocks
    UserNames userNames;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void setFirstName(){
        userNames.setFirstName("John");
        assertEquals("John", userNames.getFirstName());
    }

    @Test
    void setLastName(){
        userNames.setLastName("Doe");
        assertEquals("Doe", userNames.getLastName());
    }

    @Test
    void getFirstName(){
        userNames.setFirstName("John");
        assertEquals("John", userNames.getFirstName());
    }

    @Test
    void getLastName(){
        userNames.setLastName("Doe");
        assertEquals("Doe", userNames.getLastName());
    }
}