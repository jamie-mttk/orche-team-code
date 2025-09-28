package com.mttk.orche.agent.report.util;

import com.mttk.orche.addon.agent.AgentContext;

public class MarkdownReportGenerator extends PromptBasedReportGenerator {
    @Override
    public String getSystemPrompt(AgentContext context) throws Exception {
        return Prompt.MARKDOWN;
    }

    // @Override
    // public String getFileExt() {
    //     return "md";
    // }
}
