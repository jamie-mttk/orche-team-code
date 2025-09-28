package com.mttk.orche.addon.agent.impl;

/**
 * 函数式接口，用于定义Agent执行逻辑
 * 替代匿名内部类，使代码更加简洁优雅
 */
@FunctionalInterface
public interface AgentExecutor {
    /**
     * 执行Agent逻辑
     * 
     * @param para          Agent参数封装
     * @param transactionId 事务ID
     * @return 执行结果
     * @throws Exception 执行异常
     */
    String execute(AgentRunnerSupport.AgentParam para, String transactionId) throws Exception;
}
