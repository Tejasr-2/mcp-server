package com.localmind.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmind.memory.MemoryEntity;
import com.localmind.memory.MemoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SemanticRecallService {

    private static final Logger logger = LoggerFactory.getLogger(SemanticRecallService.class);

    private final MemoryRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public SemanticRecallService(MemoryRepository repository) {
        this.repository = repository;
    }

    public String recall(String query) {
        logger.info("SemanticRecallService.recall called with query: {}", query);
        try {
            Process p = new ProcessBuilder(
                    "python",
                    "D:/mcp-server/faiss/search.py",
                    query
            ).start();

            String output = new String(p.getInputStream().readAllBytes());
            logger.debug("FAISS search.py output: {}", output);
            List<Long> ids = mapper.readValue(
                    output,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Long>>() {}
            );

            if (ids.isEmpty()) {
                logger.info("No relevant memory found for query: {}", query);
                return "No relevant memory.";
            }

            StringBuilder sb = new StringBuilder();

            for (MemoryEntity m : repository.findAllById(ids)) {
                sb.append("- [")
                        .append(m.getType())
                        .append("] ")
                        .append(m.getContent())
                        .append('\n');
            }

            String result = sb.toString().trim();
            logger.info("SemanticRecallService.recall result: {}", result);
            return result;

        } catch (Exception e) {
            logger.error("Memory recall error", e);
            return "Memory recall error: " + e.getMessage();
        }
    }
}
