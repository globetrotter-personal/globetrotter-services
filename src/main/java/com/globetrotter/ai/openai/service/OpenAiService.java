package com.globetrotter.ai.openai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globetrotter.travel.model.FlightSearchRequest;
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
public class OpenAiService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);

    @Value("${openai.api.key:#{null}}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public OpenAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCompletion(String prompt) {
        return getCompletion(prompt, 100);
    }

    public String getCompletion(String prompt, int maxTokens) {
        logger.info("Calling OpenAI API for prompt: {}, maxTokens: {}", prompt, maxTokens);
        if (apiKey == null || apiKey.isBlank()) {
            logger.error("OpenAI API key is missing. Set openai.api.key in properties or OPENAI_API_KEY env variable.");
            return "{\"error\":\"OpenAI API key is missing.\"}";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo-instruct");
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", maxTokens);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            String response = restTemplate.postForObject(apiUrl, entity, String.class);
            logger.info("OpenAI API response: {}", response);
            return response;
        } catch (Exception ex) {
            logger.error("OpenAI API error: {}", ex.getMessage(), ex);
            if (ex instanceof org.springframework.web.client.HttpStatusCodeException) {
                org.springframework.web.client.HttpStatusCodeException httpEx = 
                    (org.springframework.web.client.HttpStatusCodeException) ex;
                String errorBody = httpEx.getResponseBodyAsString();
                int statusCode = httpEx.getStatusCode().value();
                logger.error("OpenAI error response body: {}", errorBody);
                
                // If there's no response body, create a JSON error response
                if (errorBody == null || errorBody.isBlank()) {
                    return String.format("{\"error\":\"OpenAI API error - HTTP %d\",\"message\":\"%s\"}", 
                        statusCode, httpEx.getStatusText());
                }
                return errorBody;
            }
            // For non-HTTP exceptions, return a structured error
            return String.format("{\"error\":\"OpenAI API call failed\",\"message\":\"%s\"}", 
                ex.getMessage().replace("\"", "\\\""));
        }
    }

    public FlightSearchRequest extractFlightSearchFields(String text) {
        String extractionPrompt = "Extract the following fields from this travel request: From, To, From Date, To Date, Number of Passengers, Travel Class. " +
                "Return a JSON object with keys: from, to, fromDate, toDate, numberOfPassengers, travelClass.\n" +
                "Text: '" + text + "'";
        String completion = getCompletion(extractionPrompt);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(completion);
            FlightSearchRequest req = new FlightSearchRequest();
            if (node.has("from")) req.setFrom(node.get("from").asText(null));
            if (node.has("to")) req.setTo(node.get("to").asText(null));
            if (node.has("fromDate")) req.setFromDate(node.get("fromDate").asText(null) != null ? java.time.LocalDate.parse(node.get("fromDate").asText()) : null);
            if (node.has("toDate")) req.setToDate(node.get("toDate").asText(null) != null ? java.time.LocalDate.parse(node.get("toDate").asText()) : null);
            if (node.has("numberOfPassengers")) req.setNumberOfPassengers(node.get("numberOfPassengers").asInt(1));
            if (node.has("travelClass")) req.setTravelClass(node.get("travelClass").asText(null));
            return req;
        } catch (Exception e) {
            logger.error("Failed to parse OpenAI extraction response: {}", e.getMessage(), e);
            return null;
        }
    }
}
