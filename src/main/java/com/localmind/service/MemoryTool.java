package com.localmind.service;

import com.localmind.memory.MemoryEntity;
import com.localmind.memory.MemoryRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MemoryTool {

    private static final Logger logger = LoggerFactory.getLogger(MemoryTool.class);

    private final MemoryRepository repository;

    public MemoryTool(MemoryRepository repository) {
        this.repository = repository;
    }

    public void save(String type, String content) {
        logger.info("MemoryTool.save called with type={}, content={}", type, content);
        MemoryEntity saved = repository.save(new MemoryEntity(type, content));

        try {
            Process p = new ProcessBuilder(
                    "python",
                    "D:/mcp-server/faiss/embed_and_store.py",
                    content,
                    saved.getId().toString()
            ).start();

            String stdout = new String(p.getInputStream().readAllBytes());
            String stderr = new String(p.getErrorStream().readAllBytes());

            logger.debug("FAISS STDOUT: {}", stdout);
            logger.error("FAISS STDERR: {}", stderr);

        } catch (Exception e) {
            logger.error("Error saving memory", e);
        }
    }
}
