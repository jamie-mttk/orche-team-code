package com.mttk.orche.addon.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

//记录调用LLM返回的结果中ToolCall部分
public class ToolCall {
    private Integer index;
    private String id;
    private String type;
    private String functionName;
    private String arguments;

    /**
     * 构造函数
     *
     * @param id           调用ID
     * @param functionName 函数名称
     * @param arguments    参数JSON字符串
     */
    public ToolCall(Integer index, String id, String type, String functionName, String arguments) {
        this.index = index;
        this.id = id;
        this.type = type;
        this.functionName = functionName;
        this.arguments = arguments;

    }

    // /**
    // * 解析arguments JSON字符串为Map
    // *
    // * @param argumentsJson arguments的JSON字符串
    // * @return 解析后的参数Map
    // */
    // private Map<String, Object> parseArguments(String argumentsJson) {
    // Map<String, Object> result = new HashMap<>();
    // if (argumentsJson == null || argumentsJson.trim().isEmpty()) {
    // return result;
    // }

    // try {
    // // 首先，反转义字符串以处理 \\" 变成 "
    // String unescapedArguments = unescapeJson(argumentsJson);

    // // 然后，移除外层引号（如果存在）
    // // 例如: "{\"query\":\"value\"}" -> {"query":"value"}
    // if (unescapedArguments.startsWith("\"") && unescapedArguments.endsWith("\""))
    // {
    // unescapedArguments = unescapedArguments.substring(1,
    // unescapedArguments.length() - 1);
    // }

    // // 现在使用Jackson解析清理后的JSON字符串
    // ObjectMapper mapper = new ObjectMapper();
    // JsonNode argumentsNode = mapper.readTree(unescapedArguments);

    // if (argumentsNode.isObject()) {
    // // 遍历所有字段
    // argumentsNode.fieldNames().forEachRemaining(fieldName -> {
    // JsonNode fieldValue = argumentsNode.get(fieldName);
    // if (fieldValue != null && !fieldValue.isNull()) {
    // if (fieldValue.isTextual()) {
    // result.put(fieldName, fieldValue.asText());
    // } else if (fieldValue.isNumber()) {
    // if (fieldValue.isInt()) {
    // result.put(fieldName, fieldValue.asInt());
    // } else if (fieldValue.isLong()) {
    // result.put(fieldName, fieldValue.asLong());
    // } else if (fieldValue.isDouble()) {
    // result.put(fieldName, fieldValue.asDouble());
    // } else {
    // result.put(fieldName, fieldValue.numberValue());
    // }
    // } else if (fieldValue.isBoolean()) {
    // result.put(fieldName, fieldValue.asBoolean());
    // } else {
    // // 对于复杂对象，转换为字符串
    // result.put(fieldName, fieldValue.toString());
    // }
    // }
    // });
    // }

    // } catch (Exception e) {
    // // 如果Jackson解析失败，回退到简单解析
    // try {
    // return parseArgumentsSimple(argumentsJson);
    // } catch (Exception e2) {
    // System.err.println("解析arguments失败: " + e2.getMessage());
    // }
    // }

    // return result;
    // }

    // /**
    // * 简单的JSON解析方法（作为回退方案）
    // *
    // * @param argumentsJson arguments的JSON字符串
    // * @return 解析后的参数Map
    // */
    // private Map<String, Object> parseArgumentsSimple(String argumentsJson) {
    // Map<String, Object> result = new HashMap<>();
    // if (argumentsJson == null || argumentsJson.trim().isEmpty()) {
    // return result;
    // }

    // try {
    // // 简单的JSON解析，处理基本的键值对
    // argumentsJson = argumentsJson.trim();
    // if (!argumentsJson.startsWith("{") || !argumentsJson.endsWith("}")) {
    // return result;
    // }

    // // 移除外层的大括号
    // String content = argumentsJson.substring(1, argumentsJson.length() - 1);

    // // 分割键值对
    // String[] pairs = content.split(",");
    // for (String pair : pairs) {
    // if (pair.trim().isEmpty()) {
    // continue;
    // }

    // // 查找第一个冒号
    // int colonIndex = pair.indexOf(':');
    // if (colonIndex == -1) {
    // continue;
    // }

    // String key = pair.substring(0, colonIndex).trim();
    // String value = pair.substring(colonIndex + 1).trim();

    // // 移除引号
    // if (key.startsWith("\"") && key.endsWith("\"")) {
    // key = key.substring(1, key.length() - 1);
    // }
    // if (value.startsWith("\"") && value.endsWith("\"")) {
    // value = value.substring(1, value.length() - 1);
    // }

    // // 反转义特殊字符
    // key = unescapeJson(key);
    // value = unescapeJson(value);

    // result.put(key, value);
    // }

    // } catch (Exception e) {
    // // 解析失败时返回空Map
    // System.err.println("简单解析arguments失败: " + e.getMessage());
    // }

    // return result;
    // }

    // /**
    // * 反转义JSON字符串中的特殊字符
    // */
    // private String unescapeJson(String text) {
    // if (text == null) {
    // return "";
    // }
    // return text.replace("\\n", "\n")
    // .replace("\\r", "\r")
    // .replace("\\t", "\t")
    // .replace("\\\"", "\"")
    // .replace("\\\\", "\\");
    // }

    // /**
    // * 获取解析后的参数Map
    // *
    // * @return 参数Map
    // */
    // public Map<String, Object> getParsedArguments() {
    // return parsedArguments;
    // }

    // /**
    // * 获取指定参数的值
    // *
    // * @param key 参数名
    // * @return 参数值，如果不存在返回null
    // */
    // public Object getArgument(String key) {
    // return parsedArguments.get(key);
    // }

    // /**
    // * 获取指定参数的值（字符串类型）
    // *
    // * @param key 参数名
    // * @return 参数值，如果不存在返回null
    // */
    // public String getArgumentString(String key) {
    // Object value = parsedArguments.get(key);
    // return value != null ? value.toString() : null;
    // }

    // /**
    // * 检查是否包含指定参数
    // *
    // * @param key 参数名
    // * @return 是否包含该参数
    // */
    // public boolean hasArgument(String key) {
    // return parsedArguments.containsKey(key);
    // }

    // Getter和Setter方法

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
        // this.parsedArguments = parseArguments(arguments);
    }

    @Override
    public String toString() {
        return "ToolCall{" +
                "id='" + id + '\'' +
                ", functionName='" + functionName + '\'' +
                ", arguments='" + arguments + '\'' +
                '}';
    }
}