package com.mttk.orche.agent.webSearch.download;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class SimpleMCPClient {
    public static void main(String[] args) throws Exception {

        // 启动MCP服务器进程
        ProcessBuilder serverBuilder = new ProcessBuilder(
                "npx.cmd", "-y", "@variflight-ai/tripmatch-mcp");
        serverBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process serverProcess = serverBuilder.start();

        // 获取服务器的输入输出流
        try (
                BufferedReader serverOutput = new BufferedReader(
                        new InputStreamReader(serverProcess.getInputStream()));
                PrintWriter clientOutput = new PrintWriter(
                        serverProcess.getOutputStream(), true);) {
            // 发送工具列表请求
            clientOutput.println("{\"jsonrpc\":\"2.0\",\"method\":\"tools/list\",\"id\":1}");
            clientOutput.flush();

            // 读取并显示服务器响应
            System.out.println("工具列表响应:");
            String line;
            while ((line = serverOutput.readLine()) != null) {
                System.out.println(line);
            }
        }

        // 等待服务器退出
        int exitCode = serverProcess.waitFor();
        System.out.println("服务器退出，状态码: " + exitCode);

    }
}
