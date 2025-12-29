package com.localmind.service;

import com.localmind.memory.MemoryEntity;
import com.localmind.memory.MemoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecentRecallService {

    private final MemoryRepository repository;

    public RecentRecallService(MemoryRepository repository) {
        this.repository = repository;
    }

    public List<MemoryEntity> recall(int limit) {
        return repository.findTop10ByOrderByCreatedAtEpochDesc();
    }
}
