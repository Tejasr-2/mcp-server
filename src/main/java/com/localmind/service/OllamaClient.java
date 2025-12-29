package com.localmind.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.localmind.dto.OllamaRequest;
import com.localmind.dto.OllamaResponse;

@Service
public class OllamaClient {

    private final WebClient webClient = WebClient.create("http://localhost:11434");

    public String generate(String prompt) {
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

        return res.response();
    }
}
