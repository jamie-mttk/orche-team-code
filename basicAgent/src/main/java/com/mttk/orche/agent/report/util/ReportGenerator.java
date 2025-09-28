package com.mttk.orche.agent.report.util;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;

public interface ReportGenerator {
    public String generate(AgentContext context, AdapterConfig config, AdapterConfig request) throws Exception;

    // String getFileExt();
}
