package com.mttk.orche.agent.webSearch;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;

import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.agent.webSearch.summary.SummaryResult;
import com.mttk.orche.service.support.AgentFile;

@AgentTemplateFlag(key = "_web-search", name = "互联网搜索", description = """
                互联网搜索工具,用于搜索互联网知识并汇总成markdown文件.仅支持单一查询任务,含义独立的查询需求请多次本调用工具.禁止用于其他运算或批量查询.
                """, i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "maxLoop", label = "最大循环次数", description = "当一次搜索汇总后评估不满足需求,则会再次启动查询.本数量定义了最大查询次数.", size = 1, defaultVal = "2", props = {
                "dataType:number" })
@Control(key = "keywordNum", label = "关键字数量", description = "根据查询请求生成的关键字数量.", size = 1, defaultVal = "5", props = {
                "dataType:number" })
@Control(key = "queryNum", label = "网页数量", description = "每个查询关键字返回的网页数量.", size = 1, defaultVal = "10", props = {
                "dataType:number" })
@Control(key = "ignoreMinSize", label = "忽略小的网页", description = "当获取网页内容小于此值时,则忽略不参与汇总.", size = 1, defaultVal = "1024", props = {
                "dataType:number" })
@Control(key = "ignoreMaxSize", label = "忽略大的网页", description = "当获取网页内容大于此值时,则忽略不参与汇总.", size = 1, defaultVal = "10240", props = {
                "dataType:number" })

@Control(key = "queryApiType", label = "API类型", mode = "select", size = 1, defaultVal = "serper", mandatory = true, props = "options:serper")
@Control(key = "apiKeySerper", label = "API Key", mandatory = true, size = 1, props = "type:password", bindings = "show:this.data.queryApiType=='serper'")

@Control(key = "model", label = "模型", mode = "select", size = 1, mandatory = true, props = { "url:llmModel/query" })
@Control(key = "returnFullContent", label = "返回完整结果", description = "选中后会在返回值中包含搜索结果内容,这回导致上下文增大,但是如果后续步骤需要用到搜索结果就需要选中", mode = "checkbox", size = 1, mandatory = false, defaultVal = "true")

public class WebSearchAgent extends AbstractAgent {

        @Override
        protected String executeInternal(AgentParam para, String transactionId) throws Exception {
                AgentContext context = para.getContext();
                // AdapterConfig config = para.getConfig();
                AdapterConfig request = para.getRequest();

                //
                String query = request.getString("query");

                //
                SummaryResult result = WebSearchUtil.handle(para, query);
                //
                if (result == null) {
                        return "没有搜索到任何内容,搜索失败";
                }
                //

                AgentFile agentFile = AgentUtil.getAgentFileService(context).upload(context, query + "_的搜索结果.md", query,
                                result.getSummarize());
                //
                StringBuilder sb = new StringBuilder(1024);

                sb.append("## 搜索主题:\n").append(query).append(" \n\n## 结果文件:\n")
                                .append(agentFile.getFileName() + "\n\n");
                if (result.getIsAnswer() == 1) {
                        sb.append("## 结果:\n").append("搜索结果完全满足查询需求\n");
                } else {
                        sb.append("## 结果:\n").append("搜索结果部分满足查询需求\n");
                        sb.append("## 需要更多信息:\n").append(result.getRewriteQuery()).append("\n");
                        sb.append("## 原因:\n").append(result.getReason()).append("\n");
                }
                if (para.getConfig().getBoolean("returnFullContent", true)) {
                        sb.append("## 完整内容:\n```markdown\n").append(result.getSummarize()).append("\n\n```\n");
                }

                //
                return sb.toString();
        }
}
