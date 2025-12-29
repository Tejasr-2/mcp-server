package com.localmind.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.agent.JsonSanitizer;
import com.localmind.agent.PromptBuilder;
import com.localmind.agent.SystemPrompt;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AgentService {

    private final OllamaClient ollamaClient;
    private final MemoryTool memoryTool;
    private final MemoryRecallService memoryRecallService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AgentService(OllamaClient ollamaClient, MemoryTool memoryTool, MemoryRecallService memoryRecallService) {
        this.ollamaClient = ollamaClient;
        this.memoryTool = memoryTool;
        this.memoryRecallService = memoryRecallService;
    }

    public Map<String, Object> handle(String userMessage) {

        // 1. Recall memory
        String memoryContext = memoryRecallService.recallAsText();

        // 2. Build prompt with memory
        String prompt = PromptBuilder.build(memoryContext, userMessage);
        System.out.println("Prompt" + prompt);
        // 3. Call model
        String raw = ollamaClient.generate(prompt);
        String sanitized = JsonSanitizer.sanitize(raw);
        System.out.println("Sanitised Response" + sanitized);

        try {
            Map<String, Object> json =
                    mapper.readValue(sanitized, new TypeReference<>() {});

            if ("tool_call".equals(json.get("type"))) {
                return handleTool(json);
            }

            return json;

        } catch (Exception e) {
            return Map.of(
                    "type", "message",
                    "content", "Memory recall failed due to invalid model output."
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
