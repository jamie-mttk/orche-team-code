package com.mttk.orche.agent.mcp.util.stdio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mttk.orche.agent.mcp.util.common.MCPServerBase;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * STDIO传输的MCP服务器
 */
public class STDIOMCPServer extends MCPServerBase {

    private final BufferedReader reader;
    private final PrintWriter writer;
    private boolean running = false;

    public STDIOMCPServer() {
        this.reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
    }

    public STDIOMCPServer(InputStream inputStream, OutputStream outputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
    }

    /**
     * Start STDIO MCP server
     */
    public void start() throws IOException {
        running = true;

        // Don't output to stderr as it gets redirected to stdout
        // System.err.println("STDIO MCP server started");

        String line;
        while (running && (line = reader.readLine()) != null) {
            try {
                String response = processRequest(line);
                writer.println(response);
                writer.flush();
            } catch (Exception e) {
                // Don't output to stderr as it gets redirected to stdout
                // System.err.println("Error processing request: " + e.getMessage());

                // 发送错误响应
                ObjectNode errorResponse = objectMapper.createObjectNode();
                errorResponse.put("jsonrpc", "2.0");
                errorResponse.put("id", 1);

                ObjectNode error = objectMapper.createObjectNode();
                error.put("code", -32603);
                error.put("message", "Internal error");
                errorResponse.set("error", error);

                writer.println(objectMapper.writeValueAsString(errorResponse));
                writer.flush();
            }
        }
    }

    /**
     * 停止服务器
     */
    public void stop() {
        running = false;
        try {
            reader.close();
        } catch (IOException e) {
            // 忽略关闭错误
        }
        writer.close();
    }

    @Override
    protected ObjectNode createToolsListResponse() {
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode tools = objectMapper.createArrayNode();

        // 计算器工具
        ObjectNode calcTool = objectMapper.createObjectNode();
        calcTool.put("name", "calculate");
        calcTool.put("description", "执行简单的数学计算");

        ObjectNode calcSchema = objectMapper.createObjectNode();
        calcSchema.put("type", "object");
        ObjectNode calcProperties = objectMapper.createObjectNode();
        ObjectNode expressionProperty = objectMapper.createObjectNode();
        expressionProperty.put("type", "string");
        expressionProperty.put("description", "要计算的数学表达式");
        calcProperties.set("expression", expressionProperty);
        calcSchema.set("properties", calcProperties);
        calcSchema.set("required", objectMapper.createArrayNode().add("expression"));
        calcTool.set("inputSchema", calcSchema);

        tools.add(calcTool);

        // 时间工具
        ObjectNode timeTool = objectMapper.createObjectNode();
        timeTool.put("name", "get_time");
        timeTool.put("description", "获取当前时间");

        ObjectNode timeSchema = objectMapper.createObjectNode();
        timeSchema.put("type", "object");
        timeSchema.set("properties", objectMapper.createObjectNode());
        timeSchema.set("required", objectMapper.createArrayNode());
        timeTool.set("inputSchema", timeSchema);

        tools.add(timeTool);

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
        resource1.put("uri", "stdio://system/info");
        resource1.put("name", "系统信息");
        resource1.put("description", "当前系统的基本信息");
        resource1.put("mimeType", "application/json");
        resources.add(resource1);

        ObjectNode resource2 = objectMapper.createObjectNode();
        resource2.put("uri", "stdio://environment/vars");
        resource2.put("name", "环境变量");
        resource2.put("description", "当前环境变量列表");
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
            case "calculate":
                String expression = arguments.get("expression").asText();
                try {
                    // 简单的表达式计算（仅支持基本运算）
                    double value = evaluateExpression(expression);
                    result.put("content", "计算结果: " + expression + " = " + value);
                    result.put("isError", false);
                } catch (Exception e) {
                    result.put("content", "计算错误: " + e.getMessage());
                    result.put("isError", true);
                }
                break;

            case "get_time":
                long currentTime = System.currentTimeMillis();
                result.put("content", "当前时间: " + new java.util.Date(currentTime));
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

        if (uri.equals("stdio://system/info")) {
            ObjectNode systemInfo = objectMapper.createObjectNode();
            systemInfo.put("os", System.getProperty("os.name"));
            systemInfo.put("version", System.getProperty("os.version"));
            systemInfo.put("arch", System.getProperty("os.arch"));
            systemInfo.put("javaVersion", System.getProperty("java.version"));

            result.put("contents", "[{\"type\":\"text\",\"text\":\"" + systemInfo.toString() + "\"}]");
        } else if (uri.equals("stdio://environment/vars")) {
            ObjectNode envVars = objectMapper.createObjectNode();
            System.getenv().forEach(envVars::put);

            result.put("contents", "[{\"type\":\"text\",\"text\":\"" + envVars.toString() + "\"}]");
        } else {
            result.put("contents", "[{\"type\":\"text\",\"text\":\"资源不存在: " + uri + "\"}]");
        }

        return result;
    }

    /**
     * 简单的表达式计算
     */
    private double evaluateExpression(String expression) {
        // 移除空格
        expression = expression.replaceAll("\\s+", "");

        // 简单的四则运算计算
        if (expression.matches("\\d+([+\\-*/]\\d+)*")) {
            String[] parts = expression.split("([+\\-*/])");
            String[] operators = expression.split("\\d+");

            if (parts.length == 0) {
                throw new IllegalArgumentException("无效的表达式");
            }

            double result = Double.parseDouble(parts[0]);

            for (int i = 1; i < parts.length; i++) {
                if (i - 1 < operators.length) {
                    String op = operators[i];
                    double value = Double.parseDouble(parts[i]);

                    switch (op) {
                        case "+":
                            result += value;
                            break;
                        case "-":
                            result -= value;
                            break;
                        case "*":
                            result *= value;
                            break;
                        case "/":
                            if (value == 0) {
                                throw new ArithmeticException("除零错误");
                            }
                            result /= value;
                            break;
                        default:
                            throw new IllegalArgumentException("不支持的运算符: " + op);
                    }
                }
            }

            return result;
        } else {
            throw new IllegalArgumentException("不支持的表达式格式");
        }
    }

    /**
     * 主方法 - 启动服务器
     */
    public static void main(String[] args) {
        STDIOMCPServer server = new STDIOMCPServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("\n正在关闭STDIO MCP服务器...");
            server.stop();
        }));

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("启动服务器失败: " + e.getMessage());
        }
    }
}
