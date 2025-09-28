package com.mttk.orche.agentExecute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;

import com.mttk.orche.addon.ServiceContext;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.service.AgentExecuteLogService;
import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.service.support.AgentExecuteRequest;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.support.BsonUtil;
import com.mttk.orche.support.MongoUtil;
import com.mttk.orche.util.ThrowableUtil;

public class AgentExecutePersistent {
    private AgentExecuteService service;
    private AgentExecuteLogService logService;
    private ServiceContext serviceContext;
    private AgentContext agentContext;

    //
    private String id = null;
    //
    private AtomicLong logId = new AtomicLong(0);

    public AgentExecutePersistent(ServiceContext serviceContext,
            AgentExecuteService service) {
        this.service = service;
        this.serviceContext = serviceContext;

        //
        this.logService = serviceContext.getServer().getService(AgentExecuteLogService.class);

    }

    public Document kickoff(AgentExecuteRequest request, AgentContext agentContext)
            throws Exception {

        //
        this.agentContext = agentContext;
        //
        Document document = new Document();
        document.append("agentId", request.getAgentId());
        document.append("sessionId", request.getSessionId());
        document.append("request", request.getRequest());
        document.append("mode",
                request.getMode() == null ? AgentExecuteRequest.MODE.MANUAL.name() : request.getMode().name());
        document.append("status", "running");
        document.append("files", new ArrayList<>());
        document.append("startTime", new Date());
        //
        document.append("name", request.getTaskName());
        //
        service.insert(document);
        //
        this.id = MongoUtil.getId(document);
        //
        return document;
    }

    public Document complete(Throwable t) throws Exception {

        Document document = service.load(id).orElseThrow(() -> new Exception("执行文档未找到,id:" + id));
        if (t == null) {
            document.append("status", "success");
        } else {
            document.append("status", "failed");
            document.append("error", ThrowableUtil.dump2String(t));
        }
        //
        document.append("finishTime", new Date());
        //
        service.update(document);

        //
        return document;
    }

    public void addLog(ChatResonseMessage msg) throws Exception {

        Document log = BsonUtil.convertObjectToDocument(msg);
        // 设置一个唯一的从小到大的logId
        long logId = this.logId.incrementAndGet();
        log.append("logId", logId);
        log.append("execute", id);
        //
        logService.insert(log);

    }
}
