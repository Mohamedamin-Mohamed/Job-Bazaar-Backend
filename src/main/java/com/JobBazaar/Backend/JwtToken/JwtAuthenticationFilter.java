package com.JobBazaar.Backend.JwtToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.logging.Logger;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtTokenService jwtTokenService;

    private final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token != null && jwtTokenService.verifyJwtToken(token)) {
            Authentication authentication = jwtTokenService.getAuthentication(token);
            if (authentication != null) {
                LOGGER.info("Authenticated token");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
