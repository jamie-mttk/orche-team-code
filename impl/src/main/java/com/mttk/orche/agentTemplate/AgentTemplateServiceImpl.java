package com.mttk.orche.agentTemplate;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.agent.Agent;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractCacheBeanService;
import com.mttk.orche.service.AgentTemplateService;

@ServiceFlag(key = "agentTemlatepService", name = "智能体模板管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class AgentTemplateServiceImpl extends AbstractCacheBeanService<Agent> implements AgentTemplateService {
    @Override
    public void doStart() throws Exception {
        getStrategy().setIdentifyField("key");
        //
        super.doStart();
    }

    @Override
    public Agent obtainAgent(String agentKey) throws Exception {
        return obtainBean(agentKey);
    }

    private ClassLoader obtainClassLoader(AdapterConfig config) {
        return server.obtainClassLoader(config.getString("_package_name"));
    }

    @Override
    public Agent createObject(AdapterConfig config) throws Exception {
        String key = config.getString("key");
        String implCalss = config.getString("implClass");
        //
        Object object = null;
        try {
            object = obtainClassLoader(config).loadClass(implCalss).getDeclaredConstructor().newInstance();
            // System.out.println("BUILD "+config.getString("name")+" by
            // cl:"+obtainClassLoader(config));
        } catch (Exception e) {
            throw new Exception(
                    "Instance agent of class [" + implCalss + "] failed for " + key, e);
        }
        if (object instanceof Agent) {
            return (Agent) object;
        } else {
            throw new Exception("Agent class [" + config.getString("implClass")
                    + "]  does not implement Agent interface:" + key);
        }

    }
}
