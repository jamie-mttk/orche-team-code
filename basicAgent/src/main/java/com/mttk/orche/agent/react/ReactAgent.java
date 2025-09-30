package com.mttk.orche.agent.react;

import com.mttk.orche.addon.agent.impl.BaseReactAgent;
import com.mttk.orche.addon.agent.impl.PromptUtil;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.mttk.orche.addon.RequestTarget;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.support.JsonUtil;
import com.mttk.orche.util.StringUtil;
import com.mttk.orche.util.ThrowableUtil;

@AgentTemplateFlag(key = "_react-agent", name = "React智能体", description = """
                按照最大轮次循环,每次循环体里面调用大模型然后执行工具.\n第一次会提交系统提示词和用户请求,后续会提交下一步提示词
                """, props = "SUPPORT_MEMBER", i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "maxSteps", label = "最大轮次", description = "思考和执行的轮次数", size = 1, defaultVal = "10", props = {
                "dataType:number" })
@Control(key = "model", label = "模型", mode = "select", size = 1, mandatory = true, props = { "url:llmModel/query" })

// 防止按钮直接对齐左边,所以前面加一个标题
@Control(key = "dummy", label = "操作", mode = "dummy", size = -1)
@Control(key = "setPlanAgent", label = "先规划后执行", description = "根据用户需求规划任务然后逐步执行;有异常会重新规划任务", mode = "button", size = -1, props = {
                "type:primary", "url:/util/forward", "target:agent._react-agent" })
@Control(key = "setNormalAgent", label = "边规划边执行", description = "根据用户需求执行,执行完成后规划下一步", mode = "button", size = -1, props = {
                "type:success", "url:/util/forward", "target:agent._react-agent" })
// 还有一个复杂方法时定义一个editor用户输入本操作需要的提示词如genPrompt,使用一个变量如showGenPromop控制是否显示.本按钮设置showGenPromop=true
// 显示genPrompt后还显示生成和取消两个按钮
@Control(key = "smartGenPrompt", label = "智能生成", description = "根据用户在系统提示词的输入用大模型生成系统提示词和下一步提示词", mode = "button", size = -1, props = {
                "type:warning", "url:/util/forward", "target:agent._react-agent" })

@Control(key = "systemPrompt", label = "系统提示词", description = "用于指导React智能体如何执行和跟踪任务", size = 1, defaultVal = PromptPlan.SYSTEM_PROMPT, mode = "editor", props = {
                "language:markdown", "height:480" })
@Control(key = "nextStepPrompt", label = "下一步提示词", description = "在一步完成后触发React智能体进行下一步操作", size = 1, defaultVal = PromptPlan.NEXT_STEP_PROMPT, mode = "editor", props = {
                "language:markdown", "height:240" })

public class ReactAgent extends BaseReactAgent implements RequestTarget {
        @Override
        public String getSystemPrompt(AgentParam para) throws Exception {
                return para.getConfig().getString("systemPrompt", PromptPlan.SYSTEM_PROMPT);
        }

        @Override
        public String getNextStepPrompt(AgentParam para) throws Exception {
                return para.getConfig().getString("nextStepPrompt", PromptPlan.NEXT_STEP_PROMPT);
        }

        @Override
        public Document handleRequest(Document in) throws Exception {

                //
                Document data = (Document) in.get("data");
                if (data == null) {
                        throw new RuntimeException("No data is found in request");
                }
                //
                // AdapterConfig config = new AdapterConfigImpl(data);

                //
                String buttonKey = ((Document) in.get("config")).getString("key");
                try {
                        if ("setPlanAgent".equals(buttonKey)) {
                                return new Document("systemPrompt", PromptPlan.SYSTEM_PROMPT).append("nextStepPrompt",
                                                PromptPlan.NEXT_STEP_PROMPT);
                        } else if ("setNormalAgent".equals(buttonKey)) {
                                return new Document("systemPrompt",
                                                PromptNormal.SYSTEM_PROMPT).append("nextStepPrompt",
                                                                PromptNormal.NEXT_STEP_PROMPT);
                        } else if ("smartGenPrompt".equals(buttonKey)) {
                                return generatePrompt(data);
                        } else {
                                throw new RuntimeException("Invalid config key is found in request:" + buttonKey);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        return new Document("__error", ThrowableUtil.dump2String(e));
                }

        }

        private Document generatePrompt(Document data)
                        throws Exception {
                String generatePrompt = data.getString("systemPrompt");
                if (StringUtil.isEmpty(generatePrompt)) {
                        throw new RuntimeException("请输入系统提示词");
                }
                String model = data.getString("model");
                if (StringUtil.isEmpty(model)) {
                        throw new RuntimeException("请输入模型");
                }
                // 生成prompt
                Map<String, Object> map = new HashMap<>();
                map.put("generatePrompt", generatePrompt);
                String prompt = PromptGen.PROMPT_GENERATION_SYSTEM_PROMPT;
                prompt = PromptUtil.parsePrompt(prompt, null, map);

                String result = AgentUtil.callLlm(null, model, "生成系统提示词和下一步提示词", prompt);
                result = JsonUtil.cleanJsonString(result);
                return Document.parse(result);
        }
}
