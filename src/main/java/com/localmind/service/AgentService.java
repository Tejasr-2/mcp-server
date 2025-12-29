package com.localmind.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.agent.JsonSanitizer;
import com.localmind.agent.SystemPrompt;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AgentService {

    private final OllamaClient ollamaClient;
    private final MemoryTool memoryTool;
    private final ObjectMapper mapper = new ObjectMapper();

    public AgentService(OllamaClient ollamaClient, MemoryTool memoryTool) {
        this.ollamaClient = ollamaClient;
        this.memoryTool = memoryTool;
    }

    public Map<String, Object> handle(String userMessage) {

        String prompt = SystemPrompt.PROMPT + "\nUser: " + userMessage;
        String rawResponse = ollamaClient.generate(prompt);
        System.out.println("Raw response: " + rawResponse);
        String sanitized = JsonSanitizer.sanitize(rawResponse);
        


        try {
            Map<String, Object> json =
            mapper.readValue(sanitized, new TypeReference<>() {});

            String type = (String) json.get("type");

            if ("tool_call".equals(type)) {
                return handleTool(json);
            }

            return json;

        } catch (Exception e) {
            return Map.of(
                    "type", "message",
                    "content", "Sorry, I produced invalid output. Please retry."
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
