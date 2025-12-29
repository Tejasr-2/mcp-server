package com.localmind.agent;

public class IntentDetector {

    public static boolean isRememberIntent(String message) {
        String m = message.toLowerCase();
        return m.contains("remember")
                || m.contains("save this")
                || m.contains("note that")
                || m.contains("store this");
    }
}
