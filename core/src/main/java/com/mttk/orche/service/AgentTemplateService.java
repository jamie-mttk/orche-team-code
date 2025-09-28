package com.mttk.orche.service;

import com.mttk.orche.addon.agent.Agent;
import com.mttk.orche.core.PersistService;

public interface AgentTemplateService extends PersistService {
    public Agent obtainAgent(String agentKey) throws Exception;
}
