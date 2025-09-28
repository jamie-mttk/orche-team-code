package com.mttk.orche.agent.mcp.util.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * MCP Server Base Class
 * Implements core MCP protocol server functionality
 */
public abstract class MCPServerBase {

    protected final ObjectMapper objectMapper;

    public MCPServerBase() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Process MCP request
     */
    public String processRequest(String requestBody) {
        try {
            JsonNode request = objectMapper.readTree(requestBody);
            String method = request.get("method").asText();
            Object id = request.get("id");

            ObjectNode response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            response.set("id", objectMapper.valueToTree(id));

            switch (method) {
                case "initialize":
                    response.set("result", createInitializeResponse());
                    break;

                case "tools/list":
                    response.set("result", createToolsListResponse());
                    break;

                case "tools/call":
                    response.set("result", createToolCallResponse(request));
                    break;

                case "resources/list":
                    response.set("result", createResourcesListResponse());
                    break;

                case "resources/read":
                    response.set("result", createResourceReadResponse(request));
                    break;

                default:
                    ObjectNode error = objectMapper.createObjectNode();
                    error.put("code", -32601);
                    error.put("message", "Method not found");
                    response.set("error", error);
                    break;
            }

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("jsonrpc", "2.0");
            response.put("id", 1);

            ObjectNode error = objectMapper.createObjectNode();
            error.put("code", -32700);
            error.put("message", "Parse error");
            response.set("error", error);

            try {
                return objectMapper.writeValueAsString(response);
            } catch (Exception ex) {
                return "{\"jsonrpc\":\"2.0\",\"id\":1,\"error\":{\"code\":-32700,\"message\":\"Parse error\"}}";
            }
        }
    }

    /**
     * Create initialization response
     */
    protected ObjectNode createInitializeResponse() {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");

        ObjectNode capabilities = objectMapper.createObjectNode();
        ObjectNode tools = objectMapper.createObjectNode();
        tools.put("listChanged", true);
        capabilities.set("tools", tools);

        ObjectNode resources = objectMapper.createObjectNode();
        resources.put("subscribe", true);
        resources.put("listChanged", true);
        capabilities.set("resources", resources);

        result.set("capabilities", capabilities);

        ObjectNode serverInfo = objectMapper.createObjectNode();
        serverInfo.put("name", "Java MCP Server");
        serverInfo.put("version", "1.0.0");
        result.set("serverInfo", serverInfo);

        return result;
    }

    /**
     * Create tools list response - to be implemented by subclasses
     */
    protected abstract ObjectNode createToolsListResponse();

    /**
     * Create tool call response - to be implemented by subclasses
     */
    protected abstract ObjectNode createToolCallResponse(JsonNode request);

    /**
     * Create resources list response - to be implemented by subclasses
     */
    protected abstract ObjectNode createResourcesListResponse();

    /**
     * Create resource read response - to be implemented by subclasses
     */
    protected abstract ObjectNode createResourceReadResponse(JsonNode request);

    /**
     * Handle tool call - to be implemented by subclasses
     */
    protected abstract ObjectNode handleToolCall(String toolName, JsonNode arguments);

    /**
     * Handle resource read - to be implemented by subclasses
     */
    protected abstract ObjectNode handleResourceRead(String uri);
}
