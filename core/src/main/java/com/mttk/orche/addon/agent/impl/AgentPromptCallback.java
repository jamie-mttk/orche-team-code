package com.mttk.orche.addon.agent.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        // /_开头的是Agent请求里获取
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
        } else if ("tools".equals(key)) {
            return generateTools();
        }

        //
        return null;
    }

    private String generateTools() {
        try {
            return "[" + AgentUtil.getFunctions(agentParam.getContext(), agentParam.getAgentConfig()).stream()
                    .collect(Collectors.joining(", ")) + "]";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object applyAgent(AgentParam agentParam, String key) {
        //
        if (agentParam == null) {
            return null;
        }
        return agentParam.getRequest().get(key);
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
            return "(无)";
        }
        //
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < agentFiles.size(); i++) {
            AgentFile agentFile = agentFiles.get(i);
            // 文件名作为三级标题
            sb.append("### ").append(agentFile.getFileName()).append("\n\n");
            // 描述作为文本
            if (agentFile.getDescription() != null && !agentFile.getDescription().isEmpty()) {
                sb.append("```markdown\n").append(agentFile.getDescription()).append("\n```").append("\n\n");
            }
            // 文件大小信息
            // sb.append("文件大小: ").append(agentFile.getSize()).append(" 字节\n\n");
        }

        return sb.toString();
    }

}
