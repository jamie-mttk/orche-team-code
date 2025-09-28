package com.mttk.orche.addon.agent.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.util.StringUtil;

public class AgentPromptCallback implements Function<String, Object> {
    private AgentParam agentParam = null;
    private Map<String, Object> map = null;

    public AgentPromptCallback(AgentParam agentParam) {
        this(agentParam, null);
    }

    public AgentPromptCallback(AgentParam agentParam, Map<String, Object> map) {

        this.agentParam = agentParam;
        this.map = map;
    }

    public Object apply(String key) {
        if (StringUtil.isEmpty(key)) {
            return null;
        }
        // __开头的是系统级别的
        if (key.startsWith("__")) {
            return applySystem(key.substring(2));
        }
        // /_开头的是AGent参数的
        if (key.startsWith("_")) {
            return applyAgent(agentParam, key.substring(1));
        }
        // 其他
        if (map == null) {
            return null;
        }
        //
        return map.get(key);
    }

    private Object applySystem(String key) {
        if ("now".equals(key)) {
            return AgentUtil.now();
        } else if ("files".equals(key)) {
            return generateFilesPrompt();
        }
        //
        return null;
    }

    private Object applyAgent(AgentParam agentParam, String key) {
        //
        if (agentParam == null) {
            return null;
        }
        return agentParam.getConfig().get(key);
    }

    private String generateFilesPrompt() {
        List<AgentFile> agentFiles = null;
        try {
            agentFiles = AgentUtil.getAgentFileService(agentParam.getContext())
                    .list(agentParam.getContext());
        } catch (Exception e) {
            return "";
        }
        if (agentFiles == null || agentFiles.isEmpty()) {
            return "";
        }
        //
        StringBuilder sb = new StringBuilder();
        for (AgentFile agentFile : agentFiles) {
            sb.append("### ").append(agentFile.getFileName()).append("\n");
            sb.append("描述: ").append(agentFile.getDescription()).append("\n");
            sb.append("大小: ").append(agentFile.getSize()).append("\n\n");
        }
        return sb.toString();
    }

}
