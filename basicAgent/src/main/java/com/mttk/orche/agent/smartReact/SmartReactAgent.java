package com.mttk.orche.agent.smartReact;

import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.agent.impl.BaseReactAgent;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.support.JsonUtil;
import com.mttk.orche.addon.agent.AgentContext;

import com.mttk.orche.addon.AdapterConfig;

@AgentTemplateFlag(key = "_smart-react-agent", name = "自动规划React智能体", description = """
                根据配置的系统提示词X和用户输入,自动生成React Agent需要的系统提示词和用户提示词
                """, i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "maxSteps", label = "最大轮次", description = "思考和执行的轮次数", size = 1, defaultVal = "10", props = {
                "dataType:number" })
@Control(key = "model", label = "模型", mode = "select", size = 1, mandatory = true, props = { "url:llmModel/query" })

@Control(key = "generatePrompt", label = "生成提示词的提示词", description = "根据此提示词生成React 智能体系统提示词和下一步提示词.", size = 1, defaultVal = """
                根据用户输入调用合适的工具执行.执行完成后评估并再次挑选合适的工具.循环执行直到完成用户输入任务.
                当工具执行失败时保留现有执行成果重新规划执行计划.
                """, mandatory = false, mode = "editor", props = {
                "language:markdown", "height:480" })

public class SmartReactAgent extends BaseReactAgent {
        private static ThreadLocal<PromptGenerated> promptGenerated = new ThreadLocal<>();

        @Override
        public String getSystemPrompt(AgentParam para) throws Exception {
                return promptGenerated.get().getSystemPrompt();
        }

        @Override
        public String getNextStepPrompt(AgentParam para) throws Exception {
                return promptGenerated.get().getNextStepPrompt();
        }

        @Override
        public String execute(AgentContext context, AdapterConfig agentConfig, String requestRaw,
                        String toolName) throws Exception {
                promptGenerated.set(generatePrompt(context, agentConfig, requestRaw));
                //
                return super.execute(context, agentConfig, requestRaw, toolName);
        }

        // 生成PromptGenerated并放到ThreadLocal中
        private PromptGenerated generatePrompt(AgentContext context, AdapterConfig agentConfig, String requestRaw)
                        throws Exception {
                AgentParam para = new AgentParam(context, agentConfig, requestRaw, null);
                String generatePrompt = para.getConfig().getString("generatePrompt",
                                para.getRequest().getString("query"));
                //
                String prompt = Prompt.PROMPT_GENERATION_SYSTEM_PROMPT;
                prompt = prompt.replace("${__now}", AgentUtil.now());
                prompt = prompt.replace("${generatePrompt}", generatePrompt);

                String result = AgentUtil.callLlm(context,
                                para.getConfig().getStringMandatory("model"), "生成系统提示词和下一步提示词",
                                prompt);
                result = JsonUtil.cleanJsonString(result);
                return PromptGenerated.parse(result);
        }
}
