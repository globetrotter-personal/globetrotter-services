package com.globetrotter.travel.controller;

import com.globetrotter.travel.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/travel/flights")
@Tag(name = "Travel", description = "Flight and Travel Related Endpoints")
public class FlightController {
    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @Operation(
        summary = "Search for flights",
        description = "Retrieves flight information based on origin, destination, and date"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved flights",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\n  \"data\": [\n    {\n      \"type\": \"flight-offer\",\n      \"id\": \"1\",\n      \"source\": \"GDS\",\n      \"itineraries\": [...],\n      \"price\": {\n        \"currency\": \"USD\",\n        \"total\": \"325.94\"\n      }\n    }\n  ]\n}"
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
        @ApiResponse(responseCode = "500", description = "Error retrieving flight information")
    })
    @GetMapping
    public Object getFlights(
            @Parameter(description = "Origin airport code (e.g., SFO)", required = true)
            @RequestParam String from,
            
            @Parameter(description = "Destination airport code (e.g., JFK)", required = true)
            @RequestParam String to,
            
            @Parameter(description = "Flight date in ISO 8601 format (YYYY-MM-DD)", required = true, example = "2025-07-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return flightService.getFlights(from, to, date);
    }
}
