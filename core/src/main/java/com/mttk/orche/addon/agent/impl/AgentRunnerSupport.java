package com.mttk.orche.addon.agent.impl;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.util.StringUtil;

public abstract class AgentRunnerSupport {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 静态方法：使用函数式接口执行Agent逻辑
     * 提供更优雅的API，避免匿名内部类
     * 
     * @param context     Agent上下文
     * @param agentConfig Agent配置
     * @param requestRaw  原始请求
     * @param executor    执行器函数
     * @return 执行结果
     * @throws Exception 执行异常
     */
    public static String execute(AgentContext context, AdapterConfig agentConfig,
            String requestRaw, String toolName, AgentExecutor executor) throws Exception {
        // 构建AgentParam
        AgentParam para = new AgentParam(context, agentConfig, requestRaw, toolName);
        //
        return execute(para, executor);
    }

    public static String execute(AgentParam para, AgentExecutor executor) throws Exception {
        // 生成事务ID
        String transactionId = StringUtil.getUUID();

        // 构建agent start 数据
        Document agentStartData = new Document();
        agentStartData.append("name", para.getAgentConfig().getString("name"));
        agentStartData.append("agentConfig", para.getAgentConfig().toMap());
        agentStartData.append("request", para.getRequest().toMap());

        // 记录日志
        // para.getContext().getLogger().info("@@@@@@@@@@@@@@@@@@@@@@:" +
        // agentStartData.toJson());

        // 发送开始消息
        para.getContext().sendResponse(
                new ChatResonseMessage("_agent-start", transactionId, agentStartData.toJson()));

        try {
            // 执行Agent逻辑
            String result = executor.execute(para, transactionId);

            // 发送结束消息
            para.getContext().sendResponse(new ChatResonseMessage("_agent-end", transactionId, result));

            return result;
        } catch (Throwable t) {
            AgentUtil.handleErrorChatResonse(para.getContext(), transactionId, t);
            throw t;
        }
    }

    /**
     * 内部公开的静态类，用于封装AgentRunnerSupport的参数
     */
    public static class AgentParam {
        private AgentContext context;
        private AdapterConfig agentConfig;
        private AdapterConfig config;
        private String requestRaw;
        private String toolName;
        private AdapterConfig request;

        public AgentParam(AgentContext context, AdapterConfig agentConfig, String requestRaw, String toolName)
                throws Exception {
            this.context = context;
            this.agentConfig = agentConfig;
            this.config = agentConfig.getBean("config");
            this.requestRaw = requestRaw;
            this.request = context.createAdapterConfig(mapper.readValue(requestRaw, Document.class));
            this.toolName = toolName;
        }

        public AgentContext getContext() {
            return context;
        }

        public AdapterConfig getAgentConfig() {
            return agentConfig;
        }

        public AdapterConfig getConfig() {
            return config;
        }

        public String getRequestRaw() {
            return requestRaw;
        }

        public AdapterConfig getRequest() {
            return request;
        }

        public String getToolName() {
            return toolName;
        }
    }

}
