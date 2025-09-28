package com.mttk.orche.agent.webSearch.download;

import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.mttk.orche.agent.mcp.util.stdio.STDIOMCPClient;

public class STDIOMCPClientTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== STDIO MCP Client 测试 ===");

        // 启动MCP服务器进程
        ProcessBuilder serverBuilder = new ProcessBuilder(
                "npx.cmd", "-y", "@variflight-ai/tripmatch-mcp");
        serverBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        // 设置环境变量
        serverBuilder.environment().put("VARIFLIGHT_API_KEY", "sk-FJX50KRETCVD03uyouEm73R8TNp9iinAXJd8RDs-6Zc");

        Process serverProcess = serverBuilder.start();
        System.out.println("serverProcess: " + serverProcess.isAlive() + "~~" + serverProcess);

        try {
            // 创建STDIOMCPClient，使用服务器的输入输出流
            STDIOMCPClient client = new STDIOMCPClient(
                    serverProcess.getInputStream(),
                    serverProcess.getOutputStream());

            System.out.println("1. 测试连接...");
            try {
                client.testConnection();
                System.out.println("✓ 连接测试成功");
            } catch (Exception e) {
                System.out.println("✗ 连接测试失败: " + e.getMessage());
            }

            System.out.println("\n2. 获取工具列表...");
            try {
                Collection<JsonNode> tools = client.listTools();
                System.out.println("✓ 成功获取到 " + tools.size() + " 个工具:");

                int index = 0;
                for (JsonNode tool : tools) {
                    String toolName = tool.get("name").asText();
                    String description = tool.has("description") ? tool.get("description").asText() : "无描述";
                    System.out.println("  " + (index++) + ". " + toolName + " - " + description);
                    System.out.println(tool.toString());
                }
            } catch (Exception e) {
                System.out.println("✗ 获取工具列表失败: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\n3. 测试工具调用...");
            try {
                // 尝试调用第一个工具（如果有的话）
                Collection<JsonNode> tools = client.listTools();
                if (!tools.isEmpty()) {
                    JsonNode firstTool = tools.iterator().next();
                    String toolName = firstTool.get("name").asText();
                    System.out.println("尝试调用工具: " + toolName);

                    // 计算后天的日期
                    java.time.LocalDate today = java.time.LocalDate.now();
                    java.time.LocalDate dayAfterTomorrow = today.plusDays(2);
                    String dateStr = dayAfterTomorrow.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);

                    System.out.println("查询日期: " + dateStr + " (后天)");
                    System.out.println("查询路线: 北京(PEK) -> 上海虹桥(SHA)");

                    // 构建查询参数
                    java.util.Map<String, Object> params = new java.util.HashMap<>();
                    params.put("fnum", "CA1234"); // 使用一个示例航班号
                    params.put("date", dateStr);
                    params.put("dep", "PEK"); // 北京首都机场
                    params.put("arr", "SHA"); // 上海虹桥机场

                    System.out.println("调用参数: " + params);
                    String result = client.callTool(toolName, params);
                    System.out.println("✓ 工具调用成功，结果: " + result);
                } else {
                    System.out.println("没有可用的工具进行测试");
                }
            } catch (Exception e) {
                System.out.println("✗ 工具调用失败: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\n4. 关闭客户端...");
            client.close();
            System.out.println("✓ 客户端已关闭");

        } catch (Exception e) {
            System.out.println("✗ 测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 确保服务器进程被终止
            if (serverProcess.isAlive()) {
                System.out.println("\n终止服务器进程...");
                serverProcess.destroy();
                try {
                    serverProcess.waitFor();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        System.out.println("\n=== 测试完成 ===");
    }
}
