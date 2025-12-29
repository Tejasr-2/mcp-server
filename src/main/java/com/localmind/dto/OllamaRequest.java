package com.localmind.dto;

public record OllamaRequest(
        String model,
        String prompt,
        boolean stream
) {}

