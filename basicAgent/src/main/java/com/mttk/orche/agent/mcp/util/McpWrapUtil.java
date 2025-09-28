package com.mttk.orche.agent.mcp.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.impl.ToolDefineUtil;
import com.mttk.orche.agent.mcp.util.common.MCPClientBase;

public class McpWrapUtil {
    public static List<String> getToolDefineInternal(MCPClientBase client, AdapterConfig agentConfig,
            boolean resultNameDirect) throws Exception {
        AdapterConfig config = agentConfig.getBean("config");
        List<AdapterConfig> tools = config.getBeanList("tools");
        List<String> toolDefines = new ArrayList<>();
        if (tools == null || tools.isEmpty()) {
            return toolDefines;
        }
        //
        String agentId = null;
        if (!resultNameDirect) {
            agentId = agentConfig.get("_id").toString();
        }
        //
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < tools.size(); i++) {
            AdapterConfig tool = tools.get(i);
            // 创建一个JSON Node jsonNode
            ObjectNode jsonNode = mapper.createObjectNode();

            // 从tool获取name(String)加入jsonNode
            String name = tool.getString("name");

            if (resultNameDirect) {
                // 直接返回名字
                toolDefines.add(name);
                continue;
            }
            // 处理名字
            name = ToolDefineUtil.buildToolName(name, agentId + "_" + i);
            jsonNode.put("name", name);
            // 从tool获取description(String)加入jsonNode
            String description = tool.getString("desciption");
            if (description != null) {
                jsonNode.put("description", description);
            }

            // 从tool获取description(inputSchema),解析成JSON加入jsonNode
            String inputSchema = tool.getString("inputSchema");
            if (inputSchema != null) {
                try {
                    JsonNode inputSchemaNode = mapper.readTree(inputSchema);
                    jsonNode.set("inputSchema", inputSchemaNode);
                } catch (Exception e) {
                    // 如果解析失败，作为字符串存储
                    jsonNode.put("inputSchema", inputSchema);
                }
            }

            // 转换jsonNode到String,加入toolDefines
            String toolDefine = jsonNode.toString();
            toolDefines.add(toolDefine);
        }
        return toolDefines;
    }
    // resultNameDirect true: 直接返回原始的名称 false:返回完整的toolDefine
    // public static List<String> getToolDefineInternal(MCPClientBase client,
    // AdapterConfig agentConfig,
    // boolean resultNameDirect) throws Exception {
    // //
    // // AdapterConfig config = agentConfig.getBean("config");
    // // MCPClientBase client = buildMcpClient(config);
    // //
    // List<JsonNode> tools = client.listTools();

    // ObjectMapper objectMapper = new ObjectMapper();
    // // String agentName = agentConfig.getString("name");
    // String agentId = null;
    // if (!resultNameDirect) {
    // agentId = agentConfig.get("_id").toString();
    // }

    // // 创建List来保存修改后的工具信息
    // List<String> modifiedToolsList = new ArrayList<>();

    // for (int i = 0; i < tools.size(); i++) {
    // JsonNode tool = tools.get(i);
    // String originalName = tool.get("name").asText();
    // if (resultNameDirect) {
    // modifiedToolsList.add(originalName);
    // continue;
    // }

    // // String formattedName = String.format("[%s-%s](%s-%s)", agentName,
    // // originalName, agentId, originalName);
    // // ()里使用originalName会导致太长...
    // // String formattedName = originalName + "~" + agentId + "-" + i;
    // String formattedName = ToolDefineUtil.buildToolName(originalName, agentId +
    // "_" + i);
    // // System.out.println(formattedName + "~~~" + formattedName.length());

    // // 创建新的ObjectNode，复制原tool的所有字段，但修改name字段
    // ObjectNode modifiedTool = objectMapper.createObjectNode();
    // tool.fieldNames().forEachRemaining(fieldName -> {
    // if ("name".equals(fieldName)) {
    // modifiedTool.put(fieldName, formattedName);
    // } else if ("description".equals(fieldName)) {
    // // 如果是description字段，在原有描述后添加额外信息
    // String originalDescription = tool.get(fieldName).asText();
    // // String enhancedDescription = originalDescription +
    // // "\r\n除了原有的参数外,请生成结果保存的文件名(参数OrcheFileName)和描述(参数OrcheFileDescription)";
    // String enhancedDescription = originalDescription;
    // modifiedTool.put(fieldName, enhancedDescription);
    // } else {
    // modifiedTool.set(fieldName, tool.get(fieldName));
    // }
    // });

    // // // 在MCP参数里增加两个参数
    // // if (modifiedTool.has("inputSchema") &&
    // // modifiedTool.get("inputSchema").has("properties")) {
    // // ObjectNode inputSchema = (ObjectNode) modifiedTool.get("inputSchema");
    // // ObjectNode properties = (ObjectNode) inputSchema.get("properties");

    // // // 添加__fileName参数
    // // ObjectNode fileNameParam = objectMapper.createObjectNode();
    // // fileNameParam.put("type", "string");
    // // fileNameParam.put("description", "结果保存文件名");
    // // properties.set("OrcheFileName", fileNameParam);

    // // // 添加__fileDescription参数
    // // ObjectNode fileDescriptionParam = objectMapper.createObjectNode();
    // // fileDescriptionParam.put("type", "string");
    // // fileDescriptionParam.put("description", "对于结果的描述,不超过100字");
    // // fileDescriptionParam.put("maxLength", 100);
    // // properties.set("OrcheFileDescription", fileDescriptionParam);

    // // // 将__fileName和__fileDescription添加到required数组中
    // // if (inputSchema.has("required") && inputSchema.get("required").isArray())
    // {
    // // // 如果已有required数组，添加新参数
    // // com.fasterxml.jackson.databind.node.ArrayNode requiredArray =
    // // (com.fasterxml.jackson.databind.node.ArrayNode) inputSchema
    // // .get("required");
    // // requiredArray.add("OrcheFileName");
    // // requiredArray.add("OrcheFileDescription");
    // // } else {
    // // // 如果没有required数组，创建新的
    // // com.fasterxml.jackson.databind.node.ArrayNode requiredArray =
    // // objectMapper.createArrayNode();
    // // requiredArray.add("OrcheFileName");
    // // requiredArray.add("OrcheFileDescription");
    // // inputSchema.set("required", requiredArray);
    // // }
    // // }

    // // 将修改后的工具信息保存到List中
    // modifiedToolsList.add(modifiedTool.toPrettyString());

    // }

    // // 输出List的大小和内容
    // // System.out.println("\n总共修改了 " + modifiedToolsList.size() + " 个工具");
    // // System.out.println("List中保存的修改后工具信息:" + McpWrapUtil.class.hashCode());
    // // for (int i = 0; i < modifiedToolsList.size(); i++) {
    // // System.out.println("工具 " + (i + 1) + ":");
    // // System.out.println(modifiedToolsList.get(i));
    // // System.out.println("---");
    // // }
    // //
    // return modifiedToolsList;
    // }

}
