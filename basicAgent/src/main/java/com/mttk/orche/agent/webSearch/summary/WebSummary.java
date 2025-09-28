package com.mttk.orche.agent.webSearch.summary;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.agent.impl.PromptUtil;
import com.mttk.orche.agent.webSearch.Prompt;
import com.mttk.orche.agent.webSearch.SearchItem;
import com.mttk.orche.support.JsonUtil;
import com.mttk.orche.util.StringUtil;

public class WebSummary {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int SUMMARY_MAX_TOKEN = 1024 * 10; // 汇总长度限制
    private static final int DEFAULT_CONTENT_TOKEN = 32 * 1024; // 缺省的content token

    public static SummaryResult reasoningAndSummary(
            AgentParam para, String query,
            List<SearchItem> items, SummaryResult lastSummarize) throws Exception {

        // 第一步：网页内容筛选
        List<SearchItem> selectedItems = selectRelevantWebpages(para, query, items, lastSummarize);

        if (selectedItems.isEmpty()) {
            return new SummaryResult(0, query, "没有找到相关的内容", "");
        }

        // 第二步：生成报告
        String content = buildSelectedContent(selectedItems);
        String prompt = buildSummaryPrompt(query, content, lastSummarize);
        String result = AgentUtil.callLlm(para.getContext(), para.getConfig().getString("model"), "内容评估和汇总", prompt);

        // 解析结果
        result = JsonUtil.cleanJsonString(result);
        try {
            return objectMapper.readValue(result, SummaryResult.class);
        } catch (Exception e) {
            throw new RuntimeException("解析SummaryResult失败，原始结果: " + result, e);
        }
    }

    /**
     * 第一步：筛选相关网页
     */
    private static List<SearchItem> selectRelevantWebpages(
            AgentParam para,
            String query, List<SearchItem> items, SummaryResult lastSummarize) throws Exception {

        // 只处理SUCCESS状态的items
        List<SearchItem> successItems = items.stream()
                .filter(item -> item.getStatus() == SearchItem.STATUS.SUCCESS)
                .collect(java.util.stream.Collectors.toList());

        if (successItems.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取最大内容长度
        int maxContentToken = getMaxContentToken(para, lastSummarize);

        // 构建网页列表信息
        String webpageList = buildWebpageList(successItems);

        // 构建筛选提示词
        String selectionPrompt = buildSelectionPrompt(query, webpageList, maxContentToken);

        // 调用大模型进行筛选
        String result = AgentUtil.callLlm(para.getContext(), para.getConfig().getString("model"), "网页内容筛选",
                selectionPrompt);

        // 解析筛选结果
        result = JsonUtil.cleanJsonString(result);
        Map<String, Object> selectionResult = objectMapper.readValue(result, Map.class);

        @SuppressWarnings("unchecked")
        List<Integer> selectedIndexes = (List<Integer>) selectionResult.get("selectedIndexes");

        if (selectedIndexes == null || selectedIndexes.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据选中的索引获取对应的SearchItem
        List<SearchItem> selectedItems = new ArrayList<>();
        for (Integer index : selectedIndexes) {
            if (index >= 0 && index < successItems.size()) {
                selectedItems.add(successItems.get(index));
            }
        }

        return selectedItems;
    }

    /**
     * 构建网页列表信息（Markdown格式）
     */
    private static String buildWebpageList(List<SearchItem> items) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            SearchItem item = items.get(i);
            if (item.getStatus() != SearchItem.STATUS.SUCCESS) {
                continue;
            }

            sb.append("### 网页 ").append(i).append("\n\n");
            sb.append("**标题:** ").append(item.getTitle()).append("\n\n");
            sb.append("**简介:** ").append(StringUtil.isEmpty(item.getSnippet()) ? "无" : item.getSnippet())
                    .append("\n\n");
            sb.append("**部分内容:** ")
                    .append(StringUtil.isEmpty(item.getContent()) ? "无" : item.getContent().substring(0, 1024))
                    .append("\n\n");
            sb.append("**Token数量:** ").append(item.getToken()).append("\n\n");
            // sb.append("**URL:** ").append(item.getUrl()).append("\n\n");
            sb.append("---\n\n");
        }

        return sb.toString();
    }

    /**
     * 构建筛选提示词
     */
    private static String buildSelectionPrompt(String query, String webpageList, int maxContentToken) {
        String prompt = Prompt.WEBPAGE_SELECTION;
        // prompt = prompt.replace("${query}", query);
        // prompt = prompt.replace("${webpageList}", webpageList);
        // prompt = prompt.replace("${maxContentToken}",
        // String.valueOf(maxContentToken));
        prompt = PromptUtil.parsePrompt(prompt, null, key -> {
            if ("query".equals(key)) {
                return query;
            } else if ("webpageList".equals(key)) {
                return webpageList;
            } else if ("maxContentToken".equals(key)) {
                return maxContentToken;
            }
            return null;
        });
        return prompt;
    }

    /**
     * 构建选中网页的内容
     */
    private static String buildSelectedContent(List<SearchItem> selectedItems) {
        StringBuilder contentBuilder = new StringBuilder();
        for (SearchItem item : selectedItems) {
            contentBuilder.append(buildItemContent(item));
        }
        return contentBuilder.toString();
    }

    private static int getMaxContentToken(
            AgentParam para,
            SummaryResult lastSummarize) throws Exception {
        // 得到最大token数量
        int maxTokens = AgentUtil.getModelMaxTokens(para.getContext(), para.getConfig().getString("model"),
                DEFAULT_CONTENT_TOKEN);
        // 去掉返回的SUMMARY_LENGTH,1024是一些其他的内容预留
        int lastSummarizeToken = AgentUtil
                .estimateTokenCount(lastSummarize == null ? null : lastSummarize.getSummarize());
        int contentLength = maxTokens - 2 * 1024 - SUMMARY_MAX_TOKEN - lastSummarizeToken;
        //
        if (contentLength <= 0) {
            return DEFAULT_CONTENT_TOKEN;
        } else {
            return contentLength;
        }

    }

    /**
     * 构建单个item的内容
     */
    private static String buildItemContent(SearchItem item) {
        StringBuilder sb = new StringBuilder();
        sb.append("## ").append(item.getTitle()).append("\n\n");
        sb.append("**URL:** ").append(item.getUrl()).append("\n\n");
        if (!StringUtil.isEmpty(item.getSnippet())) {
            sb.append("**摘要:** ").append(item.getSnippet()).append("\n\n");
        }
        sb.append("**内容:**\n").append(item.getContent()).append("\n\n");
        sb.append("---\n\n");
        return sb.toString();
    }

    /**
     * 构建提示词
     */
    private static String buildSummaryPrompt(String query, String content, SummaryResult lastSummarize) {
        String prompt = Prompt.REASONING_AND_SUMMARIZE;
        // prompt = prompt.replace("${query}", query);
        // prompt = prompt.replace("${lastReason}", lastSummarize == null ? "" :
        // lastSummarize.getReason());
        // prompt = prompt.replace("${content}", content);
        // // prompt = prompt.replace("${lastSummarize}",
        // StringUtil.isEmpty(lastSummarize)
        // // ? "" : lastSummarize);
        // prompt = prompt.replace("${now}", AgentUtil.now());
        // prompt = prompt.replace("${summaryTokens}",
        // String.valueOf(SUMMARY_MAX_TOKEN));
        //
        prompt = PromptUtil.parsePrompt(prompt, null, key -> {
            if ("query".equals(key)) {
                return query;
            } else if ("lastReason".equals(key)) {
                return lastSummarize == null ? "" : lastSummarize.getReason();
            } else if ("content".equals(key)) {
                return content;
            } else if ("lastSummarize".equals(key)) {
                return StringUtil.isEmpty(lastSummarize) ? "" : lastSummarize;
            } else if ("summaryTokens".equals(key)) {
                return SUMMARY_MAX_TOKEN;
            }
            return null;
        });
        //
        return prompt;
    }
}
