package com.globetrotter.travel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class FlightService {
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);

    public List<String> getFlights(String from, String to, LocalDate date) {
        logger.info("Fetching flights from {} to {} for date {}", from, to, date);
        // TODO: Integrate with a real flight API (e.g., Amadeus)
        // For now, return a mock response
        return Collections.singletonList(String.format("Flight from %s to %s on %s", from, to, date));
    }
}
