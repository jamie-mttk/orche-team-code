package com.mttk.orche.agent.webSearch;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.agent.webSearch.query.WebQueryUtil;
import com.mttk.orche.agent.webSearch.summary.SummaryResult;
import com.mttk.orche.agent.webSearch.summary.WebSummary;
import com.mttk.orche.util.StringUtil;
import com.mttk.orche.agent.webSearch.download.WebDownload;
import java.util.*;

public class WebSearchUtil {
    // private static final Logger logger =
    // LoggerFactory.getLogger(DeepSearchUtil.class);

    public static SummaryResult handle(AgentParam para, String query)
            throws Exception {
        int maxLoop = para.getConfig().getInteger("maxLoop", 3);
        //
        int loopCount = 0;
        // 记录搜索过的查询关键字
        List<String> keyWordSearched = new ArrayList<>();
        // 记录已经搜索到的文档
        List<SearchItem> itemsSearched = new ArrayList<>();

        // 当前补充搜索地查询,一开始等于query;后来来自reasoning失败的结果
        SummaryResult lastSummaryResult = null;
        //
        while (loopCount++ < maxLoop) {
            //
            para.getContext().cancelCheck();
            // 得到希望查询的关键字列表
            List<String> keyWords = ThinkKeyWords.thinkKeyWords(para,
                    lastSummaryResult == null ? query : lastSummaryResult.getRewriteQuery(), keyWordSearched);
            //
            if (keyWords.size() == 0) {
                continue;
            }
            //
            para.getContext().cancelCheck();
            //
            List<SearchItem> items = new ArrayList<>(50);
            // 根据key words查找得到列表
            for (String keyWord : keyWords) {
                items.addAll(WebQueryUtil.query(keyWord, para.getConfig()));
            }
            //
            // 去重
            items = removeDuplicatesByLink(itemsSearched, items);
            // 获取网页内容
            WebDownload.processItems(items, para.getContext(), para.getConfig());
            // 把有内容的网页加入到最终列表
            itemsSearched.addAll(items);
            //
            para.getContext().cancelCheck();
            // 评估和汇总
            SummaryResult summaryResult = WebSummary.reasoningAndSummary(para, query, items,
                    lastSummaryResult);
            if (StringUtil.isEmpty(summaryResult)) {
                // 说明没有找到任何合适的内容,则不修改lastSummaryResult直接退出
                break;
            }
            //
            lastSummaryResult = summaryResult;
            if (lastSummaryResult != null && lastSummaryResult.getIsAnswer() == 1) {
                break;
            }

        }
        //
        return lastSummaryResult;
    }

    // 去重
    private static List<SearchItem> removeDuplicatesByLink(List<SearchItem> itemsSearched, List<SearchItem> items) {
        Set<String> seenLinks = new HashSet<>();
        List<SearchItem> uniqueItems = new ArrayList<>();
        // 把itemsSearched的所有url加入到seenLinks
        for (SearchItem item : itemsSearched) {
            seenLinks.add(item.getUrl());
        }
        for (SearchItem item : items) {
            if (!seenLinks.contains(item.getUrl())) {
                seenLinks.add(item.getUrl());
                uniqueItems.add(item);
            }
        }
        return uniqueItems;
    }

}
