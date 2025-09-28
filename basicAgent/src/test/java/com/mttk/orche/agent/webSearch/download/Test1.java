package com.mttk.orche.agent.webSearch.download;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mttk.orche.agent.mcp.util.http.HTTPMCPClient;

public class Test1 {
    public static void main1(String[] args) throws Exception {
        String url = "https://mcp.amap.com/mcp?key=ac8d3aedd8c208a37214488a2a707d9d";
        HTTPMCPClient client = new HTTPMCPClient(url);
        //
        String map = client.initialize();
        System.out.println("init:\n" + map);

        //
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("city", "北京");
        String str = client.callTool("maps_weather", arguments);
        System.out.println("call:\n" + str);
    }

    public static void main(String[] args) throws Exception {
        String url = "https://mcp.amap.com/mcp?key=ac8d3aedd8c208a37214488a2a707d9d";
        HTTPMCPClient client = new HTTPMCPClient(url);
        Collection<JsonNode> tools = client.listTools();

        ObjectMapper objectMapper = new ObjectMapper();
        String yyy = "高德地图";
        String zzz = "123456";

        // 创建List来保存修改后的工具信息
        List<String> modifiedToolsList = new ArrayList<>();

        for (JsonNode tool : tools) {
            String originalName = tool.get("name").asText();
            String formattedName = String.format("[%s-%s](%s-%s)", yyy, originalName, zzz, originalName);

            // 创建新的ObjectNode，复制原tool的所有字段，但修改name字段
            ObjectNode modifiedTool = objectMapper.createObjectNode();
            tool.fieldNames().forEachRemaining(fieldName -> {
                if ("name".equals(fieldName)) {
                    modifiedTool.put(fieldName, formattedName);
                } else {
                    modifiedTool.set(fieldName, tool.get(fieldName));
                }
            });

            // 将修改后的工具信息保存到List中
            modifiedToolsList.add(modifiedTool.toPrettyString());

            System.out.println("修改后的名称: " + modifiedTool.get("name").asText());
            System.out.println(modifiedTool.toPrettyString());
            System.out.println("=============");
        }

        // 输出List的大小和内容
        System.out.println("\n总共修改了 " + modifiedToolsList.size() + " 个工具");
        System.out.println("List中保存的修改后工具信息:");
        for (int i = 0; i < modifiedToolsList.size(); i++) {
            System.out.println("工具 " + (i + 1) + ":");
            System.out.println(modifiedToolsList.get(i));
            System.out.println("---");
        }
    }
}
