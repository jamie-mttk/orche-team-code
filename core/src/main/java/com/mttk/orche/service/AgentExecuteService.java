package com.mttk.orche.service;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentEventPrinter;
import com.mttk.orche.core.PersistService;
import com.mttk.orche.service.support.AgentExecuteRequest;

public interface AgentExecuteService extends PersistService {

    // 代理文件临时上传位置
    public File getAgentUploadPath() throws Exception;

    public String execute(AgentExecuteRequest request, AgentEventPrinter printer) throws Exception;

    public CompletableFuture<String> executeAsync(AgentExecuteRequest request, AgentEventPrinter printer)
            throws Exception;

    public byte[] loadAgentFile(String sessionId, String fileName) throws Exception;

    public String reply(String sessionId, AgentEventPrinter printer) throws Exception;

    public boolean schedule(AgentExecuteRequest request, AdapterConfig scheduler) throws Exception;
}
