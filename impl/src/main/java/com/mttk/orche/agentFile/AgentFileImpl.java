package com.mttk.orche.agentFile;

import org.bson.Document;
import com.mttk.orche.service.support.AgentFile;

/**
 * AgentFile接口的实现类
 * 代表一个存储在AgentContext中的文件
 */
public class AgentFileImpl implements AgentFile {

    private String fileName;
    private String description;
    private Long size;

    // 用于Jackson反序列化
    public AgentFileImpl() {

    }

    public AgentFileImpl(String fileName, String description, long size) {
        this.fileName = fileName;
        this.description = description;
        this.size = size;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public long getSize() {
        return size;
    }

    /**
     * 从MongoDB Document创建AgentFileImpl对象
     * 
     * @param document MongoDB Document对象
     * @return AgentFileImpl对象，如果document为null则返回null
     */
    public static AgentFileImpl fromDocument(Document document) {
        if (document == null) {
            return null;
        }

        String fileName = document.getString("fileName");
        String description = document.getString("description");
        Long size = document.getLong("size");

        // 处理size字段可能为null的情况
        long fileSize = (size != null) ? size : 0L;

        return new AgentFileImpl(fileName, description, fileSize);
    }
}
