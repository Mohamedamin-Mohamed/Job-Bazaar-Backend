package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.JwtToken.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenTest {
    @Mock
    JwtTokenService tokenService;

    @InjectMocks
    Token token;

    @Test
    void validateToken_IsValid() {
        String authHeader = "Bearer dhdhdhiieie";
        when(tokenService.verifyJwtToken(anyString())).thenReturn(true);

        ResponseEntity<String> response = token.validateToken(authHeader);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void validateToken_NotValid() {
        String authHeader = "Bearer dhdhdhiieie";
        when(tokenService.verifyJwtToken(anyString())).thenReturn(false);

        ResponseEntity<String> response = token.validateToken(authHeader);
        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
    }

}