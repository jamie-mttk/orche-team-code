package com.mttk.orche.agent.javaCode.util;

import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.agent.javaCode.support.JavaCodeContext;
import com.mttk.orche.util.ThrowableUtil;

public class JavaCodeUtil {
    // 发送返回消息
    public static void sendMessage(JavaCodeContext javaCodeContext, String message) {
        javaCodeContext.getPara().getContext()
                .sendResponse(new ChatResonseMessage("_data-content", javaCodeContext.getTransactionId(), message));

    }

    public static void sendError(JavaCodeContext javaCodeContext, String name, Throwable t) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("### ").append(name).append("\n");
        sb.append("#### 错误信息\n").append(ThrowableUtil.dump2String(t)).append("\n");
        javaCodeContext.getPara().getContext()
                .sendResponse(
                        new ChatResonseMessage("_data-content", javaCodeContext.getTransactionId(), sb.toString()));

    }
}
