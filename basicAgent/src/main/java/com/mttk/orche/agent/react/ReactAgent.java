package com.mttk.orche.agent.react;

import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.BaseReactAgent;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;

@AgentTemplateFlag(key = "_react-agent", name = "React智能体", description = """
                边思考边执行
                """, props = "SUPPORT_MEMBER", i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "maxSteps", label = "最大轮次", description = "思考和执行的轮次数", size = 1, defaultVal = "10", props = {
                "dataType:number" })
@Control(key = "model", label = "模型", mode = "select", size = 1, mandatory = true, props = { "url:llmModel/query" })
@Control(key = "systemPrompt", label = "系统提示词", description = "没有设置会使用系统缺省提示词", size = 1, defaultVal = Prompt.SYSTEM_PROMPT, mode = "editor", props = {
                "language:markdown", "height:480" })
@Control(key = "nextStepPrompt", label = "下一步提示词", description = "没有设置会使用系统缺省提示词", size = 1, defaultVal = Prompt.NEXT_STEP_PROMPT, mode = "editor", props = {
                "language:markdown", "height:240" })

public class ReactAgent extends BaseReactAgent {
        @Override
        public String getSystemPrompt(AgentParam para) throws Exception {
                return para.getConfig().getString("systemPrompt", Prompt.SYSTEM_PROMPT);
        }

        @Override
        public String getNextStepPrompt(AgentParam para) throws Exception {
                return para.getConfig().getString("nextStepPrompt", Prompt.NEXT_STEP_PROMPT);
        }
}
