package com.mttk.orche.agent.javaCode;

import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.agent.javaCode.support.JavaCodeContext;
import com.mttk.orche.agent.javaCode.util.ComplieCodeUti;
import com.mttk.orche.agent.javaCode.util.ExecuteCodeUtil;
import com.mttk.orche.agent.javaCode.util.GenerateCodeUtil;
import com.mttk.orche.agent.javaCode.util.GenerateResultUtil;
import com.mttk.orche.agent.javaCode.util.JavaCodeUtil;

@AgentTemplateFlag(key = "_java-code-agent", name = "Java代码生成和执行", description = """
        根据用户需求,生成Java代码,并执行
         """, callDefineClass = JavaCodelCallDefine.class, i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "model", label = "模型", mode = "select", size = 1, mandatory = true, props = { "url:llmModel/query" })
@Control(key = "retryTimes", label = "重试次数", description = "当生成和编译代码失败后重新执行的次数.", size = 1, defaultVal = "3", props = {
        "dataType:number" })

public class JavaCodeAgent extends AbstractAgent {
    @Override
    protected String executeInternal(AgentParam para, String transactionId) throws Exception {

        //
        int retryTimes = para.getConfig().getInteger("retryTimes", 3);
        // 创建Java代码上下文
        JavaCodeContext javaCodeContext = new JavaCodeContext(para, transactionId);

        //
        for (int i = 0; i < retryTimes; i++) {
            try {
                GenerateCodeUtil.generateCode(javaCodeContext);
            } catch (Exception e) {
                JavaCodeUtil.sendError(javaCodeContext, "生成代码失败", e);
                continue;
            }
            //
            para.getContext().cancelCheck();
            try {
                ComplieCodeUti.compileCode(javaCodeContext);
            } catch (Throwable t) {
                JavaCodeUtil.sendError(javaCodeContext, "编译代码失败", t);
                javaCodeContext.setCompileError(t);
                continue;
            }
            //
            para.getContext().cancelCheck();
            try {
                ExecuteCodeUtil.executeCode(javaCodeContext);

            } catch (Throwable t) {
                JavaCodeUtil.sendError(javaCodeContext, "执行代码失败", t);
                javaCodeContext.setExecuteError(t);
                continue;
            }
            // OK
            break;
        }
        // Error
        return GenerateResultUtil.generateResult(javaCodeContext);
    }

}
