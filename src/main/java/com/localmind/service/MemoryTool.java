package com.localmind.service;

import com.localmind.memory.MemoryEntity;
import com.localmind.memory.MemoryRepository;
import org.springframework.stereotype.Service;

@Service
public class MemoryTool {

    private final MemoryRepository repository;

    public MemoryTool(MemoryRepository repository) {
        this.repository = repository;
    }

    public void save(String type, String content) {
        repository.save(new MemoryEntity(type, content));
    }
}
