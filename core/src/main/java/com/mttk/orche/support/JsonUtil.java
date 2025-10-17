package com.mttk.orche.support;

import com.mttk.orche.util.StringUtil;

public class JsonUtil {

    /**
     * 转义JSON字符串中的特殊字符
     *
     * @param text 原始文本
     * @return 转义后的文本
     */
    public static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 反转义JSON字符串中的特殊字符
     *
     * @param text 转义后的文本
     * @return 原始文本
     */
    public static String unescapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    // 去掉可能的```json和}```
    public static String cleanJsonString(String input) {
        if (StringUtil.isEmpty(input)) {
            return input;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }

        // 处理单行格式 (```json{...}```)
        if (trimmed.startsWith("```json") && trimmed.endsWith("```")) {
            return trimmed.substring(7, trimmed.length() - 3).trim();
        }

        return trimmed; // 返回标准 JSON
    }
}
