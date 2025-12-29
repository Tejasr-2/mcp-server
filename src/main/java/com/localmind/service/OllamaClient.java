package com.localmind.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.localmind.dto.OllamaRequest;
import com.localmind.dto.OllamaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OllamaClient {

    private static final Logger logger = LoggerFactory.getLogger(OllamaClient.class);
    private final WebClient webClient = WebClient.create("http://localhost:11434");

    public String generate(String prompt) {
        logger.info("OllamaClient.generate called with prompt: {}", prompt);
        OllamaRequest req = new OllamaRequest(
                "gemma3:4b",
                prompt,
                false
        );

        OllamaResponse res = webClient.post()
                .uri("/api/generate")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(OllamaResponse.class)
                .block();
        logger.info("OllamaClient.generate response: {}", res != null ? res.response() : null);
        return res.response();
    }
}
