package com.mttk.orche.agent.report.util;

import com.mttk.orche.addon.agent.AgentContext;

public class HtmlReportGenerator extends PromptBasedReportGenerator {
    @Override
    public String getSystemPrompt(AgentContext context) throws Exception {
        return Prompt.HTML;
    }

    // @Override
    // public String getFileExt() {
    // return "html";
    // }
}
