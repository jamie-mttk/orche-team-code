package com.mttk.orche.addon.agent.impl;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatMemory;
import com.mttk.orche.addon.agent.ChatMessage;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.agent.ToolCall;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.AgentFileService;
import com.mttk.orche.service.LlmExecuteService;
import com.mttk.orche.service.LlmModelService;
import com.mttk.orche.util.StringUtil;
import com.mttk.orche.util.ThrowableUtil;

public class AgentUtil {
    //
    public static AgentFileService getAgentFileService(AgentContext context) {
        return context.getServer().getService(AgentFileService.class);
    }

    //
    //
    public static LlmExecuteService getLlmExecuteService(AgentContext context) {
        Server server = null;
        if (context == null) {
            server = ServerLocator.getServer();
        } else {
            server = context.getServer();
        }
        return server.getService(LlmExecuteService.class);
    }

    // 没找到返回null
    public static AdapterConfig getModelConfig(AgentContext context, String modelId) throws Exception {
        Optional<Document> o = context.getServer().getService(LlmModelService.class).load(modelId);
        if (o.isEmpty()) {
            return null;
        }
        return context.createAdapterConfig(o.get());
    }

    // 没找到返回defaultMaxTokens,modelId不存在返回-1
    public static int getModelMaxTokens(AgentContext context, String modelId, int defaultMaxTokens) throws Exception {
        AdapterConfig a = getModelConfig(context, modelId);
        if (a == null) {
            return -1;
        }
        return a.getInteger("maxTokens", defaultMaxTokens);
    }

    public static String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static ChatMessage callLlm(AgentParam para, String name, List<ChatMessage> messages,
            List<String> functions)
            throws Exception {
        return callLlm(para.getContext(), para.getConfig().getString("model"), name, messages, functions);
    }

    public static ChatMessage callLlm(AgentContext context, String modelId, String name, List<ChatMessage> messages,
            List<String> functions)
            throws Exception {
        return getLlmExecuteService(context).call(context, modelId, name, messages, functions);
    }

    // 简化模式,一次调用
    public static String callLlm(AgentParam para, String name, String prompt) throws Exception {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.user(prompt));
        ChatMessage response = callLlm(para, name, messages, null);
        return response.getContent();
    }

    public static String callLlm(AgentContext context, String modelId, String name, String prompt) throws Exception {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.user(prompt));
        ChatMessage response = callLlm(context, modelId, name, messages, null);
        return response.getContent();
    }

    public static List<String> getFunctions(AgentContext context, AdapterConfig config) throws Exception {
        List<String> members = config.get("members", List.class);
        if (members == null || members.isEmpty()) {
            return null;
        }
        List<String> toolDefines = new ArrayList<>();
        for (String member : members) {
            toolDefines.addAll(context.getToolDefine(member));
        }
        return toolDefines;

    }

    public static void handleTools(AgentContext context, ChatMessage response, ChatMemory chatMemory) throws Exception {
        //

        //
        for (ToolCall toolCall : response.getToolCalls()) {
            context.cancelCheck();
            // logger.info("CALLING tools..." + toolCall);
            // AgentTool agentTool = new
            // AgentToolManager(context).getByName(toolCall.getFunctionName());
            String[] parts = ToolDefineUtil.partToolName(toolCall.getFunctionName());
            // System.out.println("$$$$$$$ 1:" + toolCall.getFunctionName());
            // System.out.println("$$$$$$$ 2:" + parts[0] + "!!!!!!!!!!!!!!!!!!!" +
            // parts[1]);
            String result = context.execute(parts[0], parts[1], toolCall.getArguments());
            // Object result = agentTool.execute(toolCall.getParsedArguments());
            if (result == null) {
                result = "";
            }
            if (chatMemory != null) {
                chatMemory.addMessage(ChatMessage.tool(result, toolCall.getId()));
            }
            // }
        }
    }

    public static ChatResonseMessage handleErrorChatResonse(AgentContext context, String transactionId, Throwable t) {
        String errorInfo = ThrowableUtil.dump2String(t);
        ChatResonseMessage msg = new ChatResonseMessage("_agent-error", transactionId, errorInfo);
        context.sendResponse(msg);
        return msg;
    }

    /**
     * 粗略估计字符串调用大模型占用的token数量
     * 
     * @param content 输入内容
     * @return 估计的token数量
     */
    public static int estimateTokenCount(String content) {
        if (StringUtil.isEmpty(content)) {
            return 0;
        }

        // 基本估算规则：
        // 1. 中文字符：约1.5个token
        // 2. 英文字符：约0.25个token（4个字符约1个token）
        // 3. 数字和标点：约0.25个token
        // 4. 空格和换行：约0.1个token

        int chineseCount = 0;
        int englishCount = 0;
        int otherCount = 0;

        for (char c : content.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FFF) {
                // 中文字符范围
                chineseCount++;
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                // 英文字母
                englishCount++;
            } else if (c != ' ' && c != '\n' && c != '\r' && c != '\t') {
                // 其他字符（数字、标点等）
                otherCount++;
            }
        }

        // 计算估计的token数量
        double estimatedTokens = chineseCount * 1.5 + (englishCount + otherCount) * 0.25;

        // 添加一些缓冲，确保不会低估
        return (int) Math.ceil(estimatedTokens * 1.1);
    }

}
