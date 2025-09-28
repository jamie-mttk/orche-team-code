package com.mttk.orche.addon.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 聊天消息类
 * 符合Java Bean规范的标准类
 */
public class ChatMessage {

    private MessageRole role; // 消息角色
    private String content; // 消息内容
    private String name; // 可选：消息发送者名称（如函数名）
    private String toolCallId; // 可选，用于工具消息
    private List<ToolCall> toolCalls; // 用于assistant消息,用于记录LLM返回

    /**
     * 默认构造函数
     */
    public ChatMessage() {
        this.toolCalls = new ArrayList<>();
    }

    /**
     * 构造函数
     * 
     * @param role    消息角色
     * @param content 消息内容
     */
    public ChatMessage(MessageRole role, String content) {
        this(role, content, null, null, new ArrayList<>());
    }

    /**
     * 完整构造函数
     * 
     * @param role       消息角色
     * @param content    消息内容
     * @param name       消息发送者名称
     * @param toolCallId 工具调用ID
     * @param toolCalls  工具调用列表
     */
    public ChatMessage(MessageRole role, String content, String name, String toolCallId, List<ToolCall> toolCalls) {
        Objects.requireNonNull(role, "Message role cannot be null");
        Objects.requireNonNull(content, "Message content cannot be null");

        this.role = role;
        this.content = content;
        this.name = name;
        this.toolCallId = toolCallId;
        this.toolCalls = toolCalls != null ? new ArrayList<>(toolCalls) : new ArrayList<>();
    }

    /**
     * 创建用户消息
     * 
     * @param context 消息内容
     * @return ChatMessage实例
     */
    public static ChatMessage user(String context) {
        return new ChatMessage(MessageRole.USER, context);
    }

    /**
     * 创建系统消息
     * 
     * @param context 消息内容
     * @return ChatMessage实例
     */
    public static ChatMessage system(String context) {
        return new ChatMessage(MessageRole.SYSTEM, context);
    }

    /**
     * 创建工具消息
     * 
     * @param context    消息内容
     * @param toolCallId 工具调用ID
     * @return ChatMessage实例
     */
    public static ChatMessage tool(String context, String toolCallId) {
        return new ChatMessage(MessageRole.TOOL, context, null, toolCallId, new ArrayList<>());
    }

    /**
     * 创建助手消息
     * 
     * @param context 消息内容
     * @return ChatMessage实例
     */
    public static ChatMessage assistant(String context) {
        return new ChatMessage(MessageRole.ASSISTANT, context);
    }

    /**
     * 获取消息角色
     * 
     * @return 消息角色
     */
    public MessageRole getRole() {
        return role;
    }

    /**
     * 获取消息内容
     * 
     * @return 消息内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 获取消息发送者名称
     * 
     * @return 消息发送者名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取工具调用ID
     * 
     * @return 工具调用ID
     */
    public String getToolCallId() {
        return toolCallId;
    }

    /**
     * 获取工具调用列表
     * 
     * @return 工具调用列表
     */
    public List<ToolCall> getToolCalls() {
        return toolCalls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(role, that.role) &&
                Objects.equals(content, that.content) &&
                Objects.equals(name, that.name) &&
                Objects.equals(toolCallId, that.toolCallId) &&
                Objects.equals(toolCalls, that.toolCalls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, content, name, toolCallId, toolCalls);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "role=" + role +
                ", content='" + content + '\'' +
                ", name='" + name + '\'' +
                ", toolCallId='" + toolCallId + '\'' +
                ", toolCalls=" + toolCalls +
                '}';
    }
}