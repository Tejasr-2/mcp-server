package com.localmind.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.agent.StepStrategy;
import com.localmind.agent.SystemPrompt;
import com.localmind.agent.JsonSanitizer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MultiStepAgentService {

    private static final int MAX_STEPS = 5;

    private final OllamaClient ollamaClient;
    private final MemoryTool memoryTool;
    private final ObjectMapper mapper = new ObjectMapper();

    public MultiStepAgentService(OllamaClient ollamaClient,
                                 MemoryTool memoryTool) {
        this.ollamaClient = ollamaClient;
        this.memoryTool = memoryTool;
    }

    public Map<String, Object> run(String memoryContext, String userMessage, boolean rememberIntent) throws JsonProcessingException {
        StepStrategy strategy =
                decideStrategy(rememberIntent, memoryContext);

        String conversation = """
                SYSTEM:
                %s
                
                MEMORY:
                %s
                
                USER:
                %s
                """.formatted(SystemPrompt.PROMPT, memoryContext, userMessage);

        boolean memorySaved = false;

        for (int step = 1; step <= MAX_STEPS; step++) {

            String raw = ollamaClient.generate(conversation);
            String clean = JsonSanitizer.sanitize(raw);

            Map<String, Object> response =
                    mapper.readValue(clean, new TypeReference<>() {
                    });

            String type = (String) response.get("type");

            // THOUGHT
            if ("thought".equals(type)) {
                conversation += "\nASSISTANT (thought): " + response.get("content");
                continue;
            }

            // TOOL CALL
            if ("tool_call".equals(type)) {

                if ("memory.save".equals(response.get("tool"))) {
                    Map<String, Object> args =
                            (Map<String, Object>) response.get("args");

                    memoryTool.save(
                            (String) args.get("type"),
                            (String) args.get("content")
                    );

                    memorySaved = true;
                    conversation += "\nOBSERVATION: Memory saved.";
                    continue;
                }
            }

            // FINAL
            if ("final".equals(type)) {

                // 🚀 FAST PATH: direct final allowed
                if (strategy == StepStrategy.DIRECT_FINAL) {
                    return response;
                }

                // 🚨 TOOL REQUIRED BUT NOT DONE
                if (strategy == StepStrategy.TOOL_REQUIRED && !memorySaved) {
                    conversation += """
                            SYSTEM OVERRIDE:
                            You must call memory.save before answering.
                            """;
                    continue;
                }
                return response;
            }
        }


        // Safety exit
        return Map.of(
                "type", "final",
                "content", "I could not complete the task safely."
        );
    }

    private StepStrategy decideStrategy(
            boolean rememberIntent,
            String memoryContext
    ) {
        if (rememberIntent) {
            return StepStrategy.TOOL_REQUIRED;
        }

        if (memoryContext != null
                && !memoryContext.isBlank()
                && !memoryContext.equals("No memory available.")) {
            return StepStrategy.DIRECT_FINAL;
        }

        return StepStrategy.FREE_REASONING;
    }

}
