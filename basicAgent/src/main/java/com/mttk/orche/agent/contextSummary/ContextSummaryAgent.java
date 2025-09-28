package com.mttk.orche.agent.contextSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mttk.orche.addon.agent.ChatMemory;
import com.mttk.orche.addon.agent.ChatMessage;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.agent.impl.PromptUtil;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.support.JsonUtil;
import com.mttk.orche.service.support.AgentFile;

// @AgentTemplateFlag(key = "_context-summary-agent", name = "上下文汇总助手", description = """
//         汇总上下文信息,生成总结Markdown文件
//          """, i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "model", label = "模型", mode = "select", size = 1, mandatory = true, props = { "url:llmModel/query" })

public class ContextSummaryAgent extends AbstractAgent {
    @Override
    protected String executeInternal(AgentParam para, String transactionId) throws Exception {
        Stack<ChatMemory> stack = para.getContext().getChatMemoryStack();
        if (stack.isEmpty()) {
            throw new RuntimeException("Chat memory stack is empty, no chat history to summary");
        }
        ChatMemory chatMemory = stack.peek();
        List<ChatMessage> msgs = chatMemory.getMessages();
        StringBuilder sb = new StringBuilder(2048);
        for (int i = 0; i < msgs.size() - 1; i++) {
            // - 1是因为最后一个是调用本Agent的工具,需要忽略
            ChatMessage msg = msgs.get(i);
            String content = msg.getContent();
            // 如果内容包含markdown语法，使用更安全的代码块格式
            // if (containsMarkdownSyntax(content)) {
            sb.append("##  ").append(msg.getRole()).append("\r\n```text\n").append(content).append("\n```\n");
            // } else {
            // sb.append("## ").append(msg.getRole()).append("\r\n
            // ```\n").append(content).append("\n```\n");
            // }
        }
        //
        String prompt = Prompt.PRMOPT_SUMMARY;
        prompt = PromptUtil.parsePrompt(prompt, para, key -> {
            if ("HISTORYS".equals(key)) {
                return sb.toString();
            }
            return null;
        });
        //
        // System.out.println("result 0: " + prompt);
        String result = AgentUtil.callLlm(para, "上下文汇总", prompt);

        // System.out.println("result 1: " + result);
        result = JsonUtil.cleanJsonString(result);
        // System.out.println("result 2: " + result);
        // 使用ObjectMapper解析JSON结果
        ObjectMapper objectMapper = new ObjectMapper();
        SummaryResult summaryResult = objectMapper.readValue(result, SummaryResult.class);

        // 保存文件
        AgentFile agentFile = AgentUtil.getAgentFileService(para.getContext()).upload(
                para.getContext(),
                summaryResult.getFileName(),
                summaryResult.getDescription(),
                summaryResult.getContent());

        // 返回markdown格式的汇总信息
        return String.format("## 汇总文件信息\n\n" +
                "- **文件名**: %s\n" +
                "- **描述**: %s\n" +
                "- **文件大小**: %d 字节\n",
                agentFile.getFileName(),
                agentFile.getDescription(),
                agentFile.getSize());
    }

    /**
     * 检测内容是否包含markdown语法
     * 
     * @param content 要检测的内容
     * @return 如果包含markdown语法返回true，否则返回false
     */
    // private boolean containsMarkdownSyntax(String content) {
    // if (content == null || content.trim().isEmpty()) {
    // return false;
    // }

    // // 检测常见的markdown语法
    // return content.contains("# ") || // 标题
    // content.contains("## ") || // 二级标题
    // content.contains("### ") || // 三级标题
    // content.contains("**") || // 粗体
    // content.contains("*") || // 斜体或列表
    // content.contains("```") || // 代码块
    // content.contains("`") || // 行内代码
    // content.contains("> ") || // 引用
    // content.contains("- ") || // 无序列表
    // content.contains("1. ") || // 有序列表
    // content.contains("---") || // 分隔线
    // content.contains("===") || // 分隔线
    // content.contains("[") && content.contains("](") || // 链接
    // content.contains("![") && content.contains("]("); // 图片
    // }

    // 内部类用于解析JSON结果
    private static class SummaryResult {
        private String fileName;
        private String description;
        private String content;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
