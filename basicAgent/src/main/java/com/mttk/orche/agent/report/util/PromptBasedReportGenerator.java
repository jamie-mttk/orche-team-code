package com.mttk.orche.agent.report.util;

import java.util.Arrays;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatMessage;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.util.StringUtil;

public abstract class PromptBasedReportGenerator implements ReportGenerator {
    private static String loadFiles(AgentContext context, AdapterConfig request) throws Exception {
        String inputFileNames = (String) request.get("inputFileNames");
        if (StringUtil.isEmpty(inputFileNames)) {
            return "";
        }
        String[] fileNames = inputFileNames.split(",");
        StringBuilder sb = new StringBuilder();
        for (String fileName : fileNames) {
            // System.out.println("@@@@@@@@@@@@fileName 1: " + inputFileNames);
            // System.out.println("@@@@@@@@@@@@fileName 2: " + fileName);
            AgentFile agentFile = AgentUtil.getAgentFileService(context).get(context, fileName);
            if (agentFile == null) {
                continue;
            }
            sb.append("###").append(agentFile.getFileName()).append("\n");
            sb.append("#### 描述\n").append(agentFile.getDescription()).append("\n");
            sb.append("#### 内容\n```\n").append(
                    AgentUtil.getAgentFileService(context).download(context, agentFile.getFileName()))
                    .append("\n```\n");
        }
        //
        // for (AgentFile agentFile : context.getFileManager().list()) {
        // //
        // sb.append("###").append(agentFile.getFileName()).append("\n");
        // sb.append("#### 描述\n").append(agentFile.getDescription()).append("\n");
        // sb.append("####
        // 内容\n").append(context.getFileManager().download(agentFile.getFileName())).append("\n");
        // }
        //
        return sb.toString();
    }

    public abstract String getSystemPrompt(AgentContext context) throws Exception;

    @Override
    public String generate(AgentContext context, AdapterConfig config, AdapterConfig request) throws Exception {
        //
        String systemPrompt = getSystemPrompt(context);
        //
        StringBuilder userPrompt = new StringBuilder(2048);
        //
        userPrompt.append("## Session ID \n\n");
        userPrompt.append(context.getSessionId()).append("\n\n");
        userPrompt.append("## 任务主题 \n\n");
        userPrompt.append(request.getString("task")).append("\n\n");
        userPrompt.append("## 当前时间 \n\n");
        userPrompt.append(AgentUtil.now()).append("\n\n");
        userPrompt.append("## 相关资料 \n\n");
        userPrompt.append(loadFiles(context, request)).append("\n\n");
        //
        // System.out.println(userPrompt.length());
        //
        ChatMessage msg = AgentUtil.callLlm(context, config.getString("model"), "生成报告",
                Arrays.asList(ChatMessage.system(systemPrompt), ChatMessage.user(userPrompt.toString())), null);

        //
        return msg.getContent();
    }
}
