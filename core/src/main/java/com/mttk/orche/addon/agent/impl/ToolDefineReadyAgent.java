package com.mttk.orche.addon.agent.impl;

import java.util.Arrays;

import java.util.List;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.Agent;

public abstract class ToolDefineReadyAgent implements Agent {

    @Override
    public List<String> getToolDefine(AdapterConfig agentConfig) throws Exception {
        return Arrays.asList(ToolDefineUtil.getToolDefine(agentConfig));
    }

}
