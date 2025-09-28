package com.mttk.orche.agent.webSearch.download;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.agent.webSearch.SearchItem;
import com.mttk.orche.util.StringUtil;
import com.mttk.orche.util.ThrowableUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebDownload {
    // private static final Logger logger =
    // LoggerFactory.getLogger(OrganicProcessor.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void processItems(List<SearchItem> items, AgentContext context, AdapterConfig adapterConfig)
            throws Exception {

        // 使用固定大小的线程池（替代信号量）
        ExecutorService executor = Executors.newFixedThreadPool(50);

        try {
            String transactionId = StringUtil.getUUID();
            context.sendResponse(new ChatResonseMessage("_func-start", transactionId, "获取网页内容:" + items.size()));
            // logger.info("获取网页内容:" + items.size());
            for (SearchItem item : items) {
                executor.submit(() -> {
                    processItem(item, context, adapterConfig);
                });
            }
            // logger.info("完成获取网页内容 1:" + executor);
            // 等待所有任务完成
            executor.shutdown();
            // logger.info("完成获取网页内容 2:" + executor);
            executor.awaitTermination(5, TimeUnit.MINUTES);
            // logger.info("完成获取网页内容3:" + executor);
            //
            context.sendResponse(new ChatResonseMessage("_func-end", transactionId));
        } finally {

        }
    }

    // Mock处理逻辑
    private static void processItem(SearchItem item, AgentContext context, AdapterConfig adapterConfig) {
        // 获取内容
        String transactionId = StringUtil.getUUID();
        try {

            context.sendResponse(
                    new ChatResonseMessage("_url-extract-start", transactionId, objectMapper.writeValueAsString(item)));
            String content = WebContentExtractor.extractWebText(item.getUrl());
            content = TextSanitizer.sanitize(content);
            // content = reduceIfNeeded(query, content);
            item.setContent(content);

            // 检查内容大小是否在允许范围内
            int contentSize = (int) item.getSize();
            int ignoreMinSize = adapterConfig.getInteger("ignoreMinSize", 1024);
            int ignoreMaxSize = adapterConfig.getInteger("ignoreMaxSize", 1024 * 20);

            if (contentSize < ignoreMinSize || contentSize > ignoreMaxSize) {
                item.setStatus(SearchItem.STATUS.IGNORE);
            } else {
                item.setStatus(SearchItem.STATUS.SUCCESS);
            }

            context.sendResponse(
                    new ChatResonseMessage("_url-extract-end", transactionId, objectMapper.writeValueAsString(item)));
        } catch (Exception e) {
            item.setContent(ThrowableUtil.dumpInfo(e));
            item.setStatus(SearchItem.STATUS.FAIL);
            //
            String result = "";
            try {
                result = objectMapper.writeValueAsString(item);
            } catch (Exception _) {
                // Ignore
            }
            //
            context.sendResponse(
                    new ChatResonseMessage("_url-extract-error", transactionId, result));

        }
    }

    // public static String reduceIfNeeded(String query, String content) throws
    // Exception {
    // if (StringUtil.isEmpty(content)) {
    // return content;
    // }
    // if (content.length() <= 2048 * 2048) {
    // return content;
    // }

    // //
    // String prompt = Prompt.REDUCE;
    // prompt = prompt.replace("{query}", query);
    // prompt = prompt.replace("{content}", content);
    // prompt = prompt.replace("{length}", "2048");
    // String result = null;
    // try {
    // result = LLMHelper.call(prompt);
    // } catch (Exception _) {

    // }
    // // System.out.println("after:" + result.length());
    // // System.out.println(result);
    // //
    // return result;
    // }
}
