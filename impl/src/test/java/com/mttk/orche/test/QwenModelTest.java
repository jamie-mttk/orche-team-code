package com.mttk.orche.test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class QwenModelTest {

    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String API_KEY = "sk-74e36b2510ba4b0ebb622718da81d5c7";
    private static final String MODEL_NAME = "qwen-plus-latest";
    private static final int MAX_TOKENS = 32768;
    private static final int TEMPERATURE = 0;

    public static void main(String[] args) {
        try {
            // 读取error_request.txt文件内容
            String requestContent = readFileContent("d:\\error_request.txt");
            // System.out.println("=== 读取到的请求内容 ===");
            // System.out.println(requestContent);
            System.out.println("\n=== 开始调用大模型 ===");

            // 调用大模型
            String response = callQwenModel(requestContent);

            // 打印大模型反馈
            System.out.println("\n=== 大模型反馈 ===");
            System.out.println(response);

        } catch (Exception e) {
            System.err.println("=== 调用错误 ===");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     */
    private static String readFileContent(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 调用千问大模型API
     */
    private static String callQwenModel(String content) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // 设置请求方法和头部
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 构建请求体
            String requestBody = buildRequestBody(content);
            System.out.println("\n=== 请求体 ===");
            System.out.println(requestBody);

            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.flush();
            }

            // 获取响应码
            int responseCode = connection.getResponseCode();
            System.out.println("\n=== 响应状态码: " + responseCode + " ===");

            // 读取响应
            InputStream inputStream;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP错误码: " + responseCode + ", 响应: " + response.toString());
            }

            return response.toString();

        } finally {
            connection.disconnect();
        }
    }

    /**
     * 构建请求体JSON
     */
    private static String buildRequestBody(String content) {
        // 转义JSON字符串
        String escapedContent = escapeJson(content);

        return "{"
                + "\"model\":\"" + MODEL_NAME + "\","
                + "\"messages\":["
                + "{"
                + "\"role\":\"user\","
                + "\"content\":\"" + escapedContent + "\""
                + "}"
                + "],"
                + "\"max_tokens\":" + MAX_TOKENS + ","
                + "\"temperature\":" + TEMPERATURE
                + "}";
    }

    /**
     * JSON字符串转义
     */
    private static String escapeJson(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (ch < ' ') {
                        String t = "000" + Integer.toHexString(ch);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
}
