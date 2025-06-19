package com.globetrotter.travel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globetrotter.travel.model.FlightSearchRequest;
import com.globetrotter.travel.model.FlightSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FlightService {
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
    private final AmadeusApiService amadeusApiService;
    private final ObjectMapper objectMapper;

    public FlightService(AmadeusApiService amadeusApiService) {
        this.amadeusApiService = amadeusApiService;
        this.objectMapper = new ObjectMapper();
    }

    public FlightSearchResponse getFlights(String from, String to, LocalDate date) {
        try {
            logger.info("Fetching flights from {} to {} for date {}", from, to, date);
            Object response = amadeusApiService.searchFlights(from, to, date);
            logger.debug("Received response from Amadeus API: {}", response);

            if (response instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) response;

                // Check for error response
                if (responseMap.containsKey("error")) {
                    logger.error("Error in Amadeus API response: {}", responseMap.get("error"));
                    return createErrorResponse();
                }

                FlightSearchResponse flightResponse = new FlightSearchResponse();

                // Set data
                if (responseMap.containsKey("data")) {
                    Object data = responseMap.get("data");
                    if (data instanceof List) {
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) data;
                        List<FlightSearchResponse.FlightOffer> flightOffers = dataList.stream()
                                .map(offerMap -> objectMapper.convertValue(offerMap,
                                        FlightSearchResponse.FlightOffer.class))
                                .toList();
                        flightResponse.setData(flightOffers);
                    } else {
                        logger.warn("Expected 'data' to be a List but got: {}", data.getClass().getName());
                    }
                }

                // Set meta
                if (responseMap.containsKey("meta")) {
                    Object meta = responseMap.get("meta");
                    if (meta instanceof Map) {
                        flightResponse.setMeta(objectMapper.convertValue(meta, FlightSearchResponse.Meta.class));
                    } else {
                        logger.warn("Expected 'meta' to be a Map but got: {}", meta.getClass().getName());
                    }
                }

                // Set dictionaries
                if (responseMap.containsKey("dictionaries")) {
                    Object dictionaries = responseMap.get("dictionaries");
                    if (dictionaries instanceof Map) {
                        flightResponse.setDictionaries(
                                objectMapper.convertValue(dictionaries, FlightSearchResponse.Dictionaries.class));
                    } else {
                        logger.warn("Expected 'dictionaries' to be a Map but got: {}",
                                dictionaries.getClass().getName());
                    }
                }

                return flightResponse;
            }

            logger.warn("Response is not a Map: {}", response.getClass().getName());
            return createErrorResponse();
        } catch (Exception e) {
            logger.error("Error fetching flights", e);
            return createErrorResponse();
        }
    }

    public FlightSearchResponse searchFlights(FlightSearchRequest request) {
        try {
            logger.info("Searching flights with request: {}", request);
            String fromCode = request.getFromAirportCode() != null && !request.getFromAirportCode().isEmpty()
                    ? request.getFromAirportCode()
                    : request.getFrom();
            String toCode = request.getToAirportCode() != null && !request.getToAirportCode().isEmpty()
                    ? request.getToAirportCode()
                    : request.getTo();

            Object response = amadeusApiService.searchFlights(
                    fromCode,
                    toCode,
                    request.getFromDate(),
                    request.getNumberOfPassengers(),
                    request.getTravelClass());

            logger.debug("Received response from Amadeus API: {}", response);

            if (response instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) response;

                // Check for error response
                if (responseMap.containsKey("error")) {
                    logger.error("Error in Amadeus API response: {}", responseMap.get("error"));
                    return createErrorResponse();
                }

                FlightSearchResponse flightResponse = new FlightSearchResponse();

                // Set data
                if (responseMap.containsKey("data")) {
                    Object data = responseMap.get("data");
                    if (data instanceof List) {
                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) data;
                        List<FlightSearchResponse.FlightOffer> flightOffers = dataList.stream()
                                .map(offerMap -> objectMapper.convertValue(offerMap,
                                        FlightSearchResponse.FlightOffer.class))
                                .toList();
                        flightResponse.setData(flightOffers);
                    } else {
                        logger.warn("Expected 'data' to be a List but got: {}", data.getClass().getName());
                    }
                }

                // Set meta
                if (responseMap.containsKey("meta")) {
                    Object meta = responseMap.get("meta");
                    if (meta instanceof Map) {
                        flightResponse.setMeta(objectMapper.convertValue(meta, FlightSearchResponse.Meta.class));
                    } else {
                        logger.warn("Expected 'meta' to be a Map but got: {}", meta.getClass().getName());
                    }
                }

                // Set dictionaries
                if (responseMap.containsKey("dictionaries")) {
                    Object dictionaries = responseMap.get("dictionaries");
                    if (dictionaries instanceof Map) {
                        flightResponse.setDictionaries(
                                objectMapper.convertValue(dictionaries, FlightSearchResponse.Dictionaries.class));
                    } else {
                        logger.warn("Expected 'dictionaries' to be a Map but got: {}",
                                dictionaries.getClass().getName());
                    }
                }

                return flightResponse;
            }

            logger.warn("Response is not a Map: {}", response.getClass().getName());
            return createErrorResponse();
        } catch (Exception e) {
            logger.error("Error searching flights", e);
            return createErrorResponse();
        }
    }

    private FlightSearchResponse createErrorResponse() {
        FlightSearchResponse errorResponse = new FlightSearchResponse();
        FlightSearchResponse.Meta meta = new FlightSearchResponse.Meta();
        meta.setCount(0);
        errorResponse.setMeta(meta);
        return errorResponse;
    }
}
