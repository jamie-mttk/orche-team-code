package com.mttk.orche.agentTag;

import com.mttk.orche.core.impl.AbstractPersistService;
import com.mttk.orche.service.AgentTagService;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;

@ServiceFlag(key = "agentTagService", name = "智能体标签管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")

public class AgentTagServiceImpl extends AbstractPersistService implements AgentTagService {

}
