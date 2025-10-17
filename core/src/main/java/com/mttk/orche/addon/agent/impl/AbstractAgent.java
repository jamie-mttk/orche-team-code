package com.mttk.orche.addon.agent.impl;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;

/**
 * 抽象Agent基类，使用函数式接口提供更优雅的执行方式
 * 替代匿名内部类，代码更加简洁易读
 */
public abstract class AbstractAgent extends ToolDefineReadyAgent {

    @Override
    public String execute(AgentContext context, AdapterConfig agentConfig, String request, String toolName)
            throws Exception {
        // 使用函数式接口和方法引用，代码更加简洁优雅
        return AgentRunnerSupport.execute(context, agentConfig, request, toolName, this::executeInternal);
    }

    /**
     * 子类需要实现的具体执行逻辑
     * 默认实现调用旧版本的方法签名以保持向后兼容性
     * 
     * @param para          Agent参数封装
     * @param transactionId 事务ID
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected String executeInternal(AgentParam para, String transactionId) throws Exception {

        return "done";
    }

}
