package com.mttk.orche.llmExecute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatMessage;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.agent.MessageRole;
import com.mttk.orche.addon.agent.ToolCall;

/**
 * 响应收集器，用于异步收集SSE流响应
 */
class ResponseCollector {
    private final StringBuilder contentBuilder = new StringBuilder();
    private final Map<Integer, PartialToolCall> partialToolCalls = new HashMap<>();
    private final List<ToolCall> finalToolCalls = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private AgentContext context;

    public ResponseCollector(AgentContext context) {

        this.context = context;
    }

    public void consume(String line, String requestId) throws Exception {
        if (line == null || !line.startsWith("data: ")) {
            return;
        }

        line = line.substring(6);
        if ("[DONE]".equals(line)) {
            return;
        }
        // 发送内容增量消息，直接传递LLM原始内容
        context.sendResponse(new ChatResonseMessage("_llm-response-delta", requestId, line));
        //

        JsonNode dataNode = objectMapper.readTree(line);
        JsonNode choicesNode = dataNode.get("choices");
        if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
            JsonNode choice = choicesNode.get(0);
            JsonNode delta = choice.get("delta");

            if (delta == null) {
                return;
            }
            // 处理内容增量 - 直接传递原始内容
            JsonNode contentNode = delta.get("content");
            if (contentNode != null && !contentNode.isNull()) {
                String content = contentNode.asText();
                contentBuilder.append(content);
            }

            // 处理工具调用，但不发送反馈消息
            JsonNode toolCallsNode = delta.get("tool_calls");
            if (toolCallsNode != null && toolCallsNode.isArray()) {
                for (JsonNode toolCallNode : toolCallsNode) {
                    processPartialToolCall(toolCallNode);
                }
            }
        }

    }

    /**
     * 处理部分工具调用，实现增量合并
     */
    private void processPartialToolCall(JsonNode toolCallNode) {

        // 获取index，这是合并的关键
        JsonNode indexNode = toolCallNode.get("index");
        if (indexNode == null || indexNode.isNull()) {
            return;
        }
        int index = indexNode.asInt();

        // 获取或创建部分工具调用对象
        PartialToolCall partialCall = partialToolCalls.computeIfAbsent(index, k -> {
            PartialToolCall newCall = new PartialToolCall();
            newCall.index = index;
            return newCall;
        });

        // 更新id（如果存在）
        JsonNode idNode = toolCallNode.get("id");
        if (idNode != null && !idNode.isNull() && !idNode.asText().isEmpty()) {
            partialCall.id = idNode.asText();
        }

        // 更新type（如果存在）
        JsonNode typeNode = toolCallNode.get("type");
        if (typeNode != null && !typeNode.isNull()) {
            partialCall.type = typeNode.asText();
        }

        // 更新function信息
        JsonNode functionNode = toolCallNode.get("function");
        if (functionNode != null && !functionNode.isNull()) {
            // 更新name
            JsonNode nameNode = functionNode.get("name");
            if (nameNode != null && !nameNode.isNull()) {
                partialCall.functionName = nameNode.asText();
            }

            // 增量合并arguments
            JsonNode argumentsNode = functionNode.get("arguments");
            if (argumentsNode != null && !argumentsNode.isNull()) {
                String arguments = argumentsNode.asText();
                if (arguments != null && !arguments.isEmpty()) {
                    partialCall.argumentsBuilder.append(arguments);
                }
            }
        }

    }

    /**
     * 构建最终的响应消息
     */
    public ChatMessage buildResponseMessage() {
        // 将部分工具调用转换为最终工具调用
        for (PartialToolCall partialCall : partialToolCalls.values()) {
            if (partialCall.isValid()) {
                ToolCall toolCall = new ToolCall(
                        partialCall.index,
                        partialCall.id,
                        partialCall.type,
                        partialCall.functionName,
                        partialCall.argumentsBuilder.toString());
                finalToolCalls.add(toolCall);
            }
        }

        // 创建并返回ChatMessage对象
        ChatMessage responseMessage = new ChatMessage(MessageRole.ASSISTANT, contentBuilder.toString());

        // 如果有工具调用，添加到消息中
        if (!finalToolCalls.isEmpty()) {
            responseMessage.getToolCalls().addAll(finalToolCalls);
        }

        return responseMessage;
    }

    /**
     * 部分工具调用，用于增量合并
     */
    private class PartialToolCall {
        int index;
        String id = "";
        String type = "function";
        String functionName = "";
        StringBuilder argumentsBuilder = new StringBuilder();

        boolean isValid() {
            return !functionName.isEmpty() && argumentsBuilder.length() > 0;
        }
    }
}