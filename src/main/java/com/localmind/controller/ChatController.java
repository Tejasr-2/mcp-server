package com.localmind.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.agent.SystemPrompt;
import com.localmind.service.AgentService;
import com.localmind.service.HybridRecallService;
import com.localmind.service.MultiStepAgentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final AgentService agentService;
    private final HybridRecallService hybridRecallService;
    private final MultiStepAgentService multiStepAgentService;


    public ChatController(AgentService agentService, HybridRecallService hybridRecallService, MultiStepAgentService multiStepAgentService) {
        this.agentService = agentService;
        this.hybridRecallService = hybridRecallService;
        this.multiStepAgentService = multiStepAgentService;
    }

    @PostMapping
    public Map<String, Object> chat(@RequestBody Map<String, String> body) throws JsonProcessingException {
        logger.info("POST /chat request: {}", body);
        String userMessage = body.get("message");
        Map<String, Object> response = agentService.handle(userMessage);
        logger.info("POST /chat response: {}", response);
        return response;
    }

    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> chatStream(
            @RequestBody Map<String, String> body
    ) {
        String userMessage = body.get("message");
        String memoryContext =
                hybridRecallService.recall(userMessage);

        return multiStepAgentService
                .runStreaming(memoryContext, userMessage);
    }

}
