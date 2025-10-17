package com.mttk.orche.agent.memoryPlan;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mttk.orche.addon.agent.ToolCall;
import com.mttk.orche.addon.agent.impl.BaseReactAgentUtil;
import com.mttk.orche.support.JsonUtil;
import com.mttk.orche.util.StringUtil;

/**
 * MemoryPlan响应解析类
 * 用于解析MemoryPlanPrompt中三种类型的输出：init、continue、replan
 */
public class MemoryPlanResponse {

    /**
     * 响应状态枚举
     */
    public enum Status {
        INIT,
        CONTINUE,
        REPLAN
    }

    // 主要字段
    private Status status;
    private String thinking;
    private List<Task> tasks;
    private List<ToolCall> toolCalls;

    /**
     * 构造方法：接收JSON字符串并解析
     * 
     * @param jsonString JSON格式的字符串
     */
    public MemoryPlanResponse(String jsonString) {
        // 使用JsonUtil清理JSON字符串
        String cleanedJson = JsonUtil.cleanJsonString(jsonString);

        // 解析JSON
        Document doc = Document.parse(cleanedJson);

        // 解析status（转为枚举）
        String statusStr = doc.getString("status");
        if (!StringUtil.isEmpty(statusStr)) {
            this.status = Status.valueOf(statusStr.toUpperCase());
        }

        // 解析thinking
        this.thinking = doc.getString("thinking");

        // 初始化列表
        this.tasks = new ArrayList<>();
        this.toolCalls = new ArrayList<>();

        // 解析tasks（如果字段存在）
        if (doc.containsKey("tasks")) {
            @SuppressWarnings("unchecked")
            List<Document> taskDocs = (List<Document>) doc.get("tasks");
            if (taskDocs != null) {
                for (Document taskDoc : taskDocs) {
                    String id = taskDoc.getString("id");
                    String name = taskDoc.getString("name");
                    String tool = taskDoc.getString("tool");
                    tasks.add(new Task(id, name, tool));
                }
            }
        }

        // 解析tool_calls（如果字段存在，是一个数组）
        if (doc.containsKey("tool_calls")) {
            @SuppressWarnings("unchecked")
            List<Document> toolCallDocs = (List<Document>) doc.get("tool_calls");
            if (toolCallDocs != null) {
                for (Document toolCallDoc : toolCallDocs) {
                    try {
                        ToolCall toolCall = BaseReactAgentUtil.parseToolCall(toolCallDoc);
                        if (toolCall != null) {
                            toolCalls.add(toolCall);
                        }
                    } catch (Exception e) {
                        // 忽略解析失败的工具调用
                    }
                }
            }
        }
    }

    // Getter方法

    public Status getStatus() {
        return status;
    }

    public String getThinking() {
        return thinking;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<ToolCall> getToolCalls() {
        return toolCalls;
    }

    /**
     * 将响应转换为Markdown格式
     * 
     * @return Markdown格式的描述字符串
     */
    public String toMarkDown() {
        StringBuilder markdown = new StringBuilder();

        // 1. 状态
        if (status != null) {
            markdown.append("## 状态\n\n");
            markdown.append("**").append(status.name()).append("**\n\n");
        }

        // 2. 思考过程
        if (!StringUtil.isEmpty(thinking)) {
            markdown.append("## 思考过程\n\n");
            markdown.append(thinking).append("\n\n");
        }

        // 3. 任务列表
        if (tasks != null && !tasks.isEmpty()) {
            markdown.append("## 任务列表\n\n");
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                markdown.append((i + 1)).append(". ");
                markdown.append("**").append(task.getId()).append("**: ");
                markdown.append(task.getName());
                markdown.append(" - 工具: `").append(task.getTool()).append("`\n");
            }
            markdown.append("\n");
        }

        // 4. 工具调用
        String toolCallsMarkdown = BaseReactAgentUtil.formatToolCallsToMarkdown(toolCalls, null);
        if (!StringUtil.isEmpty(toolCallsMarkdown)) {
            markdown.append(toolCallsMarkdown);
        }

        return markdown.toString();
    }
}
