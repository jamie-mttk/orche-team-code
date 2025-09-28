package com.mttk.orche.agent.mcp.util.stdio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mttk.orche.agent.mcp.util.common.MCPTransport;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * STDIO传输层实现
 * 基于标准输入输出的MCP传输
 */
public class STDIOTransport implements MCPTransport {

    private final ObjectMapper objectMapper;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final long timeoutMillis;
    private final ExecutorService executorService;

    public STDIOTransport() {
        this(60000); // 默认60秒超时
    }

    public STDIOTransport(long timeoutMillis) {
        this.objectMapper = new ObjectMapper();
        this.reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
        this.timeoutMillis = timeoutMillis;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public STDIOTransport(InputStream inputStream, OutputStream outputStream) {
        this(inputStream, outputStream, 60000);
    }

    public STDIOTransport(InputStream inputStream, OutputStream outputStream, long timeoutMillis) {
        this.objectMapper = new ObjectMapper();
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
        this.timeoutMillis = timeoutMillis;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public JsonNode sendRequest(JsonNode request) throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(request);

        // 发送请求
        writer.println(jsonRequest);
        writer.flush();

        // 使用超时机制读取响应
        Future<String> future = executorService.submit(() -> {
            try {
                return reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            String responseLine = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            if (responseLine == null) {
                throw new IOException("连接已关闭");
            }
            return objectMapper.readTree(responseLine);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new IOException("读取响应超时 (" + timeoutMillis + "ms)");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException && cause.getCause() instanceof IOException) {
                throw (IOException) cause.getCause();
            }
            throw new IOException("读取响应时发生错误", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("读取响应被中断", e);
        }
    }

    @Override
    public void testConnection() throws Exception {
        // 发送一个简单的ping请求
        ObjectNode pingRequest = objectMapper.createObjectNode();
        pingRequest.put("jsonrpc", "2.0");
        pingRequest.put("id", 1);
        pingRequest.put("method", "ping");

        sendRequest(pingRequest);
    }

    @Override
    public void close() throws IOException {
        IOException lastException = null;

        try {
            reader.close();
        } catch (IOException e) {
            lastException = e;
        }

        try {
            writer.close();
        } catch (Exception e) {
            if (lastException == null) {
                lastException = new IOException("Failed to close writer", e);
            }
        }

        // 关闭线程池
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            if (lastException == null) {
                lastException = new IOException("Failed to shutdown executor service", e);
            }
        }

        if (lastException != null) {
            throw lastException;
        }
    }
}
