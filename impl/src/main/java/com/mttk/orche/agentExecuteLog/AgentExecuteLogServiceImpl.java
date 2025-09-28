package com.mttk.orche.agentExecuteLog;

import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.core.impl.AbstractPersistService;
import com.mttk.orche.service.AgentExecuteLogService;

@ServiceFlag(key = "agentExecuteLogService", name = "智能体执行日志管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class AgentExecuteLogServiceImpl extends AbstractPersistService implements AgentExecuteLogService {

}
