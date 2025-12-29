package com.localmind.agent;

public enum StepStrategy {
    DIRECT_FINAL,      // no tool, no planning
    TOOL_REQUIRED,     // must call tool
    FREE_REASONING     // allow thoughts
}

