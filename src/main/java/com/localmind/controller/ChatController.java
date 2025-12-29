package com.localmind.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.agent.SystemPrompt;
import com.localmind.service.AgentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final AgentService agentService;

    public ChatController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    public Map<String, Object> chat(@RequestBody Map<String, String> body) {
        String userMessage = body.get("message");
        return agentService.handle(userMessage);
    }
}
