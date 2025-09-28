package com.mttk.orche.addon.agent;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import com.mttk.orche.addon.Context;
import com.mttk.orche.util.StringUtil;

public interface AgentContext extends Context {

    public ChatResonseMessage sendResponse(ChatResonseMessage msg);

    public default ChatResonseMessage sendResponse(String type, String id, String data) {
        return sendResponse(new ChatResonseMessage(type, id, data));
    }

    public default ChatResonseMessage sendResponse(String type, String data) {
        return sendResponse(type, StringUtil.getUUID(), data);
    }

    public String getSessionId() throws Exception;

    public String execute(String agentId, String toolName, String request) throws Exception;

    public List<String> getToolDefine(String agentId) throws Exception;

    // 创建ChatMemory并加入到堆栈中
    Stack<ChatMemory> createChatMemory() throws Exception;

    // 获取堆栈顶部的ChatMemory
    Stack<ChatMemory> getChatMemoryStack() throws Exception;
    // public ChatMemory createChatMemory() throws Exception;

    //
    public boolean isCancelRequested() throws Exception;

    //
    public default void cancelCheck() throws Exception {
        if (isCancelRequested()) {
            throw new RuntimeException("cancel requested");
        }
    }
}
