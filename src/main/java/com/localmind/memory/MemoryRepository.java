package com.localmind.memory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoryRepository extends JpaRepository<MemoryEntity, Long> {

    // Latest memories
    List<MemoryEntity> findTop20ByOrderByCreatedAtDesc();

    // Type-based memory
    List<MemoryEntity> findByTypeOrderByCreatedAtDesc(String type);

    // Keyword search (simple, fast)
    List<MemoryEntity> findByContentContainingIgnoreCase(String keyword);
}
