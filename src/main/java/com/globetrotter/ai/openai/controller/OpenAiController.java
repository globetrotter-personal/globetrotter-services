package com.globetrotter.ai.openai.controller;

import com.globetrotter.ai.openai.service.OpenAiService;
import com.globetrotter.travel.model.FlightSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai/openai")
@Tag(name = "OpenAI", description = "OpenAI integration endpoints")
public class OpenAiController {
    private static final Logger logger = LoggerFactory.getLogger(OpenAiController.class);
    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Operation(
        summary = "Generate text completion using OpenAI",
        description = "Sends a prompt to OpenAI's API and returns the text completion response"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successful completion",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\n  \"id\": \"cmpl-uqkvlQyYK7bGYrRHQ0eXlWi7\",\n  \"object\": \"text_completion\",\n  \"created\": 1689818628,\n  \"model\": \"gpt-3.5-turbo-instruct\",\n  \"choices\": [\n    {\n      \"text\": \"Wanderlust pulls at my heart...\",\n      \"index\": 0,\n      \"logprobs\": null,\n      \"finish_reason\": \"stop\"\n    }\n  ],\n  \"usage\": {\n    \"prompt_tokens\": 5,\n    \"completion_tokens\": 42,\n    \"total_tokens\": 47\n  }\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "OpenAI API error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\n  \"error\": {\n    \"message\": \"You exceeded your current quota\",\n    \"type\": \"insufficient_quota\",\n    \"param\": null,\n    \"code\": \"insufficient_quota\"\n  }\n}"
                )
            )
        )
    })
    @PostMapping("/completion")
    public ResponseEntity<String> getCompletion(
        @Parameter(
            description = "Request body containing prompt and optional maxTokens parameter",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\n  \"prompt\": \"Write a poem about travel\",\n  \"maxTokens\": 150\n}"
                )
            )
        )
        @RequestBody Map<String, Object> requestBody
    ) {
        logger.info("Received OpenAI completion request: {}", requestBody);
        
        String prompt = requestBody.get("prompt") != null ? requestBody.get("prompt").toString() : "";
        Integer maxTokens = requestBody.get("maxTokens") != null ? 
            Integer.parseInt(requestBody.get("maxTokens").toString()) : 100;
        
        logger.info("Processing prompt: {}, maxTokens: {}", prompt, maxTokens);
        String response = openAiService.getCompletion(prompt);
        
        // Check if the response is an error response
        if (response.contains("\"error\":")) {
            return ResponseEntity
                .status(500) // Internal Server Error for API issues
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        }
        
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    @Operation(summary = "Extract flight search fields from text")
    @PostMapping("/extract-flight-fields")
    public FlightSearchRequest extractFlightFields(@RequestBody String text) {
        logger.error("=== OpenAiController.extractFlightFields called ===");
        return openAiService.extractFlightSearchFields(text);
    }
}
