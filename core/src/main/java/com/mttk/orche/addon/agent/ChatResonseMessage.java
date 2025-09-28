package com.mttk.orche.addon.agent;

import java.io.Serializable;
import java.util.Objects;

/**
 * 聊天响应消息类
 * 符合Java Bean规范的标准类
 */
public class ChatResonseMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private String id;
    private String data;
    private long sendTime;

    /**
     * 默认构造函数
     */
    public ChatResonseMessage() {
    }

    /**
     * 构造函数
     * 
     * @param type 消息类型
     * @param id   消息ID
     */
    public ChatResonseMessage(String type, String id) {
        this(type, id, null);
    }

    /**
     * 构造函数
     * 
     * @param type 消息类型
     * @param id   消息ID
     * @param data 消息数据
     */
    public ChatResonseMessage(String type, String id, String data) {
        this(type, id, data, null);
    }

    public ChatResonseMessage(String type, String id, String data, Long sendTime) {
        this.type = type;
        this.id = id;
        this.data = data;

        if (sendTime == null) {
            this.sendTime = System.currentTimeMillis();
        } else {
            this.sendTime = sendTime;
        }
    }

    /**
     * 获取消息类型
     * 
     * @return 消息类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置消息类型
     * 
     * @param type 消息类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取消息ID
     * 
     * @return 消息ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置消息ID
     * 
     * @param id 消息ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取消息数据
     * 
     * @return 消息数据
     */
    public String getData() {
        return data;
    }

    /**
     * 设置消息数据
     * 
     * @param data 消息数据
     */
    public void setData(String data) {
        this.data = data;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "ChatResonseMessage [type=" + type + ", id=" + id + ", data=" + data + ", sendTime=" + sendTime + "]";
    }

}
