package com.mttk.orche.addon.agent.impl;

import org.bson.Document;

import com.mttk.orche.addon.agent.ToolCall;
import com.mttk.orche.support.JsonUtil;
import com.mttk.orche.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseReactAgentUtil {

    /**
     * BaseReact解析结果类
     */
    public static class BaseReactResult {
        private String thinking;
        private Object tasks;
        private List<ToolCall> toolCalls;

        public BaseReactResult(String thinking, Object tasks, List<ToolCall> toolCalls) {
            this.thinking = thinking;
            this.tasks = tasks;
            this.toolCalls = toolCalls;
        }

        public String getThinking() {
            return thinking;
        }

        public Object getTasks() {
            return tasks;
        }

        public List<ToolCall> getToolCalls() {
            return toolCalls;
        }

        @Override
        public String toString() {
            return "BaseReactResult{" +
                    "thinking='" + thinking + '\'' +
                    ", tasks=" + tasks +
                    ", toolCalls=" + toolCalls +
                    '}';
        }
    }

    /**
     * 从JSON字符串解析BaseReactResult
     * 
     * @param jsonStr JSON字符串
     * @return BaseReactResult对象
     */
    public static BaseReactResult parseBaseReactResult(String jsonStr) throws Exception {

        jsonStr = JsonUtil.cleanJsonString(jsonStr);
        if (StringUtil.isEmpty(jsonStr)) {
            return null;
        }
        // 解析JSON字符串为Document对象
        Document doc = Document.parse(jsonStr);

        // 解析thinking字段
        String thinking = doc.getString("thinking");

        // 解析tasks字段（可能为空或不存在）
        Object tasks = doc.get("tasks");

        // 解析tool_calls字段
        List<ToolCall> toolCalls = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Document> toolCallDocs = (List<Document>) doc.get("tool_calls");
        if (toolCallDocs != null) {
            for (Document toolCallDoc : toolCallDocs) {
                ToolCall toolCall = parseToolCall(toolCallDoc);
                if (toolCall != null) {
                    toolCalls.add(toolCall);
                }
            }
        }

        return new BaseReactResult(thinking, tasks, toolCalls);
    }

    /**
     * 从JSON对象解析ToolCall
     * 
     * @param toolCallDoc ToolCall的Document对象
     * @return ToolCall对象
     */
    public static ToolCall parseToolCall(Document toolCallDoc) throws Exception {
        if (toolCallDoc == null) {
            return null;
        }

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
    }

    /**
     * 将BaseReactResult转换为Markdown格式描述
     * 
     * @param result                 BaseReactResult对象
     * @param lastToolCallResultsMap 工具调用执行结果Map，key为任务ID，value为执行结果
     * @return Markdown格式的描述字符串
     */
    public static String resultToMarkdown(BaseReactResult result, Map<String, String> lastToolCallResultsMap) {
        if (result == null) {
            return "";
        }

        StringBuilder markdown = new StringBuilder();

        // 1. 思考过程
        if (!StringUtil.isEmpty(result.getThinking())) {
            markdown.append("## 思考过程\n\n");
            markdown.append(result.getThinking()).append("\n\n");
        }

        // 2. 任务列表
        if (result.getTasks() != null && !StringUtil.isEmpty(result.getTasks().toString().trim())) {
            markdown.append("## 任务列表\n\n");
            markdown.append(result.getTasks().toString()).append("\n\n");
        }

        // 3. 工具调用
        String toolCallsMarkdown = formatToolCallsToMarkdown(result.getToolCalls(), lastToolCallResultsMap);
        if (!StringUtil.isEmpty(toolCallsMarkdown)) {
            markdown.append(toolCallsMarkdown);
        }

        return markdown.toString();
    }

    /**
     * 格式化工具调用列表为Markdown
     * 
     * @param toolCalls  工具调用列表
     * @param resultsMap 执行结果Map，key为任务ID，value为执行结果（可为null）
     * @return Markdown格式的工具调用字符串
     */
    public static String formatToolCallsToMarkdown(List<ToolCall> toolCalls, Map<String, String> resultsMap) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            return "";
        }

        StringBuilder markdown = new StringBuilder();
        markdown.append("## 工具调用\n\n");

        for (ToolCall toolCall : toolCalls) {
            String executeResult = null;
            if (resultsMap != null && resultsMap.containsKey(toolCall.getId())) {
                executeResult = resultsMap.get(toolCall.getId());
            }
            markdown.append(formatSingleToolCallToMarkdown(toolCall, executeResult));
        }

        return markdown.toString();
    }

    /**
     * 格式化单个工具调用为Markdown
     * 
     * @param toolCall      工具调用对象
     * @param executeResult 执行结果（可为null）
     * @return Markdown格式的单个工具调用字符串
     */
    public static String formatSingleToolCallToMarkdown(ToolCall toolCall, String executeResult) {
        StringBuilder markdown = new StringBuilder();

        // 任务编号
        markdown.append("### ").append(toolCall.getId()).append("\n\n");

        // 解析工具名称
        String functionName = toolCall.getFunctionName();
        String toolId = "";
        String toolName = functionName;
        if (functionName != null && functionName.contains("__")) {
            String[] parts = functionName.split("__", 2);
            toolId = parts[0];
            toolName = parts.length > 1 ? parts[1] : functionName;
        }

        markdown.append("- **工具编号**: ").append(toolId).append("\n");
        markdown.append("- **工具名称**: ").append(toolName).append("\n");

        // 格式化参数
        markdown.append(formatArgumentsToMarkdown(toolCall.getArguments()));

        // 执行结果
        if (executeResult != null) {
            markdown.append("- **执行结果**:\n\n");
            markdown.append("```markdown\n");
            markdown.append(executeResult);
            markdown.append("\n```\n\n");
        }

        return markdown.toString();
    }

    /**
     * 格式化参数为Markdown
     * 
     * @param arguments 参数JSON字符串
     * @return Markdown格式的参数字符串
     */
    public static String formatArgumentsToMarkdown(String arguments) {
        StringBuilder markdown = new StringBuilder();

        if (StringUtil.isEmpty(arguments) || arguments.trim().equals("{}")) {
            markdown.append("- **参数**: 无参数\n\n");
        } else {
            markdown.append("- **参数**:\n\n");
            try {
                // 解析参数为表格
                Document argsDoc = Document.parse(arguments);
                markdown.append("| 参数名 | 参数值 |\n");
                markdown.append("|--------|--------|\n");
                for (String key : argsDoc.keySet()) {
                    Object value = argsDoc.get(key);
                    String valueStr = value != null ? value.toString() : "";
                    // 转义markdown表格中的特殊字符
                    valueStr = valueStr.replace("|", "\\|").replace("\n", "<br>");
                    markdown.append("| ").append(key).append(" | ").append(valueStr).append(" |\n");
                }
                markdown.append("\n");
            } catch (Exception e) {
                // 如果解析失败，直接输出原始参数
                markdown.append("```\n").append(arguments).append("\n```\n\n");
            }
        }

        return markdown.toString();
    }
}
