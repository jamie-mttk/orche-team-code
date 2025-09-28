package com.mttk.orche.agent.mcp.util;

import java.io.IOException;
import java.util.List;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.agent.mcp.util.common.MCPClientBase;
import com.mttk.orche.agent.mcp.util.http.HTTPMCPClient;
import com.mttk.orche.agent.mcp.util.stdio.STDIOMCPClient;

public class McpClientUtil {

    public static MCPClientBase buildMcpClient(AdapterConfig config) throws Exception {
        String mode = config.getString("mode", "http");

        switch (mode) {
            case "http":
                return buildHttpMcpClient(config);
            case "stdio":
                return buildStdioMcpClient(config);
            default:
                throw new IllegalArgumentException("不支持的传输模式: " + mode + "，支持的模式: http, stdio");
        }
    }

    /**
     * 构建HTTP模式的MCP客户端
     */
    public static MCPClientBase buildHttpMcpClient(AdapterConfig config) {
        String url = config.getString("url");
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP模式需要提供url参数");
        }
        //
        url = buildUrlWithParams(url, config);

        // System.out.println("HTTPMCPClient: " + url);
        // System.out.println(config);
        return new HTTPMCPClient(url);
    }

    /**
     * 构建STDIO模式的MCP客户端
     */
    public static MCPClientBase buildStdioMcpClient(AdapterConfig config) throws Exception {

        //
        Process serverProcess = buildServerProcess(config);

        // 创建STDIOMCPClient的匿名子类，重写close方法以销毁serverProcess
        return new STDIOMCPClient(serverProcess.getInputStream(), serverProcess.getOutputStream()) {
            @Override
            public void close() throws IOException {
                try {
                    super.close();
                } finally {
                    // 销毁serverProcess
                    if (serverProcess != null && serverProcess.isAlive()) {
                        serverProcess.destroy();
                    }
                }
            }
        };

    }

    private static Process buildServerProcess(AdapterConfig config) throws Exception {
        // 获取命令行
        String command = config.getStringMandatory("command");

        // 构建ProcessBuilder
        ProcessBuilder serverBuilder = new ProcessBuilder(command);

        // 添加命令行参数
        List<AdapterConfig> args = config.getBeanList("args");
        if (args != null && !args.isEmpty()) {
            for (AdapterConfig arg : args) {
                serverBuilder.command().add(arg.getStringMandatory("arg"));
            }
        }

        // 设置环境变量
        List<AdapterConfig> envs = config.getBeanList("envs");
        if (envs != null && !envs.isEmpty()) {
            for (AdapterConfig env : envs) {
                String envKey = env.getStringMandatory("envKey");
                String envValue = env.getStringMandatory("envValue");
                serverBuilder.environment().put(envKey, envValue);
            }
        }

        // 设置错误输出重定向
        serverBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        // 启动进程
        Process serverProcess = serverBuilder.start();

        //
        return serverProcess;
    }

    /**
     * 构建带参数的URL
     * 
     * @param url    原始URL
     * @param config 配置对象
     * @return 带参数的URL
     */
    private static String buildUrlWithParams(String url, AdapterConfig config) {
        // 获取http参数列表
        List<AdapterConfig> paras = config.getBeanList("paras");

        // 如果paras为null或长度为0，直接返回url
        if (paras == null || paras.isEmpty()) {
            return url;
        }

        // 如果paras不为空，作为http参数添加到url后
        StringBuilder urlBuilder = new StringBuilder(url);

        // 判断url是否已经有参数
        boolean hasParams = url.contains("?");

        for (int i = 0; i < paras.size(); i++) {
            AdapterConfig para = paras.get(i);
            String key = para.getStringMandatory("paraKey");
            String value = para.getStringMandatory("paraValue");

            if (i == 0 && !hasParams) {
                // 第一个参数且url没有参数，添加?
                urlBuilder.append("?");
            } else {
                // 后续参数或url已有参数，添加&
                urlBuilder.append("&");
            }

            // 添加参数
            urlBuilder.append(key).append("=").append(value);
        }

        return urlBuilder.toString();
    }
}
