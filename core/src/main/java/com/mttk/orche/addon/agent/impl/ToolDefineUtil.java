package com.mttk.orche.addon.agent.impl;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.mttk.orche.addon.AdapterConfig;

import com.mttk.orche.service.AgentTemplateService;

import com.mttk.orche.support.ServerUtil;

public class ToolDefineUtil {
    // 从mongodb获取到agent的tool-call部分
    public static String getToolDefine(AdapterConfig agentConfig) throws Exception {
        AgentTemplateService s = ServerUtil.getService(AgentTemplateService.class);
        List<Document> list = s.find(Filters.eq("key", agentConfig.getStringMandatory("agentTemplate")));
        if (list.isEmpty()) {
            return null;
        }
        //
        Document result = new Document();

        result.append("name", getToolDefineName(agentConfig));
        result.append("description", agentConfig.getString("description"));
        // parameter
        result.append("parameters", list.getFirst().get("tool-call"));
        //
        return result.toJson();
    }

    public static String getToolDefineName(AdapterConfig agentConfig) {
        String name = agentConfig.getString("name");
        String id = agentConfig.getId();
        return buildToolName(name, id);
    }

    /**
     * 构建工具名称，确保总长度不超过64字符
     * 
     * @param name 工具名称
     * @param id   工具ID
     * @return 构建后的工具名称
     */
    public static String buildToolName(String name, String id) {
        // name = removeSpecialChars(name);
        // System.out.println("~~~~~~~~~~~~~~~~~1:" + name + "@@" + id);
        String suffix = id + "__";

        // 如果suffix本身就超过64字符，直接返回suffix
        if (suffix.length() > 64) {
            return suffix;
        }

        // 计算name可以使用的最大长度
        int maxNameLength = 64 - suffix.length();

        // 如果name长度超过限制，截断name
        if (name.length() > maxNameLength) {
            name = name.substring(0, maxNameLength);
        }

        // System.out.println("~~~~~~~~~~~~~~~~~12:" + name + "@@" + suffix);

        return suffix + name;
    }

    // public static String removeSpecialChars(String input) {
    // if (input == null) {
    // return null;
    // }
    // // 保留字母、数字、下划线和中文字符
    // return input.replaceAll("[^a-zA-Z0-9_\\u4e00-\\u9fff]", "");
    // }

    public static String[] partToolName(String toolName) {
        if (toolName == null) {
            throw new IllegalArgumentException("输入字符串不能为null，原始字符串: " + toolName);
        }

        // 以 __ (两个下划线) 分割输入
        String[] parts = toolName.split("__", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("输入字符串必须包含且仅包含一个__分割符，原始字符串: " + toolName);
        }

        // String part1 = parts[0];
        String realPart = parts[0];

        // 直接使用split("_")来判断下划线情况
        String[] part2Split = realPart.split("_", -1);

        if (part2Split.length == 1) {
            // 不包含下划线，返回 [part2, null]
            return new String[] { realPart, null };
        } else if (part2Split.length == 2) {
            // 包含一个下划线，返回 [xxx, yyy]
            return new String[] { part2Split[0], part2Split[1] };
        } else {
            // 包含多个下划线，报错
            throw new IllegalArgumentException("part2包含多个下划线，原始字符串: " + toolName);
        }
    }
}
