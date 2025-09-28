package com.mttk.orche.addon.agent;

public interface AgentEventPrinter {
    // 发送数据
    void send(ChatResonseMessage message) throws Exception;

    // 完成,t==null说明是正常完成,否则是异常完成
    public default void complete(Throwable t) throws Exception {
        if (t != null) {
            t.printStackTrace();
        }
    }
}
