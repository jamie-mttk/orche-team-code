package com.mttk.orche.agent.webSearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.agent.webSearch.SearchItem;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class SerperApiClient implements WebQuery {
    // private static final Logger logger =
    // LoggerFactory.getLogger(SerperApiClient.class);

    @Override
    public List<SearchItem> query(String keyWord, AdapterConfig config) throws Exception {

        //
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .build();
        // 显式排除PDF
        keyWord = keyWord + " -filetype:pdf";
        // 构建 JSON 请求体
        String requestBody = String.format(
                "{\"q\": \"%s\", \"num\": %d}",
                keyWord.replace("\"", "\\\""),
                config.getInteger("config", 10));
        // logger.info("Serper request {}", requestBody);
        // 创建 HTTP 请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://google.serper.dev/search"))
                .header("Accept", "application/json")
                .header("X-API-KEY", config.getStringMandatory("apiKeySerper"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // 发送请求并获取响应
        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        // logger.info("Serper response {}", responseBody);

        // 检查HTTP状态码
        if (response.statusCode() != 200) {
            throw new RuntimeException("Serper API HTTP错误 - 状态码: " + response.statusCode() + ", 响应: " + responseBody);
        }

        // 检查响应是否为空
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new RuntimeException("Serper API返回空响应");
        }

        // 检查响应是否包含非JSON内容
        if (!responseBody.trim().startsWith("{") && !responseBody.trim().startsWith("[")) {
            throw new RuntimeException(
                    "Serper API返回非JSON响应: " + responseBody.substring(0, Math.min(200, responseBody.length())));
        }

        // 使用 Jackson 解析 JSON 到 Map
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> tempMap = mapper.readValue(responseBody, Map.class);
            responseMap = tempMap;
        } catch (Exception e) {
            throw new RuntimeException("Serper API JSON解析失败: " + e.getMessage() + ", 响应内容: "
                    + responseBody.substring(0, Math.min(500, responseBody.length())), e);
        }

        // 检查是否有错误状态码
        if (responseMap.containsKey("statusCode")) {
            throw new RuntimeException("Serper API错误 - statusCode: " + responseMap.get("statusCode") + ", message: "
                    + responseMap.get("message"));
        }

        // 从 Map 中提取 organic 列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> organicList = (List<Map<String, Object>>) responseMap.get("organic");
        List<SearchItem> results = new ArrayList<>();
        if (organicList == null) {
            throw new RuntimeException("Serper API错误 - 返回结果中没有organic");
        }
        // 手动转换每个 organic 项
        for (Map<String, Object> itemMap : organicList) {
            SearchItem item = new SearchItem();
            item.setKeyword(keyWord);
            // 设置标题（处理可能缺失的情况）
            if (itemMap.containsKey("title")) {
                item.setTitle(itemMap.get("title").toString());
            }

            // 设置链接（必需字段）
            item.setUrl(itemMap.get("link").toString());

            // 设置摘要（处理可能缺失的情况）
            if (itemMap.containsKey("snippet")) {
                item.setSnippet(itemMap.get("snippet").toString());
            }

            // 设置日期（处理可能缺失的情况）
            if (itemMap.containsKey("date")) {
                item.setDate(itemMap.get("date").toString());
            }

            // 设置位置（处理可能缺失的情况）
            if (itemMap.containsKey("position")) {
                // 位置可能是整数或字符串，统一转为整数
                Object positionObj = itemMap.get("position");
                if (positionObj instanceof Integer) {
                    item.setPosition((Integer) positionObj);
                } else if (positionObj instanceof String) {
                    try {
                        item.setPosition(Integer.parseInt((String) positionObj));
                    } catch (NumberFormatException e) {
                        // 解析失败时使用默认值
                        item.setPosition(0);
                    }
                }
            }

            results.add(item);
        }

        return results;
    }

}
