package com.localmind.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.agent.SystemPrompt;
import com.localmind.service.AgentService;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final AgentService agentService;

    public ChatController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    public Map<String, Object> chat(@RequestBody Map<String, String> body) throws JsonProcessingException {
        logger.info("POST /chat request: {}", body);
        String userMessage = body.get("message");
        Map<String, Object> response = agentService.handle(userMessage);
        logger.info("POST /chat response: {}", response);
        return response;
    }
}
