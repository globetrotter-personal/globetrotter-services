package com.globetrotter.travel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
public class AmadeusApiService {
    private static final Logger logger = LoggerFactory.getLogger(AmadeusApiService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${amadeus.api.clientId}")
    private String clientId;

    @Value("${amadeus.api.clientSecret}")
    private String clientSecret;

    @Value("${amadeus.api.baseUrl:https://test.api.amadeus.com}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private String accessToken;
    private long tokenExpiry = 0;

    public String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry) {
            return accessToken;
        }
        String url = baseUrl + "/v1/security/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        HttpEntity<String> entity = new HttpEntity<>(
                "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret,
                headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map body = response.getBody();
            if (body != null && body.get("access_token") != null) {
                accessToken = body.get("access_token").toString();
                int expiresIn = Integer.parseInt(body.get("expires_in").toString());
                tokenExpiry = System.currentTimeMillis() + (expiresIn - 60) * 1000L;
                return accessToken;
            }
        } catch (Exception ex) {
            logger.error("Failed to get Amadeus access token: {}", ex.getMessage(), ex);
        }
        return null;
    }

    public Object searchFlights(String from, String to, LocalDate date) {
        return searchFlights(from, to, date, 1, null);
    }

    public Object searchFlights(String from, String to, LocalDate date, int numberOfPassengers, String travelClass) {
        String token = getAccessToken();
        if (token == null) {
            return Collections.singletonMap("error", "Unable to authenticate with Amadeus API");
        }
        logger.debug("Amadeus API From: {}", from.toString());
        logger.debug("Amadeus API To: {}", to.toString());
        System.out.println("Amadeus API From: " + from);
        System.out.println("Amadeus API To: " + to);
        // Validate airport codes
        if (from == null || from.length() != 3) {
            return Collections.singletonMap("error", "Invalid origin airport code: " + from);
        }
        if (to == null || to.length() != 3) {
            return Collections.singletonMap("error", "Invalid destination airport code: " + to);
        }

        StringBuilder urlBuilder = new StringBuilder(baseUrl)
                .append("/v2/shopping/flight-offers")
                .append("?originLocationCode=").append(from)
                .append("&destinationLocationCode=").append(to)
                .append("&departureDate=").append(date)
                .append("&max=2")
                .append("&adults=").append(numberOfPassengers);

        if (travelClass != null && !travelClass.isEmpty()) {
            urlBuilder.append("&travelClass=").append(travelClass);
        }
        logger.debug("Amadeus API URL: {}", urlBuilder.toString());
        String url = urlBuilder.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();
            System.out.println("Received response from Amadeus API: " + responseBody);

            if (responseBody != null) {
                return objectMapper.readValue(responseBody, Map.class);
            }
            return Collections.singletonMap("error", "Empty response from Amadeus API");
        } catch (Exception ex) {
            logger.error("Amadeus flight search error: {}", ex.getMessage(), ex);
            return Collections.singletonMap("error", ex.getMessage());
        }
    }
}
