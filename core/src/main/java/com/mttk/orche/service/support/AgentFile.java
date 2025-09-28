package com.mttk.orche.service.support;

/**
 * 代表一个存储在AgentContext中的文件,不包含内容
 */
// @JsonDeserialize(as = AgentFileImpl.class)
public interface AgentFile {

    public String getFileName();

    public String getDescription();

    public long getSize();

}
