package com.mttk.orche.agent.smartReact;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PromptGenerated {
    private String systemPrompt;
    private String nextStepPrompt;

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getNextStepPrompt() {
        return nextStepPrompt;
    }

    public static PromptGenerated parse(String result) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, PromptGenerated.class);
    }
}
