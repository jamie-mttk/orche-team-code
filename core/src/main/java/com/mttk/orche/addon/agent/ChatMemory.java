package com.mttk.orche.addon.agent;

import java.util.List;

/**
 * 聊天内存接口
 * 用于管理聊天消息的存储和访问
 */
public interface ChatMemory {

    /**
     * 添加单条消息
     * 
     * @param message 要添加的消息
     */
    void addMessage(ChatMessage message);

    /**
     * 添加多条消息
     * 
     * @param newMessages 要添加的消息列表
     */
    void addMessages(List<ChatMessage> newMessages);

    /**
     * 获取最后一条消息
     * 
     * @return 最后一条消息，如果没有消息则返回null
     */
    ChatMessage getLastMessage();

    /**
     * 清空所有消息
     */
    void clear();

    /**
     * 获取消息数量
     * 
     * @return 消息数量
     */
    int size();

    /**
     * 检查是否为空
     * 
     * @return 如果没有消息返回true，否则返回false
     */
    boolean isEmpty();

    /**
     * 根据索引获取消息
     * 
     * @param index 消息索引
     * @return 指定索引的消息
     */
    ChatMessage get(int index);

    /**
     * 获取所有消息
     * 
     * @return 所有消息的列表
     */
    List<ChatMessage> getMessages();
}
