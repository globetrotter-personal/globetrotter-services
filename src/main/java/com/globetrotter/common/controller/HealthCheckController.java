package com.globetrotter.common.controller;

import com.globetrotter.common.service.HealthCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
@Tag(name = "Health", description = "API Health and Status Endpoints")
public class HealthCheckController {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);
    private final HealthCheckService healthCheckService;

    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @Operation(summary = "Check API health status", description = "Returns UP if the API is functioning correctly")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API is healthy"),
        @ApiResponse(responseCode = "500", description = "API is not functioning correctly")
    })
    @GetMapping("/health")
    public String health() {
        logger.info("Health check endpoint called");
        return healthCheckService.getHealthStatus();
    }
}
