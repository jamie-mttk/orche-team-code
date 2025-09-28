package com.mttk.orche.agent.sqlQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

import org.bson.Document;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.RequestTarget;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatMessage;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.addon.annotation.ui.Table;
import com.mttk.orche.core.impl.AdapterConfigImpl;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.util.ThrowableUtil;

@AgentTemplateFlag(key = "_sql-query", name = "数据库查询", description = """
                这是一个数据库搜索工具,可以传入希望获取的数据的用户指令,生成SQL语句查询后,生成CSV格式的文件.
                """, i18n = "/com/mttk/api/addon/basic/i18n")
@Control(key = "jdbcDriver", label = "JDBC驱动类", size = 2, mandatory = true)
@Control(key = "jdbcUrl", label = "JDBC URL", size = 2, mandatory = true)
@Control(key = "jdbcUser", label = "JDBC用户", size = 2, mandatory = true)
@Control(key = "jdbcPassword", label = "JDBC密码", size = 2, mandatory = true, props = "type:password")
@Control(key = "model", label = "模型", mode = "select", size = 1, mandatory = true, props = { "url:llmModel/query" })
@Control(key = "systemPrompt", label = "系统提示词", mandatory = true, size = 1, defaultVal = Prompt.SYSTEM_PROMPT, mode = "editor", props = {
                "language:markdown", "height:480" })
@Control(key = "tableFilter", label = "表过滤", description = "输入后点击按钮增加表得表列表\n支持使用schema.table的形式过滤\n支持使用*(代表多个字符)模糊匹配", size = 2)
@Control(key = "addTables", label = "增加表", mode = "button", size = -1, props = {
                "type:primary", "url:/util/forward", "target:agent._sql-query" })
@Control(key = "removeTables", label = "删除所有表", mode = "button", size = -1, props = {
                "type:primary", "url:/util/forward", "target:agent._sql-query" })
@Control(key = "genPrompt", label = "生成提示词", mode = "button", size = -1, props = {
                "type:primary", "url:/util/forward", "target:agent._sql-query" })
@Table(key = "tables", label = "表", props = { "operates:_delete",
                "cols:name,desciption", "showLabel:false" })
@Control(key = "name", label = "名称", parent = "tables")
@Control(key = "desciption", label = "描述", parent = "tables")

public class SqlQueryAgent extends AbstractAgent implements RequestTarget {
        @Override
        protected String executeInternal(AgentParam para, String transactionId) throws Exception {
                AgentContext context = para.getContext();
                AdapterConfig config = para.getConfig();
                AdapterConfig request = para.getRequest();

                //

                String prompt = config.getString("systemPrompt");
                //
                String query = request.getStringMandatory("query").toString();
                ChatMessage msg1 = ChatMessage.system(prompt);
                ChatMessage msg2 = ChatMessage.user(query);
                // LLMExecutor llmExecutor = new LLMExecutor(config.getString("model"));
                ChatMessage message = AgentUtil.callLlm(context, config.getString("model"), "生成SQL",
                                Arrays.asList(msg1, msg2),
                                null);
                //
                // getAgentContext().getMemory().addMessage(message);
                //
                SqlQueryUtil.QueryResult queryResult = SqlQueryUtil.executeQueryToCsv(message.getContent(), config);
                // 生成描述
                StringBuilder sb1 = new StringBuilder(1024);
                sb1.append("**查询请求**: ").append(query).append("\n");
                sb1.append("**表头**: ").append(queryResult.getHeader()).append("\n");
                sb1.append("**数据行数**: ").append(queryResult.getCount()).append("\n");
                String description = sb1.toString();
                //
                AgentFile agentFile = AgentUtil.getAgentFileService(context).upload(context, query + "的数据库查询结果.csv",
                                description,
                                queryResult.getCsv());

                // 生成返回内容
                StringBuilder sb = new StringBuilder(1024);
                //

                sb.append(String.format("针对 \"%s\" 的查询已经生成并保存到文件 \"%s\"\n", query, agentFile.getFileName()));
                sb.append(description);
                //
                // System.out.println("~~~~~~~~~~~~~~~~\n" + sb);
                return sb.toString();
        }

        @Override

        public Document handleRequest(Document in) throws Exception {

                //
                Document data = (Document) in.get("data");
                if (data == null) {
                        throw new RuntimeException("No data is found in request");
                }
                //
                AdapterConfig config = new AdapterConfigImpl(data);

                //
                String buttonKey = ((Document) in.get("config")).getString("key");
                try {
                        if ("addTables".equals(buttonKey)) {
                                return SqlQueryTablesUtil.getTables(config, data);
                        } else if ("removeTables".equals(buttonKey)) {
                                return new Document("tables", new ArrayList<>());
                        } else if ("genPrompt".equals(buttonKey)) {
                                return SqlQueryGenUtil.genPrompt(config, data);
                        } else {
                                throw new RuntimeException("Invalid config key is found in request:" + buttonKey);
                        }
                } catch (Exception e) {
                        return new Document("__error", ThrowableUtil.dumpInfo(e));
                }

        }

}
