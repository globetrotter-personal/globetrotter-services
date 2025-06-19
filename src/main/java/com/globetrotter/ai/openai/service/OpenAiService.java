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

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String model;

    private final RestTemplate restTemplate;

    public OpenAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCompletion(String prompt) {
        logger.debug("getCompletion called with prompt: {}", prompt);
        String result = getCompletion(prompt, 100);
        logger.debug("getCompletion result: {}", result);
        return result;
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
        requestBody.put("model", model);
        requestBody.put("messages", new Object[] {
                Map.of("role", "user", "content", prompt)
        });
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            String response = restTemplate.postForObject(apiUrl, entity, String.class);
            logger.info("OpenAI API response: {}", response);
            return response;
        } catch (Exception ex) {
            logger.error("OpenAI API error: {}", ex.getMessage(), ex);
            if (ex instanceof org.springframework.web.client.HttpStatusCodeException) {
                org.springframework.web.client.HttpStatusCodeException httpEx = (org.springframework.web.client.HttpStatusCodeException) ex;
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
        logger.error("=== extractFlightSearchFields START ===");
        logger.error("Input text: [{}]", text);
        String extractionPrompt = "Extract the following fields from this travel request: From (city name), To (city name), From Airport Code (3-letter IATA), To Airport Code (3-letter IATA), From Date, To Date, Number of Passengers, Travel Class. "
                +
                "Return a JSON object with keys: from, to, fromAirportCode, toAirportCode, fromDate, toDate, numberOfPassengers, travelClass. "
                +
                "IMPORTANT: The airport codes MUST be valid 3-letter IATA codes. For example: SFO for San Francisco, JFK for New York, LHR for London, CDG for Paris, NRT for Tokyo, ICN for Seoul. "
                +
                "If you're not sure about the airport code, use the main international airport for the city.\n" +
                "Text: '" + text + "'";
        String completion = getCompletion(extractionPrompt);
        logger.info("OpenAI completion: {}", completion);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseNode = mapper.readTree(completion);
            logger.info("Response node: {}", responseNode);

            // Extract the text from either completions or chat completions format
            String extractedJson;
            JsonNode choices = responseNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                if (firstChoice.has("message")) {
                    // Chat completions format
                    extractedJson = firstChoice.get("message").get("content").asText().trim();
                } else if (firstChoice.has("text")) {
                    // Completions format
                    extractedJson = firstChoice.get("text").asText().trim();
                } else {
                    logger.error("Unexpected response format: {}", responseNode);
                    return null;
                }
            } else {
                logger.error("No choices found in response: {}", responseNode);
                return null;
            }

            logger.debug("Extracted text from OpenAI response (raw): [{}]", extractedJson);

            // Use regex to extract the first JSON object from the text
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{[\\s\\S]*?\\}");
            java.util.regex.Matcher matcher = pattern.matcher(extractedJson);
            boolean found = matcher.find();
            logger.debug("Regex matcher.find() result: {}", found);
            if (found) {
                extractedJson = matcher.group();
                logger.debug("JSON extracted after regex: {}", extractedJson);
            } else {
                logger.error("No JSON object found in extracted text: [{}]", extractedJson);
                return null;
            }

            // Check if extractedJson is empty or null after regex
            if (extractedJson == null || extractedJson.isEmpty()) {
                logger.error("Extracted JSON is empty or null after regex");
                return null;
            }

            logger.debug("JSON to be parsed: {}", extractedJson);

            // Clean up the JSON string by removing any leading/trailing whitespace and
            // newlines
            extractedJson = extractedJson.replaceAll("^\\s+", "").replaceAll("\\s+$", "");

            // Parse the JSON response
            JsonNode jsonNode = mapper.readTree(extractedJson);
            logger.info("Parsed JSON: {}", jsonNode);

            FlightSearchRequest req = new FlightSearchRequest();

            if (jsonNode.has("from"))
                req.setFrom(jsonNode.get("from").asText());
            if (jsonNode.has("to"))
                req.setTo(jsonNode.get("to").asText());
            if (jsonNode.has("fromAirportCode")) {
                String code = jsonNode.get("fromAirportCode").asText().toUpperCase().trim();
                if (code.length() == 3) {
                    req.setFromAirportCode(code);
                } else {
                    logger.error("Invalid fromAirportCode: {}", code);
                    return null;
                }
            }
            if (jsonNode.has("toAirportCode")) {
                String code = jsonNode.get("toAirportCode").asText().toUpperCase().trim();
                if (code.length() == 3) {
                    req.setToAirportCode(code);
                } else {
                    logger.error("Invalid toAirportCode: {}", code);
                    return null;
                }
            }
            if (jsonNode.has("fromDate")) {
                String fromDateStr = jsonNode.get("fromDate").asText();
                if (fromDateStr != null && !fromDateStr.isEmpty()) {
                    try {
                        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                                .ofPattern("MMMM d['st']['nd']['rd']['th'], yyyy");
                        req.setFromDate(java.time.LocalDate.parse(fromDateStr, formatter));
                    } catch (Exception e) {
                        logger.error("Failed to parse fromDate: {}", fromDateStr, e);
                    }
                }
            }
            if (jsonNode.has("toDate")) {
                String toDateStr = jsonNode.get("toDate").asText();
                if (toDateStr != null && !toDateStr.isEmpty()) {
                    try {
                        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                                .ofPattern("MMMM d['st']['nd']['rd']['th'], yyyy");
                        req.setToDate(java.time.LocalDate.parse(toDateStr, formatter));
                    } catch (Exception e) {
                        logger.error("Failed to parse toDate: {}", toDateStr, e);
                    }
                }
            }
            if (jsonNode.has("numberOfPassengers"))
                req.setNumberOfPassengers(jsonNode.get("numberOfPassengers").asInt());
            if (jsonNode.has("travelClass")) {
                String travelClass = jsonNode.get("travelClass").asText().toLowerCase().trim();
                if (travelClass.contains("business")) {
                    req.setTravelClass("BUSINESS");
                } else if (travelClass.contains("economy")) {
                    req.setTravelClass("ECONOMY");
                } else if (travelClass.contains("first")) {
                    req.setTravelClass("FIRST");
                } else if (travelClass.contains("premium")) {
                    req.setTravelClass("PREMIUM_ECONOMY");
                }
            }

            logger.info("Created FlightSearchRequest: {}", req.toString());
            return req;
        } catch (Exception e) {
            logger.error("Failed to parse OpenAI extraction response: {}", e.getMessage(), e);
            return null;
        }
    }
}
