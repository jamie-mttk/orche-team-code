package com.mttk.orche.addon.agent.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;

public class PromptUtil {

    // 匹配 ${abc} 格式的正则表达式
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    public static String parsePrompt(String prompt) {
        return prompt;
    }

    public static String parsePrompt(String prompt, AgentParam agentParam, Map<String, Object> map) {
        return parsePrompt(prompt, new AgentPromptCallback(agentParam, map));
    }

    public static String parsePrompt(String prompt, AgentParam agentParam) {
        return parsePrompt(prompt, agentParam, new HashMap<>());
    }

    public static String parsePrompt(String prompt, AgentParam agentParam, Function<String, Object> callback) {
        return parsePrompt(prompt, new AgentPromptCallback(agentParam) {
            @Override
            public Object apply(String key) {
                Object obj = super.apply(key);
                if (obj != null) {
                    return obj;
                }
                //
                if (callback != null) {
                    return callback.apply(key);
                } else {
                    return null;
                }
            }
        });
    }

    /**
     * Parse variables in prompt
     * 
     * @param prompt   Original prompt
     * @param callback Variable value callback function
     * @return Prompt with variables replaced
     */
    public static String parsePrompt(String prompt, Function<String, Object> callback) {
        if (prompt == null || callback == null) {
            return prompt;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(prompt);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1); // Get content inside ${}
            Object variableValue = callback.apply(variableName); // Call callback to get value

            // If callback returns null, replace with empty string; otherwise use toString()
            String replacement = variableValue != null ? variableValue.toString() : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
