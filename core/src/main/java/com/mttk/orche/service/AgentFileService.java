package com.mttk.orche.service;

import java.util.List;

import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.core.Service;
import com.mttk.orche.service.support.AgentFile;

public interface AgentFileService extends Service {
    // 列出当前AgentContext中存储的文件
    public List<AgentFile> list(AgentContext context) throws Exception;

    public AgentFile get(AgentContext context, String fileName) throws Exception;

    // 添加一个文件到AgentContext中
    // 注意文件名可能会被移除特殊字符,所以返回的文件名可能与传入的文件名不同
    public AgentFile upload(AgentContext context, String fileName, String description, String content) throws Exception;

    public AgentFile upload(AgentContext context, String fileName, String description, byte[] data) throws Exception;

    // 根据文件名获取文件内容
    public String download(AgentContext context, String fileName) throws Exception;

    public byte[] downloadData(AgentContext context, String fileName) throws Exception;
}
