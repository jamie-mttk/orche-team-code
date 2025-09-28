package com.mttk.orche.agent.javaCode.util;

import com.mttk.orche.agent.javaCode.support.JavaCodeBase;
import com.mttk.orche.agent.javaCode.support.JavaCodeContext;
import com.mttk.orche.agent.javaCode.support.JavaCodeInput;
import com.mttk.orche.agent.javaCode.support.JavaCodeOutput;
import com.mttk.orche.util.StringUtil;

public class ExecuteCodeUtil {

    // 执行
    public static void executeCode(JavaCodeContext javaCodeContext) throws Exception {
        //
        Class<? extends JavaCodeBase> compiledClass = javaCodeContext.getCompiledClass();
        //
        JavaCodeBase javaCodeBase = compiledClass.getDeclaredConstructor().newInstance();
        //
        JavaCodeInput javaCodeInput = new JavaCodeInput(javaCodeContext.getPara(), javaCodeContext.getTransactionId(),
                javaCodeContext.getInputFiles());
        JavaCodeOutput javaCodeOutput = new JavaCodeOutput();
        javaCodeBase.call(javaCodeInput, javaCodeOutput);
        //
        javaCodeContext.setResult(javaCodeOutput);
        //
        if (StringUtil.notEmpty(javaCodeOutput.getContent())) {
            JavaCodeUtil.sendMessage(javaCodeContext, javaCodeOutput.getContent());
        }
    }
}
