package com.mttk.orche.agentExecute;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.AgentEventPrinter;

import com.mttk.orche.addon.agent.ChatMemory;
import com.mttk.orche.addon.agent.ChatResonseMessage;
import com.mttk.orche.addon.impl.ContextImpl;
import com.mttk.orche.service.AgentExecuteCancelService;
import com.mttk.orche.util.StringUtil;

public class AgentContextImpl extends ContextImpl implements AgentContext {
    private String sessionId;

    private AgentEventPrinter printer;
    private AgentExecutePersistent persistent;
    private Stack<ChatMemory> chatMemoryStack = new Stack<>();
    //
    private static final Logger logger = LoggerFactory.getLogger(AgentContextImpl.class);

    public AgentContextImpl() {
        this(null, null, null);
    }

    public AgentContextImpl(String sessionId, AgentExecutePersistent persistent, AgentEventPrinter printer) {
        super(logger);
        if (StringUtil.isEmpty(sessionId)) {
            sessionId = StringUtil.getUUID();
        }
        //
        this.sessionId = sessionId;
        this.printer = printer;
        this.persistent = persistent;

    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    private StringBuilder sb = new StringBuilder(1024 * 1024);

    public String getDatas() {
        return sb.toString();
    }

    @Override
    public ChatResonseMessage sendResponse(ChatResonseMessage message) {
        try {
            persistent.addLog(message);
        } catch (Throwable t) {
            logger.error("Save chat response message to database error", t);
        }
        //
        if (printer != null) {
            try {
                printer.send(message);
            } catch (Throwable t) {
                logger.error("Send chat response message to client error", t);
            }
        }
        //
        return message;

    }

    @Override
    public String execute(String agentId, String toolName, String request) throws Exception {
        return AgentSupport.execute(this, agentId, toolName, request);
    }

    @Override
    public List<String> getToolDefine(String agentId) throws Exception {
        return AgentSupport.getToolDefine(this, agentId);
    }

    @Override
    public Stack<ChatMemory> createChatMemory() throws Exception {
        chatMemoryStack.push(new ChatMemoryImpl());
        return chatMemoryStack;
    }

    @Override
    public Stack<ChatMemory> getChatMemoryStack() throws Exception {
        return chatMemoryStack;
    }

    @Override
    public boolean isCancelRequested() throws Exception {
        return getServer().getService(AgentExecuteCancelService.class).isCancelRequested(sessionId);
    }
}
