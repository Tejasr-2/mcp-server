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

            System.out.println("FAISS STDOUT: " + stdout);
            System.err.println("FAISS STDERR: " + stderr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
