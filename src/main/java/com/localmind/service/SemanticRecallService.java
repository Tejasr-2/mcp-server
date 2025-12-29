package com.localmind.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.memory.MemoryEntity;
import com.localmind.memory.MemoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemanticRecallService {

    private final MemoryRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public SemanticRecallService(MemoryRepository repository) {
        this.repository = repository;
    }

    public String recall(String query) {
        try {
            Process p = new ProcessBuilder(
                    "python",
                    "D:/mcp-server/faiss/search.py",
                    query
            ).start();

            String output = new String(p.getInputStream().readAllBytes());
            List<Long> ids = mapper.readValue(
                    output,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {}
            );

            if (ids.isEmpty()) return "No relevant memory.";

            StringBuilder sb = new StringBuilder();

            for (MemoryEntity m : repository.findAllById(ids)) {
                sb.append("- [")
                        .append(m.getType())
                        .append("] ")
                        .append(m.getContent())
                        .append('\n');
            }

            return sb.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Memory recall error: " + e.getMessage();
        }
    }
}
