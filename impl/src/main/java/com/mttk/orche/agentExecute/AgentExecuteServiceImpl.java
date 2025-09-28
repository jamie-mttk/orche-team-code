package com.mttk.orche.agentExecute;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.AgentEventPrinter;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.agent.impl.AgentUtil;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractPersistService;
import com.mttk.orche.service.AgentExecuteLogService;
import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.service.SchedulerService;
import com.mttk.orche.service.support.AgentExecuteRequest;
import com.mttk.orche.service.support.AgentFile;
import com.mttk.orche.support.MongoUtil;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.FileHelper;
import com.mttk.orche.util.LangUtil;
import com.mttk.orche.util.StringUtil;
import com.mttk.orche.util.ThrowableUtil;

@ServiceFlag(key = "agentExecuteService", name = "智能体执行管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class AgentExecuteServiceImpl extends AbstractPersistService implements AgentExecuteService {
    //
    private static final Logger logger = LoggerFactory.getLogger(AgentExecuteServiceImpl.class);
    //
    // private ObjectMapper mapper = new ObjectMapper();

    @Override
    public File getAgentUploadPath() throws Exception {
        File path = new File(ServerUtil.getPathData(server) + File.separator + "chatTemp");
        FileHelper.createDir(path);
        return path;
    }

    @Override
    public String execute(AgentExecuteRequest request, AgentEventPrinter printer)
            throws Exception {
        //

        // boolean logTask = (boolean) bodyMap.getOrDefault("logTask", true);
        AgentExecutePersistent persistent = new AgentExecutePersistent(context, this);
        // 创建agentContext
        AgentContext agentContext = new AgentContextImpl(request.getSessionId(), persistent, printer);

        try {
            //
            persistent.kickoff(request, agentContext);

            // 发送启动消息
            agentContext.sendResponse("_flow-start", request.getSessionId(), "");
            // 处理request里面的文件问题 - 必须在flow-start后执行,
            List<AgentFile> agentFiles = AgentSupport.normalizeRequest(agentContext, request);
            // new ObjectMapper().writeValueAsString(Map.of("sessionId", sessionId)));
            // 执行
            String result = AgentSupport.execute(agentContext, request.getAgentId(), null, request.getRequest());
            // 发送完成汇总消息

            agentContext.sendResponse("_flow-end",
                    request.getSessionId(),
                    new ObjectMapper()
                            .writeValueAsString(Map.of("files", getAgentFilesToSend(agentContext, agentFiles))));
            //
            // logger.error("AgentExecuteServiceImpl.execute.error", "模拟出错");
            // agentContext.sendResponse("_flow-error",
            // sessionId, "模拟出错");

            // 发送完成标记
            LangUtil.suppressThrowable(() -> {
                printer.complete(null);
            });
            LangUtil.suppressThrowable(() -> {
                persistent.complete(null);
            });
            //
            return result;
        } catch (Throwable t) {
            // t.printStackTrace();
            agentContext.sendResponse("_flow-error", request.getSessionId(), ThrowableUtil.errorInfo(t));
            t.printStackTrace();
            LangUtil.suppressThrowable(() -> {
                printer.complete(t);
            });
            LangUtil.suppressThrowable(() -> {
                persistent.complete(t);
            });

            logger.error("AgentExecuteServiceImpl.execute.error", t);
            t.printStackTrace();
            return null;
            // throw t;
        }
    }

    @Override
    public CompletableFuture<String> executeAsync(AgentExecuteRequest request, AgentEventPrinter printer)
            throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(request, printer);
            } catch (Exception e) {
                // e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public byte[] loadAgentFile(String sessionId, String fileName) throws Exception {
        // 新建AgentContext
        AgentContext context = new AgentContextImpl(sessionId, null, null);

        // 得到文件内容
        return AgentUtil.getAgentFileService(context).downloadData(context, fileName);

    }

    @Override
    public String reply(String sessionId, AgentEventPrinter printer) throws Exception {
        // context.getLogger().info("Start REPLY ...");
        // 根据session id找到AgentExecute记录
        Document agentExecute = loadBySessionid(sessionId);
        //
        AgentExecuteLogService s = ServerUtil.getService(AgentExecuteLogService.class);
        // 先用最笨的办法得到所有数据...
        long l1 = System.currentTimeMillis();
        List<Document> list = s.find(Filters.eq("execute", MongoUtil.getId(agentExecute)), -1, -1,
                Sorts.ascending("logId"));
        long l2 = System.currentTimeMillis();
        // logger.info("find agent execute log by session:" + sessionId + " cost:" + (l2
        // - l1));
        //
        for (Document log : list) {
            ChatResonseMessage message = new ChatResonseMessage(log.getString("type"), log.getString("id"),
                    log.getString("data"), log.getLong("sendTime"));
            // Thread.sleep(10);
            printer.send(message);
        }
        //
        printer.complete(null);
        //
        long l3 = System.currentTimeMillis();
        logger.info("send agent execute log by session:" + sessionId + " cost:" + (l3 - l2));
        //
        return "";
    }

    @Override
    public boolean schedule(AgentExecuteRequest request, AdapterConfig scheduler) throws Exception {
        //
        Map<String, Object> paras = new HashMap<>();
        paras.put("agent", request.getAgentId());
        paras.put("taskName", request.getTaskName());
        paras.put("request", request.getRequest());
        paras.put("scheduler", scheduler.toMap());
        //
        SchedulerService s = server.getService(SchedulerService.class);
        // job使用UUID是爲了防止重複
        s.schedule(StringUtil.getUUID(), request.getTaskName(), "AGENT_EXECUTE", scheduler, AgentSchedulerRunner.class,
                paras);
        return true;
    }

    private List<AgentFile> getAgentFilesToSend(AgentContext agentContext, List<AgentFile> agentFiles)
            throws Exception {
        List<AgentFile> agentFilesAll = AgentUtil.getAgentFileService(agentContext).list(agentContext);
        List<AgentFile> agentFilesToSend = new ArrayList<>();
        if (agentFilesToSend != null) {
            // 从agentFilesAll拷贝所有文件，除了agentFiles里出现过的文件
            for (AgentFile file : agentFilesAll) {
                boolean exists = false;
                for (AgentFile existingFile : agentFiles) {
                    if (file.getFileName().equals(existingFile.getFileName())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    agentFilesToSend.add(file);
                }
            }
        }
        return agentFilesToSend;
    }

    private Document loadBySessionid(String sessionId) throws Exception {
        Optional<Document> o = this.load(Filters.eq("sessionId", sessionId));
        if (o.isEmpty()) {
            throw new RuntimeException("No agent execute is found by session:" + sessionId);
        }
        //
        return o.get();
    }
}
