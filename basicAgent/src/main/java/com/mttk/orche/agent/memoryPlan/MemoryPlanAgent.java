package com.mttk.orche.agent.memoryPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatMessage;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.agent.ToolCall;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.agent.impl.PromptUtil;
import com.mttk.orche.addon.agent.impl.ToolDefineReadyAgent;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.service.support.LlmExecuteEventListener;
import com.mttk.orche.support.JsonUtil;
import com.mttk.orche.util.StringUtil;
import com.mttk.orche.util.ThrowableUtil;

@AgentTemplateFlag(key = "_memory-plan-agent", name = "记录执行计划智能体", description = """
        按照最大轮次循环,每次循环体里面调用大模型然后执行工具.\n系统记录每次执行任务结果
        """, props = "SUPPORT_MEMBER", i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "maxSteps", label = "最大轮次", description = "思考和执行的轮次数", size = 1, defaultVal = "10", props = {
        "dataType:number" })
@Control(key = "model", label = "模型", mode = "select", size = 1, mandatory = true, props = { "url:llmModel/query" })

public class MemoryPlanAgent extends ToolDefineReadyAgent {

    @Override
    public String execute(AgentContext context, AdapterConfig agentConfig, String requestRaw,
            String toolName) throws Exception {

        // 初始化参数
        AgentParam para = new AgentParam(context, agentConfig, requestRaw, null);
        String query = para.getRequest().getString("query");

        int maxSteps = para.getConfig().getInteger("maxSteps", 10);
        ExecutionPlan plan = new ExecutionPlan();

        for (int i = 1; i <= maxSteps; i++) {
            context.cancelCheck();
            // 检查是否所有任务完成,注意第一轮为空不能检查
            if (i != 1 && plan.isAllCompleted()) {
                break;
            }
            // 开始新一轮
            String transactionId = StringUtil.getUUID();
            sendAgentStart(context, transactionId, agentConfig, para, "(第" + i + "轮)");
            // 创建系统提示词
            // 调用大模型
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.system(buildProperPrompt(para, plan, i)));
            messages.add(ChatMessage.user(query));
            ChatMessage responseMessage = AgentUtil.callLlm(para, "规划任务和执行", messages, null,
                    new LlmExecuteEventListener() {
                        @Override
                        public void onResponse(String requestId, ChatMessage responseMessage) throws Exception {
                            MemoryPlanResponse r = new MemoryPlanResponse(responseMessage.getContent());
                            para.getContext().sendResponse(new ChatResonseMessage("_data-content",
                                    requestId, r.toMarkDown()));
                        }
                    });

            //
            MemoryPlanResponse response = new MemoryPlanResponse(responseMessage.getContent());
            plan.update(response);
            // 执行工具
            if (response.getToolCalls().isEmpty()) {
                // logger.info("没有任何工具调用需要执行完成");
                //
                para.getContext().sendResponse(new ChatResonseMessage("_agent-end", transactionId));
                //
                break;
            } else {
                // 执行工具
                for (ToolCall toolCall : response.getToolCalls()) {
                    try {
                        String result = AgentUtil.handleTool(para.getContext(), toolCall);
                        // 更新任务
                        plan.updateCall(toolCall, result);
                    } catch (Exception e) {
                        plan.updateCallError(toolCall, ThrowableUtil.dumpSimple(e));
                    }
                }
            }
            //
            sendAgentEnd(context, transactionId, "### 计划状态\n" + plan.toReadableText());

        }
        //
        return null;
    }

    // 根据轮次创建不同的提示词
    private String buildProperPrompt(AgentParam para, ExecutionPlan plan, int round) {
        if (round == 1) {
            return PromptUtil.parsePrompt(MemoryPlanPrompt.SYSTEM_PROMPT, para);
        } else {
            String str = PromptUtil.parsePrompt(MemoryPlanPrompt.NEXT_STEP_PROMPT, para,
                    Map.of("executionPlan", plan.toReadableText()));

            return str;
        }
    }

    /**
     * 发送agent-start事件
     */
    private void sendAgentStart(AgentContext context, String transactionId,
            AdapterConfig agentConfig, AgentParam para, String roundInfo) {
        String name = agentConfig.getString("name") + " " + roundInfo;
        Document agentStartData = new Document();
        agentStartData.append("name", name);
        agentStartData.append("agentConfig", para.getAgentConfig().toMap());
        agentStartData.append("request", para.getRequest().toMap());

        context.sendResponse(new ChatResonseMessage("_agent-start", transactionId, agentStartData.toJson()));
        // context.sendResponse(
        // new ChatResonseMessage("_data-content", transactionId,
        // para.getRequest().getString("query")));
    }

    /**
     * 发送agent-end事件
     */
    private void sendAgentEnd(AgentContext context, String transactionId, String message) {
        context.sendResponse(new ChatResonseMessage("_agent-end", transactionId, message));
    }
}
