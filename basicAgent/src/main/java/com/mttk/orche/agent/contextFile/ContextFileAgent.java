package com.mttk.orche.agent.contextFile;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.agent.impl.ToolDefineUtil;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.service.AgentFileService;
import com.mttk.orche.service.support.AgentFile;

// @AgentTemplateFlag(key = "_filt-tool-agent", name = "文件服务", description = "管理上下文文件,提供文件保存,读取和列表服务", i18n = "/com/mttk/api/addon/basic/i18n")

public class ContextFileAgent extends AbstractAgent {
    @Override
    public List<String> getToolDefine(AdapterConfig agentConfig) throws Exception {
        // 创建静态JSON字符串定义三个文件操作工具
        String toolsJson = """
                [
                  {
                    "name": "upload",
                    "description": "上传一个文件到上下文环境，返回实际的文件名（上传后可能会去掉文件名里的非法字符）",
                    "parameters": {
                      "type": "object",
                      "properties": {
                        "content": {
                          "type": "string",
                          "description": "文件内容"
                        },
                        "description": {
                          "type": "string",
                          "description": "文件描述"
                        },
                        "filename": {
                          "type": "string",
                          "description": "文件名"
                        }
                      },
                      "required": ["content", "filename"]
                    }
                  },
                  {
                    "name": "download",
                    "description": "从上下文下载一个文件，返回文件内容",
                    "parameters": {
                      "type": "object",
                      "properties": {
                        "filename": {
                          "type": "string",
                          "description": "文件名"
                        }
                      },
                      "required": ["filename"]
                    }
                  },
                  {
                    "name": "list",
                    "description": "查询文件的上下文列表，返回文件列表",
                    "parameters": {
                      "type": "object",
                      "properties": {},
                      "required": []
                    }
                  }
                ]
                """;

        // 解析JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode toolsArray = objectMapper.readTree(toolsJson);

        // 转换为List<String>
        List<String> toolDefines = new ArrayList<>();
        String agentId = agentConfig.get("_id").toString();

        for (int i = 0; i < toolsArray.size(); i++) {
            JsonNode tool = toolsArray.get(i);
            String originalName = tool.get("name").asText();
            String formattedName = ToolDefineUtil.buildToolName(originalName, agentId + "_" + originalName);

            // 创建新的ObjectNode，复制原tool的所有字段，但修改name字段
            ObjectNode modifiedTool = objectMapper.createObjectNode();
            tool.fieldNames().forEachRemaining(fieldName -> {
                if ("name".equals(fieldName)) {
                    modifiedTool.put(fieldName, formattedName);
                } else {
                    modifiedTool.set(fieldName, tool.get(fieldName));
                }
            });
            //
            // System.out.println("@@@@@@@@@@:" + modifiedTool.toString());
            // 将修改后的工具信息保存到List中
            toolDefines.add(modifiedTool.toString());
        }

        return toolDefines;
    }

    @Override
    protected String executeInternal(AgentParam para, String transactionId) throws Exception {
        String toolName = para.getToolName();
        AgentFileService fileService = para.getContext().getServer().getService(AgentFileService.class);

        // 根据toolName分别调用不同的方法
        switch (toolName) {
            case "upload":
                // 上传文件：参数 content, description, filename
                String content = para.getRequest().getString("content");
                String description = para.getRequest().getString("description");
                String filename = para.getRequest().getString("filename");

                if (content == null || filename == null) {
                    throw new IllegalArgumentException("上传文件缺少必需参数：content 和 filename");
                }

                AgentFile uploadedFile = fileService.upload(para.getContext(), filename, description, content);
                return uploadedFile.getFileName();

            case "download":
                // 下载文件：参数 filename
                String downloadFilename = para.getRequest().getString("filename");

                if (downloadFilename == null) {
                    throw new IllegalArgumentException("下载文件缺少必需参数：filename");
                }

                String fileContent = fileService.download(para.getContext(), downloadFilename);
                return fileContent;

            case "list":
                // 文件列表：无参数
                List<AgentFile> fileList = fileService.list(para.getContext());
                // 将文件列表转换为字符串返回
                StringBuilder result = new StringBuilder();
                for (AgentFile file : fileList) {
                    result.append(file.getFileName()).append("\n");
                }
                return result.toString();

            default:
                throw new IllegalArgumentException("不支持的工具名称: " + toolName);
        }
    }

}
