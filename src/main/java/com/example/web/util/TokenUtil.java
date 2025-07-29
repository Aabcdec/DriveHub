package com.example.web.util;

import com.example.web.query.TokenWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {
    public TokenWrapper parseToken(String userAgent) {
        System.out.println(userAgent);
        if (userAgent == null || userAgent.trim().isEmpty()) {
            throw new IllegalArgumentException("Input JSON string cannot be null or empty");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TokenWrapper response = objectMapper.readValue(userAgent, TokenWrapper.class);
            System.out.println("Parsed value: " + response.getValue());
            return response;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + userAgent, e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during parsing", e);
        }
    }
}
