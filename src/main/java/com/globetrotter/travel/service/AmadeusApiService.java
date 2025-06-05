package com.globetrotter.travel.service;

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
        String token = getAccessToken();
        if (token == null) {
            return Collections.singletonMap("error", "Unable to authenticate with Amadeus API");
        }
        String url = baseUrl + "/v2/shopping/flight-offers" +
                "?originLocationCode=" + from +
                "&destinationLocationCode=" + to +
                "&departureDate=" + date +
                "&adults=1";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception ex) {
            logger.error("Amadeus flight search error: {}", ex.getMessage(), ex);
            return Collections.singletonMap("error", ex.getMessage());
        }
    }
}
