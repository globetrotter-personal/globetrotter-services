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
    private final AmadeusApiService amadeusApiService;

    public FlightService(AmadeusApiService amadeusApiService) {
        this.amadeusApiService = amadeusApiService;
    }

    public Object getFlights(String from, String to, LocalDate date) {
        logger.info("Fetching flights from {} to {} for date {}", from, to, date);
        return amadeusApiService.searchFlights(from, to, date);
    }
}
