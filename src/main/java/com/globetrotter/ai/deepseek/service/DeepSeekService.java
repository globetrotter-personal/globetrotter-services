package com.globetrotter.ai.deepseek.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DeepSeekService {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekService.class);

    @Value("${deepseek.api.key:#{null}}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;
    
    @Value("${deepseek.api.model:deepseek-chat}")
    private String model;

    private final RestTemplate restTemplate;

    public DeepSeekService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCompletion(String prompt) {
        return getCompletion(prompt, 100);
    }

    public String getCompletion(String prompt, int maxTokens) {
        logger.info("Calling DeepSeek API for prompt: {}, maxTokens: {}", prompt, maxTokens);
        if (apiKey == null || apiKey.isBlank()) {
            logger.error("DeepSeek API key is missing. Set deepseek.api.key in properties or DEEPSEEK_API_KEY env variable.");
            return "{\"error\":\"DeepSeek API key is missing.\"}";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", new Object[]{
            Map.of("role", "user", "content", prompt)
        });
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            String response = restTemplate.postForObject(apiUrl, entity, String.class);
            logger.info("DeepSeek API response: {}", response);
            return response;
        } catch (Exception ex) {
            logger.error("DeepSeek API error: {}", ex.getMessage(), ex);
            if (ex instanceof org.springframework.web.client.HttpStatusCodeException) {
                org.springframework.web.client.HttpStatusCodeException httpEx = 
                    (org.springframework.web.client.HttpStatusCodeException) ex;
                String errorBody = httpEx.getResponseBodyAsString();
                int statusCode = httpEx.getStatusCode().value();
                logger.error("DeepSeek error response body: {}", errorBody);
                
                // If there's no response body, create a JSON error response
                if (errorBody == null || errorBody.isBlank()) {
                    return String.format("{\"error\":\"DeepSeek API error - HTTP %d\",\"message\":\"%s\"}", 
                        statusCode, httpEx.getStatusText());
                }
                return errorBody;
            }
            // For non-HTTP exceptions, return a structured error
            return String.format("{\"error\":\"DeepSeek API call failed\",\"message\":\"%s\"}", 
                ex.getMessage().replace("\"", "\\\""));
        }
    }
} 