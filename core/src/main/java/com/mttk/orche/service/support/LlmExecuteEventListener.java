package com.mttk.orche.service.support;

import com.mttk.orche.addon.agent.ChatMessage;

//大模型执行事件监听接口,当前仅仅使用了onEnd
public interface LlmExecuteEventListener {
    // 大模型执行开始事件
    public default void onRequest(String requestId, String request) throws Exception {
    }

    // 大模型执行结束事件
    public default void onResponse(String requestId, ChatMessage responseMessage) throws Exception {
    }

    // // 大模型执行错误事件
    // public void onLlmExecuteError(String modelId, String name, List<ChatMessage>
    // messages, List<String> functions,
    // Throwable t);
}
