package com.localmind.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.localmind.service.OllamaClient;

@RestController
public class ChatController {

@Autowired
OllamaClient ollamaClient;
    
@PostMapping("/chat")
public String chat(@RequestBody Map<String, String> req) {
    String userMessage = req.get("message");

    String prompt = """
    You are LocalMind, a local personal AI.
    Respond concisely.

    User: %s
    """.formatted(userMessage);

    return ollamaClient.generate(prompt);
}

}
