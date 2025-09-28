package com.mttk.orche.service;

import java.util.List;

import com.mttk.orche.addon.agent.AgentContext;
import com.mttk.orche.addon.agent.ChatMessage;
import com.mttk.orche.core.Service;

public interface LlmExecuteService extends Service {
    public ChatMessage call(AgentContext context, String modelId, String name, List<ChatMessage> messages,
            List<String> functions)
            throws Exception;
}
