package com.mttk.orche.llmExecute;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bson.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatMessage;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.agent.MessageRole;
import com.mttk.orche.addon.agent.ToolCall;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractService;
import com.mttk.orche.service.LlmExecuteService;
import com.mttk.orche.service.LlmModelService;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.StringUtil;
import com.mttk.orche.util.ThrowableUtil;

@ServiceFlag(key = "llmExecuteService", name = "LLM执行服务", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class LlmExecuteImplService extends AbstractService implements LlmExecuteService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMinutes(3)) // 连接超时30秒
            .build();

    @Override
    public ChatMessage call(AgentContext context, String modelId, String name, List<ChatMessage> messages,
            List<String> functions)
            throws Exception {
        // 生成请求的唯一标识号
        String requestId = StringUtil.getUUID();
        // 得到配置
        Document modelConfig = getModelConfigById(modelId);

        // 发送LLM开始消息
        name = StringUtil.isEmpty(name) ? "LLM" : name;
        //
        context.sendResponse(new ChatResonseMessage("_llm-start", requestId, name));
        // 构建请求体
        String requestBody = buildRequestBody(modelConfig, messages, functions);

        // 发送请求数据消息
        context.sendResponse(new ChatResonseMessage("_llm-request", requestId, requestBody));

        // 创建HTTP请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(modelConfig.getString("apiBaseUrl")))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + modelConfig.getString("apiKey"))
                .header("Accept", "text/event-stream")
                .timeout(Duration.ofMinutes(5))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, java.nio.charset.StandardCharsets.UTF_8))
                .build();

        // 创建响应收集器
        ResponseCollector responseCollector = new ResponseCollector(context);

        // 完全异步处理SSE流
        CompletableFuture<Void> future = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(response -> {
                    // 检查HTTP状态码
                    if (response.statusCode() != 200) {
                        // 读取服务器返回的错误数据
                        String errorBody = "";
                        try {
                            errorBody = new String(response.body().readAllBytes(),
                                    java.nio.charset.StandardCharsets.UTF_8);
                        } catch (Exception e) {
                            errorBody = "无法读取错误响应体" + ThrowableUtil.dumpInfo(e);
                        }

                        String errorMessage = "HTTP请求失败，状态码: " + response.statusCode();
                        if (!errorBody.isEmpty()) {
                            errorMessage += "，服务器响应: " + errorBody;
                        }
                        throw new RuntimeException(errorMessage);
                    }
                    return response;
                })
                .thenApply(HttpResponse::body)
                .thenAccept(inputStream -> {
                    try (java.util.Scanner scanner = new java.util.Scanner(inputStream,
                            java.nio.charset.StandardCharsets.UTF_8)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            responseCollector.consume(line, requestId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("处理SSE流失败", e);
                    }
                })
                .exceptionally(throwable -> {
                    context.sendResponse(
                            new ChatResonseMessage("_llm-error", requestId, ThrowableUtil.dumpInfo(throwable)));
                    throw new RuntimeException("HTTP请求失败: " + throwable.getMessage(), throwable);
                });

        // 等待异步操作完成
        future.join();

        // 发送LLM结束消息
        context.sendResponse(new ChatResonseMessage("_llm-end", requestId));

        // 返回收集到的响应
        return responseCollector.buildResponseMessage();

    }

    // 根据modelId得到config
    private Document getModelConfigById(String modelId) throws Exception {
        if (modelId == null || modelId.trim().isEmpty()) {
            throw new IllegalArgumentException("模型ID不能为空");
        }
        LlmModelService llmModelService = ServerUtil.getService(LlmModelService.class);
        return llmModelService.load(modelId).get();
    }

    /**
     * 构建请求体JSON
     */
    private String buildRequestBody(Document modelConfig, List<ChatMessage> messages, List<String> functions)
            throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestBody = mapper.createObjectNode();

        // 设置模型参数（从modelConfig读取）
        requestBody.put("model", modelConfig.getString("modelName"));
        requestBody.put("stream", true);

        // 处理temperature参数

        Object temperatureObj = modelConfig.get("temperature");
        double temperature = temperatureObj != null
                ? (temperatureObj instanceof Double ? (Double) temperatureObj
                        : temperatureObj instanceof Integer ? ((Integer) temperatureObj).doubleValue() : 0.0)
                : 0.0;
        requestBody.put("temperature", temperature);

        // 处理maxTokens参数
        Object maxTokensObj = modelConfig.get("maxTokens");
        int maxTokens = maxTokensObj != null
                ? (maxTokensObj instanceof Integer ? (Integer) maxTokensObj
                        : maxTokensObj instanceof Long ? ((Long) maxTokensObj).intValue() : 65536)
                : 65536;
        requestBody.put("max_tokens", maxTokens);

        // 设置消息
        ArrayNode messagesArray = requestBody.putArray("messages");
        for (ChatMessage message : messages) {
            ObjectNode messageNode = messagesArray.addObject();
            messageNode.put("role", message.getRole().getValue());
            messageNode.put("content", message.getContent());

            if (message.getName() != null) {
                messageNode.put("name", message.getName());
            }

            if (message.getRole() == MessageRole.TOOL && message.getToolCallId() != null) {
                messageNode.put("tool_call_id", message.getToolCallId());
            }

            // 如果是Assistant消息且有toolCalls，添加tool_calls字段
            if (message.getRole() == MessageRole.ASSISTANT && message.getToolCalls() != null
                    && !message.getToolCalls().isEmpty()) {
                ArrayNode toolCallsArray = messageNode.putArray("tool_calls");
                for (ToolCall toolCall : message.getToolCalls()) {
                    ObjectNode toolCallNode = toolCallsArray.addObject();
                    toolCallNode.put("index", toolCall.getIndex());
                    toolCallNode.put("id", toolCall.getId());
                    toolCallNode.put("type", toolCall.getType());

                    ObjectNode functionNode = toolCallNode.putObject("function");
                    functionNode.put("name", toolCall.getFunctionName());
                    functionNode.put("arguments", toolCall.getArguments());
                }
            }
        }

        // 设置工具函数
        if (functions != null && !functions.isEmpty()) {
            com.fasterxml.jackson.databind.node.ArrayNode toolsArray = requestBody.putArray("tools");
            for (String function : functions) {
                com.fasterxml.jackson.databind.node.ObjectNode toolNode = toolsArray.addObject();
                toolNode.put("type", "function");
                toolNode.putIfAbsent("function", mapper.readValue(function, JsonNode.class));

            }

            requestBody.put("tool_choice", "auto");
        }

        return objectMapper.writeValueAsString(requestBody);
    }

}