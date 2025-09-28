package com.mttk.orche.agent.webSearch;

import java.util.ArrayList;
import java.util.List;

import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.agent.impl.PromptUtil;
import com.mttk.orche.util.StringUtil;

public class ThinkKeyWords {
    public static List<String> thinkKeyWords(
            AgentParam para,
            String rewrite_query, List<String> keyWordSearched)
            throws Exception {
        String decomposeResult = thinkAndDecompose(para, rewrite_query);
        // parseItems
        List<String> items = parseItems(decomposeResult);
        // 去掉已经搜过过的
        items = items.stream().filter(w -> !keyWordSearched.contains(w)).toList();
        //
        return items;

    }

    private static String thinkAndDecompose(AgentParam para,
            String rewrite_query)
            throws Exception {
        // 系统提示词
        String prompt = Prompt.THINK_AND_DECOMPOSE;
        // prompt.replace("${query}", rewrite_query)
        prompt = PromptUtil.parsePrompt(prompt, para, key -> {
            if ("query".equals(key)) {
                return rewrite_query;
            }
            return null;
        });
        // prompt = prompt.replace("${KeywordNum}", config.getInteger("KeywordNum") +
        // "");
        // prompt = prompt.replace("${now}", new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss").format(new Date()));
        // logger.info("prompt of thinkAndDecompose:\n" + prompt);
        String result = AgentUtil.callLlm(para.getContext(), para.getConfig().getString("model"), "查询关键字分解", prompt);

        return result;
    }

    private static List<String> parseItems(String input) {
        List<String> resultList = new ArrayList<>();
        // 处理空输入
        if (StringUtil.isEmpty(input)) {
            return resultList;
        }

        // 按行分割输入
        String[] lines = input.split("\\r?\\n");

        for (String line : lines) {
            // 去除行首空白字符
            String trimmedLine = line.trim();

            // 检查是否以 - 开头
            if (trimmedLine.startsWith("-")) {
                // 提取 - 后面的内容
                String content = trimmedLine.substring(1).trim();
                if (!content.isEmpty()) {
                    resultList.add(content);
                }
            }
        }

        return resultList;
    }
}
