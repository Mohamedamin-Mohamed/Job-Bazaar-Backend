package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.Dto.UserNames;
import com.JobBazaar.Backend.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonTest {
    @Mock
    UserService userService;

    @InjectMocks
    Person person;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPersonNotNull(){
        UserNames names = new UserNames();
        names.setFirstName("test");
        names.setLastName("com");

        when(userService.getUsersInfo(anyString())).thenReturn(names);

        ResponseEntity<UserNames> response = person.getPerson(anyString());

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getUsersInfo(anyString());
    }
    @Test
    void getPersonNull(){
        when(userService.getUsersInfo(anyString())).thenReturn(null);

        ResponseEntity<UserNames> response = person.getPerson(anyString());

        assertEquals(404, response.getStatusCode().value());
        verify(userService).getUsersInfo(anyString());
    }

}