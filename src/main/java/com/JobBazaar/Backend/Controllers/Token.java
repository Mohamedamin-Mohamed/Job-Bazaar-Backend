package com.JobBazaar.Backend.Controllers;

import com.JobBazaar.Backend.JwtToken.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/validate-token")
public class Token {

    private final JwtTokenService jwtTokenService;
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    @Autowired
    public Token(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @GetMapping("/")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        LOGGER.info("Received request to check token {}", authHeader);
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = jwtTokenService.verifyJwtToken(token);
        if (isValid) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
