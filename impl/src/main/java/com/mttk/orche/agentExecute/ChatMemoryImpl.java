package com.mttk.orche.agentExecute;

import java.util.ArrayList;
import java.util.List;

import com.mttk.orche.addon.agent.ChatMemory;
import com.mttk.orche.addon.agent.ChatMessage;

/**
 * 聊天内存接口的默认实现
 * 使用ArrayList存储聊天消息
 */
class ChatMemoryImpl implements ChatMemory {
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Override
    public void addMessage(ChatMessage message) {
        chatMessages.add(message);
    }

    @Override
    public void addMessages(List<ChatMessage> newMessages) {
        chatMessages.addAll(newMessages);
    }

    @Override
    public ChatMessage getLastMessage() {
        return chatMessages.isEmpty() ? null : chatMessages.get(chatMessages.size() - 1);
    }

    @Override
    public void clear() {
        chatMessages.clear();
    }

    @Override
    public int size() {
        return chatMessages.size();
    }

    @Override
    public boolean isEmpty() {
        return chatMessages.isEmpty();
    }

    @Override
    public ChatMessage get(int index) {
        return chatMessages.get(index);
    }

    @Override
    public List<ChatMessage> getMessages() {
        return chatMessages;
    }
}
