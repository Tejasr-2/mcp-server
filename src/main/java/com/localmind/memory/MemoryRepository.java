package com.localmind.memory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoryRepository extends JpaRepository<MemoryEntity, Long> {

    // Latest memories
    List<MemoryEntity> findTop20ByOrderByCreatedAtEpoch();

    // Type-based memory
    List<MemoryEntity> findByTypeOrderByCreatedAtEpoch(String type);

    // Keyword search (simple, fast)
    List<MemoryEntity> findByContentContainingIgnoreCase(String keyword);

    List<MemoryEntity> findTop10ByOrderByCreatedAtEpochDesc();

}
