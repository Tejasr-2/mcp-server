package com.localmind.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.agent.IntentDetector;
import com.localmind.agent.JsonSanitizer;
import com.localmind.agent.PromptBuilder;
import com.localmind.agent.SystemPrompt;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AgentService {

    private final OllamaClient ollamaClient;
    private final MemoryTool memoryTool;
    private final SemanticRecallService semanticRecallService;

    private final ObjectMapper mapper = new ObjectMapper();

    public AgentService(OllamaClient ollamaClient, MemoryTool memoryTool, SemanticRecallService semanticRecallService) {
        this.ollamaClient = ollamaClient;
        this.memoryTool = memoryTool;
        this.semanticRecallService = semanticRecallService;
    }

    public Map<String, Object> handle(String userMessage) {

        // 1. HARD RULE: remember intent
        if (IntentDetector.isRememberIntent(userMessage)) {

            // Ask LLM ONLY to extract memory
            String extractionPrompt = """
                    You are extracting memory content.
                    
                    User input:
                    %s
                    
                    Respond ONLY in JSON:
                    { "memory": "<text to store>" }
                    """.formatted(userMessage);

            String raw = ollamaClient.generate(extractionPrompt);
            String sanitized = JsonSanitizer.sanitize(raw);

            try {
                Map<String, String> parsed =
                        mapper.readValue(sanitized, new TypeReference<>() {
                        });

                String memoryText = parsed.get("memory");

                memoryTool.save("FACT", memoryText);

                return Map.of(
                        "type", "message",
                        "content", "Saved to memory."
                );

            } catch (Exception e) {
                return Map.of(
                        "type", "message",
                        "content", "Failed to save memory."
                );
            }
        }

        // 2. Normal agent flow
        String memoryContext = semanticRecallService.recall(userMessage);
        String prompt = PromptBuilder.build(memoryContext, userMessage);

        String raw = ollamaClient.generate(prompt);
        String sanitized = JsonSanitizer.sanitize(raw);

        try {
            return mapper.readValue(sanitized,
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception e) {
            return Map.of(
                    "type", "message",
                    "content", "Model returned invalid output."
            );
        }
    }


    private Map<String, Object> handleTool(Map<String, Object> json) {

        String tool = (String) json.get("tool");
        Map<String, Object> args = (Map<String, Object>) json.get("args");

        if ("memory.save".equals(tool)) {
            memoryTool.save(
                    (String) args.get("type"),
                    (String) args.get("content")
            );

            return Map.of(
                    "type", "message",
                    "content", "Saved to memory."
            );
        }

        return Map.of(
                "type", "message",
                "content", "Unknown tool: " + tool
        );
    }
}
