package com.globetrotter.openai.controller;

import com.globetrotter.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/openai")
public class OpenAiController {
    private static final Logger logger = LoggerFactory.getLogger(OpenAiController.class);
    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/completion")
    public ResponseEntity<String> getCompletion(@RequestBody String prompt) {
        logger.info("Received OpenAI completion request");
        String response = openAiService.getCompletion(prompt);
        return ResponseEntity.ok(response);
    }
}
