package com.mttk.orche.service.support;

/**
 * Agent执行请求接口
 * 定义Agent执行所需的基本参数获取方法
 */
public interface AgentExecuteRequest {

    /**
     * 执行模式枚举
     */
    public enum MODE {
        /**
         * 手动执行
         */
        MANUAL,

        /**
         * 调度执行
         */
        SCHEDULER
    }

    /**
     * 获取Agent ID
     * 
     * @return Agent ID
     */
    String getAgentId();

    /**
     * 获取会话ID
     * 
     * @return 会话ID
     */
    String getSessionId();

    /**
     * 获取请求内容
     * 
     * @return 请求内容
     */
    String getRequest();

    /**
     * 获取任务名称
     * 
     * @return 任务名称
     */
    String getTaskName();

    /**
     * 获取执行模式
     * 
     * @return 执行模式
     */
    MODE getMode();

    void setRequest(String request);
}