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

@RestController
@RequestMapping("/tools/memory")
public class MemoryController {

    private final MemoryRepository repository;

    public MemoryController(MemoryRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/save")
    public void save(@RequestBody Map<String, String> body) {
        String type = body.getOrDefault("type", "FACT");
        String content = body.get("content");

        repository.save(new MemoryEntity(type, content));
    }

    @GetMapping("/recent")
    public List<MemoryEntity> recent() {
        return repository.findTop20ByOrderByCreatedAtDesc();
    }

}
