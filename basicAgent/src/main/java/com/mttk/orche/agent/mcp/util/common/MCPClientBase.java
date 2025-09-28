package com.mttk.orche.agent.mcp.util.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MCP Client Base Class
 * Implements core MCP protocol functionality, decoupled from transport layer
 */
public abstract class MCPClientBase implements Closeable {

    protected final MCPTransport transport;
    protected final ObjectMapper objectMapper;
    protected final AtomicLong requestId = new AtomicLong(1);

    public MCPClientBase(MCPTransport transport) {
        this.transport = transport;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Initialize MCP connection
     * 
     * @return result节点的内容（转换为字符串）
     * @throws Exception 初始化失败时抛出异常
     */
    public String initialize() throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("jsonrpc", "2.0");
        request.put("id", requestId.getAndIncrement());
        request.put("method", "initialize");

        ObjectNode params = objectMapper.createObjectNode();
        params.put("protocolVersion", "2024-11-05");
        params.put("capabilities", objectMapper.createObjectNode());
        params.put("clientInfo", objectMapper.createObjectNode()
                .put("name", "Java MCP Client")
                .put("version", "1.0.0"));

        request.set("params", params);

        JsonNode response = transport.sendRequest(request);

        // 检查响应是否有错误
        if (response.has("error")) {
            throw new RuntimeException("JSON-RPC错误: " + response.get("error").toString());
        }

        // 获取result节点并转换为字符串
        JsonNode result = response.get("result");
        if (result == null) {
            throw new RuntimeException("响应中缺少result节点");
        }

        return result.toString();
    }

    /**
     * Get list of tools supported by MCP server
     * 
     * @return Collection of tool nodes from the tools array
     * @throws Exception if request fails
     */
    public List<JsonNode> listTools() throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("jsonrpc", "2.0");
        request.put("id", requestId.getAndIncrement());
        request.put("method", "tools/list");

        JsonNode response = transport.sendRequest(request);

        // 检查响应是否有错误
        if (response.has("error")) {
            throw new RuntimeException("JSON-RPC错误: " + response.get("error").toString());
        }

        // 获取result.tools数组
        JsonNode result = response.get("result");
        if (result == null || !result.has("tools")) {
            throw new RuntimeException("响应中缺少tools数组");
        }

        JsonNode toolsArray = result.get("tools");
        if (!toolsArray.isArray()) {
            throw new RuntimeException("tools字段不是数组类型");
        }

        // 将每个工具节点添加到Collection中
        List<JsonNode> tools = new ArrayList<>();
        for (JsonNode tool : toolsArray) {
            tools.add(tool);
        }

        return tools;
    }

    /**
     * Call MCP tool
     * 
     * @param toolName  工具名称
     * @param arguments 工具参数
     * @return result.content节点的内容（字符串）
     * @throws Exception 调用失败时抛出异常
     */
    public String callTool(String toolName, Map<String, Object> arguments) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("jsonrpc", "2.0");
        request.put("id", requestId.getAndIncrement());
        request.put("method", "tools/call");

        ObjectNode params = objectMapper.createObjectNode();
        params.put("name", toolName);
        params.set("arguments", objectMapper.valueToTree(arguments));

        request.set("params", params);

        JsonNode response = transport.sendRequest(request);

        // 检查响应是否有错误
        if (response.has("error")) {
            throw new RuntimeException("JSON-RPC错误: " + response.get("error").toString());
        }

        // 获取result节点
        JsonNode result = response.get("result");
        if (result == null) {
            throw new RuntimeException("响应中缺少result节点");
        }

        // 获取content节点
        JsonNode content = result.get("content");
        if (content == null) {
            throw new RuntimeException("result中缺少content节点");
        }

        // 根据content节点类型返回相应的字符串
        if (content.isTextual()) {
            // 如果是字符串，直接返回
            return content.asText();
        } else if (content.isObject() || content.isArray()) {
            // 如果是JSON对象或数组，返回对应的字符串
            return content.toString();
        } else {
            // 其他情况，调用toString
            return content.toString();
        }
    }

    // /**
    // * Get list of resources supported by MCP server
    // */
    // public Map<String, Object> listResources() {
    // try {
    // ObjectNode request = objectMapper.createObjectNode();
    // request.put("jsonrpc", "2.0");
    // request.put("id", requestId.getAndIncrement());
    // request.put("method", "resources/list");

    // JsonNode response = transport.sendRequest(request);

    // Map<String, Object> result = new HashMap<>();
    // result.put("resources", response);
    // result.put("status", "success");

    // return result;
    // } catch (Exception e) {
    // System.err.println("Failed to get resources list: " + e.getMessage());
    // Map<String, Object> errorResult = new HashMap<>();
    // errorResult.put("error", e.getMessage());
    // errorResult.put("status", "error");
    // return errorResult;
    // }
    // }

    // /**
    // * Read MCP resource
    // */
    // public Map<String, Object> readResource(String uri) {
    // try {
    // ObjectNode request = objectMapper.createObjectNode();
    // request.put("jsonrpc", "2.0");
    // request.put("id", requestId.getAndIncrement());
    // request.put("method", "resources/read");

    // ObjectNode params = objectMapper.createObjectNode();
    // params.put("uri", uri);

    // request.set("params", params);

    // JsonNode response = transport.sendRequest(request);

    // Map<String, Object> result = new HashMap<>();
    // result.put("uri", uri);
    // result.put("response", response);
    // result.put("status", "success");

    // return result;
    // } catch (Exception e) {
    // System.err.println("Failed to read resource: " + e.getMessage());
    // Map<String, Object> errorResult = new HashMap<>();
    // errorResult.put("uri", uri);
    // errorResult.put("error", e.getMessage());
    // errorResult.put("status", "error");
    // return errorResult;
    // }
    // }

    /**
     * Test MCP server connection
     * 
     * @throws Exception Connection test failed with detailed error information
     */
    public String testConnection() throws Exception {
        // 首先测试传输层连接
        transport.testConnection();

        // 然后测试MCP协议初始化
        String result = initialize();
        // initialize方法成功返回即表示初始化成功
        // System.out.println("MCP协议初始化成功: " + result);
        return result;
    }

    /**
     * Close connection
     */
    @Override
    public void close() throws IOException {
        transport.close();
    }
}
