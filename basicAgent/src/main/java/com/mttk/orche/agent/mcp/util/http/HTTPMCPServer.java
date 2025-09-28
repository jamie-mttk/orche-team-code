package com.mttk.orche.agent.mcp.util.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mttk.orche.agent.mcp.util.common.MCPServerBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * HTTP传输的MCP服务器
 */
public class HTTPMCPServer extends MCPServerBase {

    private final int port;
    private ServerSocket serverSocket;
    private boolean running = false;

    public HTTPMCPServer(int port) {
        this.port = port;
    }

    /**
     * 启动HTTP MCP服务器
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;

        System.out.println("HTTP MCP服务器启动在端口: " + port);

        while (running) {
            try (Socket clientSocket = serverSocket.accept()) {
                handleClient(clientSocket);
            } catch (IOException e) {
                if (running) {
                    System.err.println("处理客户端连接时出错: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 停止服务器
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("关闭服务器时出错: " + e.getMessage());
        }
    }

    /**
     * 处理客户端请求
     */
    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        OutputStream writer = clientSocket.getOutputStream();

        StringBuilder request = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            request.append(line).append("\n");
        }

        // 读取请求体
        StringBuilder body = new StringBuilder();
        while (reader.ready()) {
            body.append((char) reader.read());
        }

        String response = processRequest(body.toString());

        // 发送HTTP响应
        writer.write("HTTP/1.1 200 OK\r\n".getBytes());
        writer.write("Content-Type: application/json\r\n".getBytes());
        writer.write("Access-Control-Allow-Origin: *\r\n".getBytes());
        writer.write(("Content-Length: " + response.getBytes(StandardCharsets.UTF_8).length + "\r\n").getBytes());
        writer.write("\r\n".getBytes());
        writer.write(response.getBytes(StandardCharsets.UTF_8));
        writer.flush();
    }

    @Override
    protected ObjectNode createToolsListResponse() {
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode tools = objectMapper.createArrayNode();

        // 文件系统工具
        ObjectNode listTool = objectMapper.createObjectNode();
        listTool.put("name", "list_directory");
        listTool.put("description", "列出指定目录的内容");

        ObjectNode listSchema = objectMapper.createObjectNode();
        listSchema.put("type", "object");
        ObjectNode listProperties = objectMapper.createObjectNode();
        ObjectNode pathProperty = objectMapper.createObjectNode();
        pathProperty.put("type", "string");
        pathProperty.put("description", "要列出的目录路径");
        listProperties.set("path", pathProperty);
        listSchema.set("properties", listProperties);
        listSchema.set("required", objectMapper.createArrayNode().add("path"));
        listTool.set("inputSchema", listSchema);

        tools.add(listTool);

        // 读取文件工具
        ObjectNode readTool = objectMapper.createObjectNode();
        readTool.put("name", "read_file");
        readTool.put("description", "读取指定文件的内容");

        ObjectNode readSchema = objectMapper.createObjectNode();
        readSchema.put("type", "object");
        ObjectNode readProperties = objectMapper.createObjectNode();
        ObjectNode filePathProperty = objectMapper.createObjectNode();
        filePathProperty.put("type", "string");
        filePathProperty.put("description", "要读取的文件路径");
        readProperties.set("path", filePathProperty);
        readSchema.set("properties", readProperties);
        readSchema.set("required", objectMapper.createArrayNode().add("path"));
        readTool.set("inputSchema", readSchema);

        tools.add(readTool);

        result.set("tools", tools);
        return result;
    }

    @Override
    protected ObjectNode createToolCallResponse(JsonNode request) {
        String toolName = request.get("params").get("name").asText();
        JsonNode arguments = request.get("params").get("arguments");

        return handleToolCall(toolName, arguments);
    }

    @Override
    protected ObjectNode createResourcesListResponse() {
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode resources = objectMapper.createArrayNode();

        ObjectNode resource1 = objectMapper.createObjectNode();
        resource1.put("uri", "file:///README.md");
        resource1.put("name", "README文件");
        resource1.put("description", "项目说明文档");
        resource1.put("mimeType", "text/markdown");
        resources.add(resource1);

        ObjectNode resource2 = objectMapper.createObjectNode();
        resource2.put("uri", "file:///config.json");
        resource2.put("name", "配置文件");
        resource2.put("description", "应用程序配置文件");
        resource2.put("mimeType", "application/json");
        resources.add(resource2);

        result.set("resources", resources);
        return result;
    }

    @Override
    protected ObjectNode createResourceReadResponse(JsonNode request) {
        String uri = request.get("params").get("uri").asText();
        return handleResourceRead(uri);
    }

    @Override
    protected ObjectNode handleToolCall(String toolName, JsonNode arguments) {
        ObjectNode result = objectMapper.createObjectNode();

        switch (toolName) {
            case "list_directory":
                result.put("content",
                        "[{\"name\":\"file1.txt\",\"type\":\"file\"},{\"name\":\"folder1\",\"type\":\"directory\"}]");
                result.put("isError", false);
                break;

            case "read_file":
                String path = arguments.get("path").asText();
                result.put("content", "这是文件 " + path + " 的内容\n包含一些示例文本。");
                result.put("isError", false);
                break;

            default:
                result.put("content", "未知工具: " + toolName);
                result.put("isError", true);
                break;
        }

        return result;
    }

    @Override
    protected ObjectNode handleResourceRead(String uri) {
        ObjectNode result = objectMapper.createObjectNode();

        if (uri.equals("file:///README.md")) {
            result.put("contents", "[{\"type\":\"text\",\"text\":\"# 项目说明\\n\\n这是一个示例项目的README文件。\"}]");
        } else if (uri.equals("file:///config.json")) {
            result.put("contents",
                    "[{\"type\":\"text\",\"text\":\"{\\\"name\\\":\\\"example\\\",\\\"version\\\":\\\"1.0.0\\\"}\"}]");
        } else {
            result.put("contents", "[{\"type\":\"text\",\"text\":\"资源不存在: " + uri + "\"}]");
        }

        return result;
    }

    /**
     * 主方法 - 启动服务器
     */
    public static void main(String[] args) {
        HTTPMCPServer server = new HTTPMCPServer(3000);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n正在关闭HTTP MCP服务器...");
            server.stop();
        }));

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("启动服务器失败: " + e.getMessage());
        }
    }
}
