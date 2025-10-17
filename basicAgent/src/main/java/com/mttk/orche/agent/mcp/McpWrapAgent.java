package com.mttk.orche.agent.mcp;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.RequestTarget;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.addon.annotation.ui.Panel;
import com.mttk.orche.addon.annotation.ui.Table;
import com.mttk.orche.agent.mcp.util.McpClientUtil;
import com.mttk.orche.agent.mcp.util.McpWrapUtil;
import com.mttk.orche.agent.mcp.util.common.MCPClientBase;
import com.mttk.orche.core.impl.AdapterConfigImpl;
import com.mttk.orche.util.StringUtil;

@AgentTemplateFlag(key = "_mcp-wrap-agent", name = "MCP代理", description = "包装MCP服务成代理", i18n = "/com/mttk/api/addon/basic/i18n")

@Control(key = "mode", label = "传输方式", mode = "select", size = 1, defaultVal = "http", mandatory = true, props = "options:http:HTTP,stdio:标准输入输出")
@Control(key = "url", label = "URL地址", size = 1, mandatory = true, bindings = "show:this.data.mode=='http'")
@Control(key = "command", label = "命令行", size = 1, mandatory = true, bindings = "show:this.data.mode=='stdio'")
@Table(key = "args", label = "命令参数", props = {
        "operates:_add,_edit,_delete,_copy,_up,_down" }, bindings = "show:this.data.mode=='stdio'")
@Control(key = "arg", parent = "args", mandatory = true, label = "参数")
@Table(key = "envs", label = "环境变量", bindings = "show:this.data.mode=='stdio'")
@Control(key = "envKey", parent = "envs", mandatory = true, label = "变量名")
@Control(key = "envValue", parent = "envs", mandatory = true, label = "变量值")

@Table(key = "paras", label = "URL参数", bindings = "show:this.data.mode=='http'")
@Control(key = "paraKey", parent = "paras", mandatory = true, label = "参数名")
@Control(key = "paraValue", parent = "paras", mandatory = true, label = "参数值")

@Panel(key = "toolsListPanel", label = "")

@Control(key = "getTools", label = "获取MCP工具列表", parent = "toolsListPanel", mode = "button", size = 1, props = {
        "type:primary",
        "url:/util/forward", "target:agent._mcp-wrap-agent" })
@Table(key = "tools", label = "工具", parent = "toolsListPanel", props = { "operates:_edit,_delete",
        "cols:name,desciption", "showLabel:false" })
@Control(key = "name", label = "名称", mandatory = true, size = 1, parent = "tools", bindings = {
        "disabled:true" })
@Control(key = "desciption", label = "描述", description = "可以修为更加符合使用场景的描述", size = 1, parent = "tools", mode = "editor", props = {
        "language:markdown", "height:240" })
@Control(key = "inputSchema", label = "输入参数的Schema", description = "可以修改参数描述,注意禁止改变JSONJ格式", size = 1, parent = "tools", mode = "editor", props = {
        "language:markdown", "height:480" })

public class McpWrapAgent extends AbstractAgent implements RequestTarget {
    @Override
    public List<String> getToolDefine(AdapterConfig agentConfig) throws Exception {
        //
        //
        AdapterConfig config = agentConfig.getBean("config");
        try (MCPClientBase client = McpClientUtil.buildMcpClient(config)) {
            //
            return McpWrapUtil.getToolDefineInternal(client, agentConfig, false);
        }

    }

    @Override
    protected String executeInternal(AgentParam para, String transactionId) throws Exception {

        try (MCPClientBase client = McpClientUtil.buildMcpClient(para.getConfig())) {
            //
            List<String> tools = McpWrapUtil.getToolDefineInternal(client, para.getAgentConfig(), true);

            // 验证para.getToolName()必须是整数
            int toolIndex;
            String toolName = para.getToolName();
            try {
                toolIndex = Integer.parseInt(toolName);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("工具名称必须是整数，当前值: " + toolName);
            }

            // 验证toolIndex范围
            if (toolIndex < 0) {
                throw new IllegalArgumentException("工具索引不能小于0，当前值: " + toolName);
            }
            if (toolIndex >= tools.size()) {
                throw new IllegalArgumentException("工具索引超出范围，当前值: " + toolName + "，工具总数: " + tools.size());
            }
            //
            toolName = tools.get(toolIndex);

            //
            String result = client.callTool(toolName, para.getRequest().toMap());
            //
            if (StringUtil.notEmpty(result)) {
                // String fileName = para.getRequest().getString("OrcheFileName", toolName + "_"
                // + StringUtil.getUUID());
                // String fileDescription = para.getRequest().getString("OrcheFileDescription",
                // toolName);
                // para.getContext().getServer().getService(AgentFileService.class).upload(para.getContext(),
                // fileName,
                // fileDescription, result);
                // para.getContext().sendResponse(new ChatResonseMessage("_data-content",
                // transactionId, result));

                // return "##" + toolName + "调用结果\n\n# 保存文件名: " + fileName + " \n\n文件描述: " +
                // fileDescription + " \n\n##内容:\n\n"
                // + result;
                // para.getContext().sendResponse(new ChatResonseMessage("_data-content",
                // transactionId, result));
                return result;
            } else {
                // System.out.println("executeInternal 1: " + toolName);
                // System.out.println("executeInternal 2: " + result);
                //
                return toolName + " 调用成功,无返回结果";
            }
        }
    }

    @Override
    
    public Document handleRequest(Document in) throws Exception {
        //
        Document data = (Document) in.get("data");
        if (data == null) {
            throw new RuntimeException("No data is found in request");
        }
        //
        AdapterConfig agentConfig = new AdapterConfigImpl(data);
        try (MCPClientBase client = McpClientUtil.buildMcpClient(agentConfig)) {
            //
            List<JsonNode> tools = client.listTools();
            //
            List<Document> toolsList = new ArrayList<>();
            for (JsonNode tool : tools) {
                //
                Document toolData = new Document();
                toolData.put("name", tool.get("name").asText());
                toolData.put("desciption", tool.get("description").asText());
                if (tool.has("inputSchema")) {
                    toolData.put("inputSchema", tool.get("inputSchema").toString());
                }
                toolsList.add(toolData);
            }
            //
            Document doc = new Document();
            doc.put("tools", toolsList);
            //
            return doc;
        }
    }
}
