package com.mttk.orche.admin.controller.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mttk.orche.addon.agent.AgentEventPrinter;
import com.mttk.orche.addon.agent.ChatResonseMessage;

public class AgentEventPrinterImpl implements AgentEventPrinter {
    private SseEmitter emitter;
    private ObjectMapper mapper = new ObjectMapper();
    //
    private static final Logger logger = LoggerFactory.getLogger(AgentEventPrinterImpl.class);

    public AgentEventPrinterImpl(SseEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void send(ChatResonseMessage message) throws Exception {
        String data = null;

        data = mapper.writeValueAsString(message);

        // logger.info("SSE response" + data);
        // if (message.getType().endsWith("-start") ||
        // message.getType().endsWith("-end")) {
        // logger.info("SSE response" + data);
        // }

        emitter.send(SseEmitter.event().data(data.getBytes("UTF-8")));

    }

    @Override
    public void complete(Throwable t) throws Exception {
        if (t == null) {
            sendCompletionMessage();
        } else {
            handleChatError(t);
        }
    }

    /**
     * 发送完成消息
     */
    private void sendCompletionMessage() throws Exception {
        try {
            emitter.send(SseEmitter.event().data("[DONE]".getBytes("UTF-8")));
            emitter.complete();
            logger.info("SSE connection completed");
            // try {
            // throw new Exception("test");
            // } catch (Throwable e) {

            // e.printStackTrace();
            // }
        } catch (Throwable e) {

            throw e;
        }
    }

    /**
     * 处理聊天错误
     */
    private void handleChatError(Throwable e) {

        try {
            emitter.send(SseEmitter.event().data("[DONE]".getBytes("UTF-8")));
            emitter.complete();
            logger.info("SSE connection completed with error", e);
        } catch (Throwable ex) {

            emitter.completeWithError(ex);
        }
    }

}
