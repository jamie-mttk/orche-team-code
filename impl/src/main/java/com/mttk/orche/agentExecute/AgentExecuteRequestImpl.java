package com.mttk.orche.agentExecute;

import com.mttk.orche.service.support.AgentExecuteRequest;

/**
 * Agent执行请求实现类
 * 实现AgentExecuteRequest接口，通过构造函数传入所有属性
 */
public class AgentExecuteRequestImpl implements AgentExecuteRequest {

    private final String agentId;
    private final String sessionId;
    private String request;
    private final String taskName;
    private final MODE mode;

    public AgentExecuteRequestImpl(String agentId, String sessionId, String taskName, String request) {
        this(agentId, sessionId, taskName, request, AgentExecuteRequest.MODE.MANUAL);
    }

    /**
     * 构造函数
     * 
     * @param agentId   Agent ID
     * @param sessionId 会话ID
     * @param request   请求内容
     * @param taskName  任务名称
     * @param mode      执行模式
     */
    public AgentExecuteRequestImpl(String agentId, String sessionId, String taskName, String request, MODE mode) {
        this.agentId = agentId;
        this.sessionId = sessionId;
        this.request = request;
        this.taskName = taskName;
        this.mode = mode;
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getRequest() {
        return request;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    @Override
    public MODE getMode() {
        return mode;
    }

    @Override
    public void setRequest(String request) {
        this.request = request;
    }
}
