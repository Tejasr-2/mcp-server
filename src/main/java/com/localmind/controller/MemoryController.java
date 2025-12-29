package com.localmind.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localmind.memory.MemoryEntity;
import com.localmind.memory.MemoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/tools/memory")
public class MemoryController {

    private static final Logger logger = LoggerFactory.getLogger(MemoryController.class);

    private final MemoryRepository repository;

    public MemoryController(MemoryRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/save")
    public void save(@RequestBody Map<String, String> body) {
        logger.info("POST /tools/memory/save request: {}", body);
        String type = body.getOrDefault("type", "FACT");
        String content = body.get("content");
        repository.save(new MemoryEntity(type, content));
        logger.info("Memory saved: type={}, content={}", type, content);
    }

    @GetMapping("/recent")
    public List<MemoryEntity> recent() {
        logger.info("GET /tools/memory/recent request");
        List<MemoryEntity> result = repository.findTop20ByOrderByCreatedAtEpoch();
        logger.info("Recent memories response: {} entries", result.size());
        return result;
    }

}
