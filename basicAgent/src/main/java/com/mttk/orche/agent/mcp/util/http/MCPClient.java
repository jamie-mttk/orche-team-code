package com.mttk.orche.agent.mcp.util.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 真正的MCP (Model Context Protocol) 客户端实现
 * 基于JSON-RPC 2.0协议和Streamable HTTP transport
 */
public class MCPClient {

    private final String serverUrl;
    private final ObjectMapper objectMapper;
    private final AtomicLong requestId = new AtomicLong(1);

    public MCPClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 初始化MCP连接
     * 发送initialize请求并处理响应
     */
    public Map<String, Object> initialize() {
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("jsonrpc", "2.0");
            request.put("id", requestId.getAndIncrement());
            request.put("method", "initialize");

            ObjectNode params = objectMapper.createObjectNode();
            params.put("protocolVersion", "2024-11-05");
            params.set("capabilities", objectMapper.createObjectNode());
            ObjectNode clientInfo = objectMapper.createObjectNode();
            clientInfo.put("name", "Java MCP Client");
            clientInfo.put("version", "1.0.0");
            params.set("clientInfo", clientInfo);

            request.set("params", params);

            JsonNode response = sendRequest(request);

            Map<String, Object> result = new HashMap<>();
            result.put("response", response);
            result.put("status", "success");

            return result;
        } catch (Exception e) {
            System.err.println("MCP初始化失败: " + e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            errorResult.put("status", "error");
            return errorResult;
        }
    }

    /**
     * 获取MCP服务器支持的工具列表
     */
    public Map<String, Object> listTools() {
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("jsonrpc", "2.0");
            request.put("id", requestId.getAndIncrement());
            request.put("method", "tools/list");

            JsonNode response = sendRequest(request);

            Map<String, Object> result = new HashMap<>();
            result.put("tools", response);
            result.put("status", "success");

            return result;
        } catch (Exception e) {
            System.err.println("获取工具列表失败: " + e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            errorResult.put("status", "error");
            return errorResult;
        }
    }

    /**
     * 调用MCP工具
     */
    public Map<String, Object> callTool(String toolName, Map<String, Object> arguments) {
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("jsonrpc", "2.0");
            request.put("id", requestId.getAndIncrement());
            request.put("method", "tools/call");

            ObjectNode params = objectMapper.createObjectNode();
            params.put("name", toolName);
            params.set("arguments", objectMapper.valueToTree(arguments));

            request.set("params", params);

            JsonNode response = sendRequest(request);

            Map<String, Object> result = new HashMap<>();
            result.put("tool", toolName);
            result.put("arguments", arguments);
            result.put("response", response);
            result.put("status", "success");

            return result;
        } catch (Exception e) {
            System.err.println("调用工具失败: " + e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("tool", toolName);
            errorResult.put("error", e.getMessage());
            errorResult.put("status", "error");
            return errorResult;
        }
    }

    /**
     * 获取MCP服务器支持的资源列表
     */
    public Map<String, Object> listResources() {
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("jsonrpc", "2.0");
            request.put("id", requestId.getAndIncrement());
            request.put("method", "resources/list");

            JsonNode response = sendRequest(request);

            Map<String, Object> result = new HashMap<>();
            result.put("resources", response);
            result.put("status", "success");

            return result;
        } catch (Exception e) {
            System.err.println("获取资源列表失败: " + e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            errorResult.put("status", "error");
            return errorResult;
        }
    }

    /**
     * 读取MCP资源
     */
    public Map<String, Object> readResource(String uri) {
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("jsonrpc", "2.0");
            request.put("id", requestId.getAndIncrement());
            request.put("method", "resources/read");

            ObjectNode params = objectMapper.createObjectNode();
            params.put("uri", uri);

            request.set("params", params);

            JsonNode response = sendRequest(request);

            Map<String, Object> result = new HashMap<>();
            result.put("uri", uri);
            result.put("response", response);
            result.put("status", "success");

            return result;
        } catch (Exception e) {
            System.err.println("读取资源失败: " + e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("uri", uri);
            errorResult.put("error", e.getMessage());
            errorResult.put("status", "error");
            return errorResult;
        }
    }

    /**
     * 发送JSON-RPC请求
     */
    private JsonNode sendRequest(ObjectNode request) throws IOException {
        String jsonRequest = objectMapper.writeValueAsString(request);
        System.out.println("发送请求到: " + serverUrl);
        System.out.println("请求内容: " + jsonRequest);

        URL url = URI.create(serverUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json, text/event-stream");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);

            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode >= 200 && responseCode < 300) {
                String responseBody = response.toString();
                System.out.println("服务器响应: " + responseBody);

                // 尝试解析JSON响应
                try {
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);

                    // 检查JSON-RPC错误
                    if (jsonResponse.has("error")) {
                        throw new RuntimeException("JSON-RPC错误: " + jsonResponse.get("error").toString());
                    }

                    return jsonResponse;
                } catch (Exception e) {
                    // 如果响应不是JSON格式，可能是其他格式的响应
                    System.out.println("响应不是JSON格式，原始响应: " + responseBody);
                    throw new RuntimeException("无法解析服务器响应为JSON: " + e.getMessage());
                }
            } else {
                throw new IOException("HTTP错误: " + responseCode + " - " + response.toString());
            }

        } finally {
            connection.disconnect();
        }
    }

    /**
     * 测试MCP服务器连接
     * 
     * @throws Exception 连接测试失败时抛出详细错误信息
     */
    public void testConnection() throws Exception {
        // 首先测试HTTP连接
        URL url = URI.create(serverUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode < 200 || responseCode >= 300) {
                // 读取错误响应信息
                String errorMessage = "HTTP连接测试失败: " + responseCode;
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    if (errorResponse.length() > 0) {
                        errorMessage += " - " + errorResponse.toString();
                    }
                } catch (Exception ignored) {
                    // 如果无法读取错误流，使用默认错误信息
                }
                throw new IOException(errorMessage);
            }
        } finally {
            connection.disconnect();
        }

        // 然后测试MCP协议初始化
        Map<String, Object> result = initialize();
        if (!"success".equals(result.get("status"))) {
            throw new RuntimeException("MCP协议初始化失败: " + result.get("message"));
        }
    }
}
