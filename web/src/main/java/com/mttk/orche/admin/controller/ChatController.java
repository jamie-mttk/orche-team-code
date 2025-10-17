package com.mttk.orche.admin.controller;

import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.admin.controller.util.AgentEventPrinterImpl;
import com.mttk.orche.admin.util.controller.ChatUploadUtil;
import com.mttk.orche.agentExecute.AgentExecuteRequestImpl;
import com.mttk.orche.core.impl.AdapterConfigImpl;
import com.mttk.orche.service.AgentExecuteCancelService;
import com.mttk.orche.service.AgentExecuteCancelService.CANCEL_RESULT;
import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.service.support.AgentExecuteRequest;
import com.mttk.orche.support.ServerUtil;

@RestController
@RequestMapping(value = "/chat")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    //
    private ObjectMapper mapper = new ObjectMapper();
    //
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    //
    @PostMapping(value = "/execute", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter execute(@RequestBody String body) throws Exception {
        Map<String, Object> bodyMap = mapper.readValue(body, Map.class);
        String agentId = (String) bodyMap.get("agentId");
        String sessionId = (String) bodyMap.get("sessionId");
        //
        checkParams(agentId, sessionId);

        // 创建SseEmitter，设置超时时间为60分钟
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        ScheduledFuture<?> heartbeatFuture = startHeartbeat(emitter);

        // 注册SSE事件监听器
        registerSSEMonitor(emitter, heartbeatFuture);

        // 使用CompletableFuture异步处理请求

        AgentExecuteService agentExecuteService = ServerUtil.getService(AgentExecuteService.class);

        try {
            long count = agentExecuteService.count(Filters.eq("sessionId", sessionId));
            if (count > 0) {
                // CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                // try {
                // return agentExecuteService.reply(sessionId, new
                // AgentEventPrinterImpl(emitter));
                // } catch (Exception e) {
                // // e.printStackTrace();
                // throw new RuntimeException(e);
                // }
                // });
                throw new IllegalArgumentException("sessionId已存在");
            } else {

                // String request = (String) bodyMap.get("request");
                // String taskName = (String) bodyMap.get("taskName");
                AgentExecuteRequest request = new AgentExecuteRequestImpl(
                        agentId, sessionId,
                        (String) bodyMap.get("taskName"), (String) bodyMap.get("request"));

                CompletableFuture<String> future = agentExecuteService.executeAsync(request,
                        new AgentEventPrinterImpl(emitter));
            }

            // logger.info("ChatController.startChat.future.start:" + future);
        } catch (Throwable t) {
            logger.error("ChatController.startChat.error", t);
        }

        return emitter;
    }

    //
    @PostMapping(value = "/replay", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter replay(@RequestBody String body) throws Exception {
        Map<String, Object> bodyMap = mapper.readValue(body, Map.class);
        // String agentId = (String) bodyMap.get("agentId");
        String sessionId = (String) bodyMap.get("sessionId");
        //
        checkSessionId(sessionId);

        // 创建SseEmitter，设置超时时间为60分钟
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        ScheduledFuture<?> heartbeatFuture = startHeartbeat(emitter);

        // 注册SSE事件监听器
        registerSSEMonitor(emitter, heartbeatFuture);

        // 使用CompletableFuture异步处理请求

        AgentExecuteService agentExecuteService = ServerUtil.getService(AgentExecuteService.class);

        try {
            long count = agentExecuteService.count(Filters.eq("sessionId", sessionId));
            if (count <= 0) {
                throw new IllegalArgumentException("sessionId不存在");
            } else {
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return agentExecuteService.reply(sessionId, new AgentEventPrinterImpl(emitter));
                    } catch (Exception e) {
                        // e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
            }

            // logger.info("ChatController.startChat.future.start:" + future);
        } catch (Throwable t) {
            logger.error("ChatController.startChat.error", t);
        }

        return emitter;
    }

    @PostMapping(value = "/scheduler")
    public Document scheduler(@RequestBody String body) throws Exception {
        //
        @SuppressWarnings("unchecked")
        Map<String, Object> bodyMap = mapper.readValue(body, Map.class);

        // 解析各个字段
        String schedulerStr = (String) bodyMap.get("scheduler");
        AdapterConfig schedulerConfig = new AdapterConfigImpl(mapper.readValue(schedulerStr, Map.class));
        //
        AgentExecuteService agentExecuteService = ServerUtil.getService(AgentExecuteService.class);
        //
        AgentExecuteRequest request = new AgentExecuteRequestImpl(
                (String) bodyMap.get(
                        "agentId"),
                null,
                (String) bodyMap.get("taskName"), (String) bodyMap.get("request"));

        agentExecuteService.schedule(request, schedulerConfig);
        //
        return new Document("result", true);
    }

    @PostMapping(value = "/cancel/{sessionId}")
    public Document cancel(@PathVariable("sessionId") String sessionId) throws Exception {
        AgentExecuteCancelService service = ServerUtil.getService(AgentExecuteCancelService.class);
        CANCEL_RESULT result = service.cancel(sessionId);
        return new Document("result", result.name());
    }

    @GetMapping(value = "/fileDownload")
    public void fileDownload(@RequestParam("sessionId") String sessionId,
            @RequestParam("fileName") String fileName,
            javax.servlet.http.HttpServletResponse response) {
        try {
            // 参数验证
            if (sessionId == null || sessionId.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getOutputStream().write("Session ID cannot be empty".getBytes("UTF-8"));
                return;
            }

            if (fileName == null || fileName.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getOutputStream().write("File name cannot be empty".getBytes("UTF-8"));
                return;
            }

            // 得到文件内容
            byte[] fileData = ServerUtil.getService(AgentExecuteService.class).loadAgentFile(sessionId, fileName);

            if (fileData == null || fileData.length == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 设置响应头 - 直接设置为二进制流，不使用字符编码
            response.setContentType("application/octet-stream");
            String encodeFileName = URLEncoder.encode(fileName, "UTF-8")
                    .replace("+", "%20"); // 将"+"替换为"%20"
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFileName + "\"");
            response.setContentLength(fileData.length);

            // logger.info("File download scduccessful:" + fileData.length);

            // 直接写入二进制数据，避免任何字符编码转换
            response.getOutputStream().write(fileData);
            response.getOutputStream().flush();

        } catch (Exception e) {
            logger.error("File download failed: sessionId={}, fileName={}",
                    sessionId, fileName, e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                String errorMessage = "File download failed: " + e.getMessage();
                response.getOutputStream().write(errorMessage.getBytes("UTF-8"));
            } catch (Exception ex) {
                logger.error("Error writing error response", ex);
            }
        }
    }

    // 上传文件到临时位置
    @PostMapping(value = "/uploadFile")
    public Document doUpload(HttpServletRequest request) throws Exception {
        return ChatUploadUtil.doUpload(request);
    }

    /**
     * 启动心跳任务
     */
    private ScheduledFuture<?> startHeartbeat(SseEmitter emitter) {
        return executor.scheduleAtFixedRate(() -> {
            try {
                // 发送心跳消息
                emitter.send(SseEmitter.event()
                        .name("keep-alive")
                        .data(("{\"type\":\"keep-alive\",\"timestamp\":" + System.currentTimeMillis() + "}")
                                .getBytes("UTF-8")));
            } catch (Throwable t) {
                // 发送心跳失败，关闭连接
                logger.error("Heartbeat failed, closing connection", t);
                emitter.completeWithError(t);
            }
        }, 15L, 15L, TimeUnit.SECONDS);
    }

    /**
     * 注册SSE事件监听器
     */
    private void registerSSEMonitor(SseEmitter emitter, ScheduledFuture<?> heartbeatFuture) {
        // 监听SSE完成事件
        emitter.onCompletion(() -> {
            logger.info("SSE connection completed");
            heartbeatFuture.cancel(true);
        });

        // 监听连接超时事件
        emitter.onTimeout(() -> {
            logger.error("SSE connection error:timeout");
            heartbeatFuture.cancel(true);
            emitter.complete();
        });

        // 监听连接错误事件
        emitter.onError((ex) -> {
            logger.error("SSE connection error:" + ex.getMessage(), ex);
            heartbeatFuture.cancel(true);
            emitter.completeWithError(ex);
        });
    }

    private void checkParams(String agentId, String sessionId) {
        // 参数校验
        if (agentId == null || agentId.trim().isEmpty()) {
            throw new IllegalArgumentException("agentId不能为空");
        }
        //
        checkSessionId(sessionId);
    }

    private void checkSessionId(String sessionId) {
        //
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }
        if (sessionId.length() > 32) {
            throw new IllegalArgumentException("sessionId长度不能超过32个字符");
        }
    }

}
