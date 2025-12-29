package com.localmind.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.agent.IntentDetector;
import com.localmind.agent.JsonSanitizer;
import com.localmind.agent.PromptBuilder;
import com.localmind.agent.SystemPrompt;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class AgentService {

    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);

    private final OllamaClient ollamaClient;
    private final MemoryTool memoryTool;
    private final HybridRecallService hybridRecallService;
    private final MultiStepAgentService multiStepAgentService;

    private final ObjectMapper mapper = new ObjectMapper();

    public AgentService(OllamaClient ollamaClient, MemoryTool memoryTool, HybridRecallService hybridRecallService, MultiStepAgentService multiStepAgentService) {
        this.ollamaClient = ollamaClient;
        this.memoryTool = memoryTool;
        this.hybridRecallService = hybridRecallService;
        this.multiStepAgentService = multiStepAgentService;
    }

    public Map<String, Object> handle(String userMessage) throws JsonProcessingException {
        logger.info("AgentService.handle called with userMessage: {}", userMessage);

        // 1. HARD RULE: remember intent
//        if (IntentDetector.isRememberIntent(userMessage)) {
//
//            // Ask LLM ONLY to extract memory
//            String extractionPrompt = """
//                    You are extracting memory content.
//
//                    User input:
//                    %s
//
//                    Respond ONLY in JSON:
//                    { "memory": "<text to store>" }
//                    """.formatted(userMessage);
//
//            String raw = ollamaClient.generate(extractionPrompt);
//            String sanitized = JsonSanitizer.sanitize(raw);
//
//            try {
//                Map<String, String> parsed =
//                        mapper.readValue(sanitized, new TypeReference<>() {
//                        });
//
//                String memoryText = parsed.get("memory");
//                logger.info("Extracted memoryText: {}", memoryText);
//
//                memoryTool.save("FACT", memoryText);
//                logger.info("Memory saved via memoryTool");
//
//                return Map.of(
//                        "type", "message",
//                        "content", "Saved to memory."
//                );
//
//            } catch (Exception e) {
//                logger.error("Failed to save memory", e);
//                return Map.of(
//                        "type", "message",
//                        "content", "Failed to save memory."
//                );
//            }
//        }

        // 2. Normal agent flow
        String memoryContext = hybridRecallService.recall(userMessage);
        boolean rememberIntent = IntentDetector.isRememberIntent(userMessage);




        logger.info("Memory context for agent: {}", memoryContext);
        String prompt = PromptBuilder.build(
                memoryContext,
                userMessage,
                rememberIntent
        );
        logger.info("Built prompt: {}", prompt);
        String raw = ollamaClient.generate(prompt);
        logger.info("Raw model response: {}", raw);
        String sanitized = JsonSanitizer.sanitize(raw);
        logger.info("Sanitized model response: {}", sanitized);
        return multiStepAgentService.run(memoryContext, userMessage,rememberIntent);
//        try {
//            Map<String, Object> result = mapper.readValue(sanitized,
//                    new TypeReference<Map<String, Object>>() {
//                    });
//            logger.info("Parsed model response: {}", result);
//            return result;
//        } catch (Exception e) {
//            logger.error("Model returned invalid output", e);
//            return Map.of(
//                    "type", "message",
//                    "content", "Model returned invalid output."
//            );
//        }
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
