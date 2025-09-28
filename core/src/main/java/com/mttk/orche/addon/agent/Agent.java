package com.mttk.orche.addon.agent;

import java.util.List;

import com.mttk.orche.addon.AdapterConfig;

public interface Agent {
    // 执行Agent
    // public default String execute(AgentContext context, AdapterConfig
    // agentConfig, String request) throws Exception {
    // return this.execute(context, agentConfig, request, null);
    // }

    // 当getToolDefine返回多个结果时,toolName给出具体调用哪一个工具
    public String execute(AgentContext context, AdapterConfig agentConfig, String request, String toolName)
            throws Exception;

    // 获取Agent的工具调用描述
    List<String> getToolDefine(AdapterConfig agentConfig) throws Exception;
}
