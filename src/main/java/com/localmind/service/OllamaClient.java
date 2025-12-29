package com.localmind.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.localmind.dto.OllamaRequest;
import com.localmind.dto.OllamaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class OllamaClient {

    private static final Logger logger = LoggerFactory.getLogger(OllamaClient.class);
    private final WebClient webClient = WebClient.create("http://localhost:11434");
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public Flux<String> generateStream(String prompt) {
        return webClient.post()
                .uri("/api/generate")
                .bodyValue(Map.of(
                        "model", "gemma3:4b",
                        "prompt", prompt,
                        "stream", true
                ))
                .retrieve()
                .bodyToFlux(String.class)
                .map(line -> {
                    try {
                        Map<String, Object> json =
                                objectMapper.readValue(line, Map.class);
                        return (String) json.getOrDefault("response", "");
                    } catch (Exception e) {
                        return "";
                    }
                })
                .filter(token -> !token.isBlank());
    }

}
