package com.mttk.orche.agent.memoryPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.agent.ToolCall;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.agent.impl.PromptUtil;
import com.mttk.orche.addon.agent.impl.ToolDefineReadyAgent;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;
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

        // 创建执行计划
        ExecutionPlan plan = new ExecutionPlan();

        // 获取工具定义列表
        List<String> functions = AgentUtil.getFunctions(context, agentConfig);
        String toolsInfo = formatToolsInfo(functions);

        // ========== 第一次执行：制定计划 ==========
        Map<String, Object> vars = new HashMap<>();
        vars.put("query", query);
        vars.put("tools", toolsInfo);
        String systemPrompt = PromptUtil.parsePrompt(MemoryPlanPrompt.SYSTEM_PROMPT, para, vars);

        // 第一轮执行
        String transactionId = StringUtil.getUUID();
        sendAgentStart(context, transactionId, agentConfig, para, "(第1轮)");

        // 调用大模型
        String firstResponse = AgentUtil.callLlm(para, "任务规划", systemPrompt);

        // 解析第一次响应，提取执行计划和工具调用信息
        List<ToolCall> firstToolCalls = parseInitialPlanFromJson(para, plan, firstResponse);

        // 执行第一个工具调用
        if (!firstToolCalls.isEmpty()) {
            executeToolCalls(para, plan, firstToolCalls);
        }

        sendAgentEnd(context, transactionId);

        // ========== 后续轮次：执行任务 ==========
        for (int i = 2; i <= maxSteps; i++) {
            context.cancelCheck();

            // 检查是否所有任务完成
            if (plan.isAllCompleted()) {
                break;
            }

            // 开始新一轮
            transactionId = StringUtil.getUUID();
            sendAgentStart(context, transactionId, agentConfig, para, "(第" + i + "轮)");

            // 构造下一步提示
            vars = new HashMap<>();
            vars.put("query", query);
            vars.put("tools", toolsInfo);
            vars.put("executionPlan", plan.toReadableText());
            String nextPrompt = PromptUtil.parsePrompt(MemoryPlanPrompt.NEXT_STEP_PROMPT, para, vars);

            // 调用大模型
            String response = AgentUtil.callLlm(para, "执行任务", nextPrompt);

            // 解析响应JSON,获取工具调用信息
            List<ToolCall> toolCalls = new ArrayList<>();
            if (!StringUtil.isEmpty(response)) {
                toolCalls = processNextStepResponse(para, plan, response);
            }

            // 检查是否有工具调用
            if (toolCalls.isEmpty()) {
                // 没有更多工具调用，结束
                sendAgentEnd(context, transactionId);
                break;
            }

            // 执行工具调用
            executeToolCalls(para, plan, toolCalls);

            sendAgentEnd(context, transactionId);
        }

        return null;
    }

    /**
     * 格式化工具列表信息为易读文本
     */
    private String formatToolsInfo(List<String> functions) {
        if (functions == null || functions.isEmpty()) {
            return "无可用工具";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < functions.size(); i++) {
            if (i > 0) {
                sb.append("\n\n");
            }
            sb.append(functions.get(i));
        }
        return sb.toString();
    }

    /**
     * 解析第一次LLM返回的JSON内容，提取执行计划和工具调用信息
     *
     * @return 工具调用列表
     */
    private List<ToolCall> parseInitialPlanFromJson(AgentParam para, ExecutionPlan plan, String content) {
        List<ToolCall> toolCalls = new ArrayList<>();

        if (StringUtil.isEmpty(content)) {
            return toolCalls;
        }

        try {
            // 清理JSON字符串
            String jsonStr = JsonUtil.cleanJsonString(content);
            Document doc = Document.parse(jsonStr);

            // 提取思考部分
            String thinking = doc.getString("thinking");
            if (!StringUtil.isEmpty(thinking)) {
                plan.setThinking(thinking);
            }

            // 提取任务列表
            @SuppressWarnings("unchecked")
            List<Document> tasks = (List<Document>) doc.get("tasks");
            if (tasks != null) {
                for (Document taskDoc : tasks) {
                    String id = taskDoc.getString("id");
                    String name = taskDoc.getString("name");
                    String tool = taskDoc.getString("tool");

                    PlanItem item = new PlanItem(id, name, tool);
                    plan.addItem(item);
                }
            }

            // 提取toolCall信息
            Document toolCallDoc = (Document) doc.get("toolCall");
            if (toolCallDoc != null) {
                ToolCall toolCall = parseToolCallFromJson(toolCallDoc);
                if (toolCall != null) {
                    toolCalls.add(toolCall);

                    // 更新第一个任务的状态为IN_PROGRESS（使用任务ID查找）
                    PlanItem firstItem = plan.findItemById(toolCall.getId());
                    if (firstItem != null) {
                        firstItem.setStatus(PlanItem.Status.IN_PROGRESS);
                        firstItem.setInputParams(toolCall.getArguments());
                    }
                }
            }
        } catch (Exception e) {
            // JSON解析失败，记录错误
            para.getContext().getLogger().error("Failed to parse initial plan JSON: " + content, e);
        }

        return toolCalls;
    }

    /**
     * 处理后续步骤的响应
     * 
     * @return 工具调用列表
     */
    private List<ToolCall> processNextStepResponse(AgentParam para, ExecutionPlan plan, String content) {
        // 第一步：从响应中解析ToolCall列表
        List<ToolCall> toolCalls = parseToolCallsFromResponse(para, plan, content);

        // 第二步：根据ToolCall列表更新所有对应任务的状态
        updateTaskStatusFromToolCalls(plan, toolCalls);

        return toolCalls;
    }

    /**
     * 从LLM响应中解析ToolCall列表
     * 同时处理重新规划逻辑
     * 
     * @return 工具调用列表
     */
    private List<ToolCall> parseToolCallsFromResponse(AgentParam para, ExecutionPlan plan, String content) {
        List<ToolCall> toolCalls = new ArrayList<>();

        if (StringUtil.isEmpty(content)) {
            return toolCalls;
        }

        try {
            // 清理JSON字符串
            String jsonStr = JsonUtil.cleanJsonString(content);
            Document doc = Document.parse(jsonStr);

            boolean rescheduled = doc.getBoolean("rescheduled", false);

            if (rescheduled) {
                // 场景B：重新规划
                plan.clearPendingItems();

                // 解析新的任务列表
                @SuppressWarnings("unchecked")
                List<Document> tasks = (List<Document>) doc.get("tasks");
                if (tasks != null) {
                    for (Document taskDoc : tasks) {
                        String id = taskDoc.getString("id");
                        String name = taskDoc.getString("name");
                        String tool = taskDoc.getString("tool");

                        // 检查是否已存在（已完成的任务）
                        PlanItem existingItem = plan.findItemById(id);
                        if (existingItem == null) {
                            // 新任务
                            PlanItem item = new PlanItem(id, name, tool);
                            plan.addItem(item);
                        }
                    }
                }
            }

            // 提取toolCall信息（可能是单个或多个）
            Document toolCallDoc = (Document) doc.get("toolCall");
            if (toolCallDoc != null) {
                ToolCall toolCall = parseToolCallFromJson(toolCallDoc);
                if (toolCall != null) {
                    toolCalls.add(toolCall);
                }
            }

            // 注意：如果将来支持多个toolCall，可以在这里解析toolCalls数组

        } catch (Exception e) {
            // JSON解析失败，记录错误
            para.getContext().getLogger().error("Failed to parse tool calls from response: " + content, e);
        }

        return toolCalls;
    }

    /**
     * 根据ToolCall列表更新所有对应任务的状态
     * 将所有待执行的任务状态设置为IN_PROGRESS
     */
    private void updateTaskStatusFromToolCalls(ExecutionPlan plan, List<ToolCall> toolCalls) {
        // 遍历所有ToolCall，更新对应任务的状态
        for (ToolCall toolCall : toolCalls) {
            if (toolCall == null) {
                continue;
            }

            // 使用任务ID查找对应的任务项
            PlanItem item = plan.findItemById(toolCall.getId());
            if (item != null) {
                // 更新状态为执行中
                item.setStatus(PlanItem.Status.IN_PROGRESS);
                item.setInputParams(toolCall.getArguments());
            }
        }
    }

    /**
     * 从JSON对象解析ToolCall
     */
    private ToolCall parseToolCallFromJson(Document toolCallDoc) {
        try {
            String id = toolCallDoc.getString("id");
            String type = toolCallDoc.getString("type");

            Document functionDoc = (Document) toolCallDoc.get("function");
            if (functionDoc == null) {
                return null;
            }

            String name = functionDoc.getString("name");
            String arguments = functionDoc.getString("arguments");

            // 创建ToolCall对象
            return new ToolCall(null, id, type, name, arguments);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 执行工具调用并更新计划
     */
    private void executeToolCalls(AgentParam para, ExecutionPlan plan, List<ToolCall> toolCalls) throws Exception {
        // 逐个执行工具调用，记录每次执行结果
        for (ToolCall toolCall : toolCalls) {
            para.getContext().cancelCheck();

            // 查找对应的计划项（使用任务ID查找，更安全）
            PlanItem item = plan.findItemById(toolCall.getId());

            try {
                // 更新状态为执行中
                if (item != null) {
                    item.setStatus(PlanItem.Status.IN_PROGRESS);
                    item.setInputParams(toolCall.getArguments());
                }

                // 执行工具并获取结果
                String result = AgentUtil.handleTool(para.getContext(), toolCall, null);

                // 更新为成功
                if (item != null) {
                    item.setStatus(PlanItem.Status.COMPLETED);
                    item.setResult(PlanItem.Result.SUCCESS);
                    item.setSuccessOutput(result != null ? result : "");
                }

            } catch (Exception e) {
                // 更新为失败
                if (item != null) {
                    item.setStatus(PlanItem.Status.COMPLETED);
                    item.setResult(PlanItem.Result.FAILED);
                    item.setErrorMessage(ThrowableUtil.dump2String(e));
                }
                // 记录错误日志
                para.getContext().getLogger().error("Tool execution failed: " + toolCall.getId(), e);
            }
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
    private void sendAgentEnd(AgentContext context, String transactionId) {
        context.sendResponse(new ChatResonseMessage("_agent-end", transactionId));
    }
}
