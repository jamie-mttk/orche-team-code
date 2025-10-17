package com.mttk.orche.agent.contextFile;

import java.util.List;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.impl.AbstractAgent;
import com.mttk.orche.addon.agent.impl.AgentRunnerSupport.AgentParam;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.service.AgentFileService;
import com.mttk.orche.service.support.AgentFile;

@AgentTemplateFlag(key = "_context-file-agent", name = "会话文件管理智能体", description = """
    管理会话文件,提供以下操作:
    1. upload(上传): 保存文件到会话环境, 返回实际保存的文件名
    2. download(下载): 读取指定文件的内容, 返回文件的完整内容
    3. list(列表): 查询所有会话文件, 返回文件名列表(包含描述信息)
    """, callDefineClass = ContextFileCallDefine.class, i18n = "/com/mttk/api/addon/basic/i18n")
public class ContextFileAgent extends AbstractAgent {

  @Override
  protected String executeInternal(AgentParam para, String transactionId) throws Exception {
    AdapterConfig request = para.getRequest();

    // 获取操作类型
    String operation = request.getString("operation");
    if (operation == null || operation.isEmpty()) {
      throw new IllegalArgumentException("操作类型不能为空");
    }

    // 根据operation分别调用不同的方法
    switch (operation) {
      case "upload":
        return handleUpload(para, request);
      case "download":
        return handleDownload(para, request);
      case "list":
        return handleList(para);
      default:
        throw new IllegalArgumentException("不支持的操作类型: " + operation);
    }
  }

  /**
   * 处理上传文件操作
   * 
   * @param para    代理参数
   * @param request 请求配置
   * @return 实际保存的文件名(可能已去除非法字符)
   * @throws Exception 上传失败时抛出异常
   */
  private String handleUpload(AgentParam para, AdapterConfig request) throws Exception {
    AgentFileService fileService = para.getContext().getServer().getService(AgentFileService.class);

    // 获取参数
    String content = request.getString("content");
    String description = request.getString("description");
    String filename = request.getString("filename");

    // 参数校验
    if (content == null || content.isEmpty()) {
      throw new IllegalArgumentException("上传文件缺少必需参数：content");
    }
    if (filename == null || filename.isEmpty()) {
      throw new IllegalArgumentException("上传文件缺少必需参数：filename");
    }

    // 执行上传
    AgentFile uploadedFile = fileService.upload(para.getContext(), filename, description, content);
    return uploadedFile.getFileName();
  }

  /**
   * 处理下载文件操作
   * 
   * @param para    代理参数
   * @param request 请求配置
   * @return 文件的完整内容
   * @throws Exception 下载失败时抛出异常
   */
  private String handleDownload(AgentParam para, AdapterConfig request) throws Exception {
    AgentFileService fileService = para.getContext().getServer().getService(AgentFileService.class);

    // 获取参数
    String filename = request.getString("filename");

    // 参数校验
    if (filename == null || filename.isEmpty()) {
      throw new IllegalArgumentException("下载文件缺少必需参数：filename");
    }

    // 执行下载
    String fileContent = fileService.download(para.getContext(), filename);
    return fileContent;
  }

  /**
   * 处理列出文件列表操作
   * 
   * @param para 代理参数
   * @return 格式化的文件列表(每行一个文件,包含文件名和描述信息)
   * @throws Exception 列表获取失败时抛出异常
   */
  private String handleList(AgentParam para) throws Exception {
    AgentFileService fileService = para.getContext().getServer().getService(AgentFileService.class);

    // 获取文件列表
    List<AgentFile> fileList = fileService.list(para.getContext());

    // 格式化输出
    StringBuilder result = new StringBuilder();
    // result.append("会话文件列表:\n");
    if (fileList.isEmpty()) {
      result.append("(无文件)");
    } else {
      for (AgentFile file : fileList) {
        result.append("- ").append(file.getFileName());
        if (file.getDescription() != null && !file.getDescription().isEmpty()) {
          result.append(" (").append(file.getDescription()).append(")");
        }
        result.append("\n");
      }
    }
    return result.toString();
  }

}
