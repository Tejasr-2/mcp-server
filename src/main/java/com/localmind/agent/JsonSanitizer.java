package com.localmind.agent;

public class JsonSanitizer {

    public static String sanitize(String raw) {
        if (raw == null) return null;

        String cleaned = raw.trim();

        // Remove ```json or ``` fences
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```[a-zA-Z]*", "");
            cleaned = cleaned.replaceFirst("```$", "");
        }

        return cleaned.trim();
    }
}
