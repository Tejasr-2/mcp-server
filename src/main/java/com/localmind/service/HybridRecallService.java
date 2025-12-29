package com.localmind.service;

import com.localmind.memory.MemoryEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HybridRecallService {

    private final RecentRecallService recentRecallService;
    private final SemanticRecallService semanticRecallService;

    public HybridRecallService(
            RecentRecallService recentRecallService,
            SemanticRecallService semanticRecallService
    ) {
        this.recentRecallService = recentRecallService;
        this.semanticRecallService = semanticRecallService;
    }

    public String recall(String userQuery) {

        // 1. Recent memory
        List<MemoryEntity> recent = recentRecallService.recall(5);

        // 2. Semantic memory (already formatted text)
        String semanticText = semanticRecallService.recall(userQuery);

        // 3. Deduplicate by content
        Set<String> seen = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        if (!recent.isEmpty()) {
            sb.append("RECENT MEMORY:\n");
            for (MemoryEntity m : recent) {
                if (seen.add(m.getContent())) {
                    sb.append("- [")
                            .append(m.getType())
                            .append("] ")
                            .append(m.getContent())
                            .append("\n");
                }
            }
        }

        if (!semanticText.equals("No relevant memory.")) {
            sb.append("\nRELEVANT MEMORY:\n");
            for (String line : semanticText.split("\n")) {
                if (seen.add(line)) {
                    sb.append(line).append("\n");
                }
            }
        }

        return sb.length() == 0
                ? "No memory available."
                : sb.toString().trim();
    }
}
