package com.mttk.orche.addon.agent.impl;

import java.util.Stack;

import org.bson.Document;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatMemory;
import com.mttk.orche.addon.agent.ChatMessage;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.agent.MessageRole;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.util.StringUtil;

public abstract class BaseReactAgent extends AbstractAgent {
    // private static final Logger logger =
    // LoggerFactory.getLogger(BaseReactAgent.class);

    public abstract String getSystemPrompt(AgentParam para) throws Exception;

    public abstract String getNextStepPrompt(AgentParam para) throws Exception;

    @Override
    public String execute(AgentContext context, AdapterConfig agentConfig, String requestRaw,
            String toolName) throws Exception {

        //
        Stack<ChatMemory> chatMemoryStack = context.createChatMemory();
        ChatMemory chatMemory = chatMemoryStack.peek();
        AgentParam para = new AgentParam(context, agentConfig, requestRaw, null);
        //
        String query = para.getRequest().getString("query");

        //
        String systemPrompt = getSystemPrompt(para);

        // systemPrompt = systemPrompt.replace("${now}", AgentUtil.now());
        systemPrompt = PromptUtil.parsePrompt(systemPrompt, para);

        //
        chatMemory.addMessage(ChatMessage.system(systemPrompt));
        chatMemory.addMessage(ChatMessage.user(query));
        //
        executeByStep(para, chatMemory);
        //
        chatMemoryStack.pop();
        //
        return null;
    }

    private void executeByStep(
            AgentParam para, ChatMemory chatMemory) throws Exception {
        //
        String nextStepPrompt = getNextStepPrompt(para);
        //
        for (int count = 1; count <= para.getConfig().getInteger("maxSteps", 10); count++) {
            //
            para.getContext().cancelCheck();
            //
            String name = para.getAgentConfig().getString("name");
            name += " (第" + count + "轮)";
            //
            String transactionId = StringUtil.getUUID();
            //
            Document agentStartData = new Document();
            agentStartData.append("name", name);
            agentStartData.append("agentConfig", para.getAgentConfig().toMap());
            agentStartData.append("request", para.getRequest().toMap());
            //
            para.getContext()
                    .sendResponse(new ChatResonseMessage("_agent-start", transactionId, agentStartData.toJson()));
            para.getContext()
                    .sendResponse(new ChatResonseMessage("_data-content", transactionId,
                            para.getRequest().getString("query")));
            //
            // logger.info("######开始第 {} 轮执行", count);
            //
            if (chatMemory.getLastMessage().getRole() != MessageRole.USER) {
                chatMemory.addMessage(ChatMessage.user(nextStepPrompt));
            }
            //
            // LLMExecutor llmExecutor = new
            // LLMExecutor(para.getConfig().getString("model"));
            ChatMessage response = AgentUtil.callLlm(para
                    .getContext(), para.getConfig().getString("model"), "任务规划", chatMemory.getMessages(),
                    AgentUtil.getFunctions(para.getContext(), para.getAgentConfig()));
            // assistant message
            chatMemory.addMessage(response);
            //
            if (response.getToolCalls().isEmpty()) {
                // logger.info("没有任何工具调用需要执行完成");
                //
                para.getContext().sendResponse(new ChatResonseMessage("_agent-end", transactionId));
                //
                break;
            } else {
                // 执行工具
                AgentUtil.handleTools(para.getContext(), response, chatMemory);
                //
                //
                para.getContext().sendResponse(new ChatResonseMessage("_agent-end", transactionId));
            }

        }

    }

}
