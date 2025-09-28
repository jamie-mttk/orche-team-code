package com.mttk.orche.agent.mcp.util.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mttk.orche.agent.mcp.util.common.MCPTransport;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * HTTP传输层实现
 * 基于HTTP协议的MCP传输
 */
public class HTTPTransport implements MCPTransport {

    private final String serverUrl;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public HTTPTransport(String serverUrl) {
        this.serverUrl = serverUrl;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public JsonNode sendRequest(JsonNode request) throws Exception {
        // return sendRequestInternal(request);
        // }

        // private JsonNode sendRequestInternal(JsonNode request) throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json, text/event-stream")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode >= 200 && statusCode < 300) {
            JsonNode jsonResponse = objectMapper.readTree(responseBody);

            // 检查JSON-RPC错误
            if (jsonResponse.has("error")) {
                throw new RuntimeException("JSON-RPC错误: " + jsonResponse.get("error").toString());
            }

            return jsonResponse;
        } else {
            throw new IOException("HTTP错误: " + statusCode + " - " + responseBody);
        }
    }

    @Override
    public void testConnection() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl))
                .header("Accept", "application/json, text/event-stream")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            String responseBody = response.body();
            throw new IOException("HTTP连接测试失败: " + statusCode + (responseBody != null ? " - " + responseBody : ""));
        }
    }

    @Override
    public void close() throws IOException {
        // HTTP连接不需要显式关闭，但为了符合Closeable接口，我们提供一个空实现
        // HttpClient会自动管理连接池
    }
}
