package com.mttk.orche.agent;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractCacheBeanService;
import com.mttk.orche.service.AgentService;

@ServiceFlag(key = "agentService", name = "智能体管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class AgentServiceImpl extends AbstractCacheBeanService<AdapterConfig> implements AgentService {

}
