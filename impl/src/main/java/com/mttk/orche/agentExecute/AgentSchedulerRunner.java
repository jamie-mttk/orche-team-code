package com.mttk.orche.agentExecute;

import java.util.Map;

import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.service.support.SchedulerCallback;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.StringUtil;
import com.mttk.orche.service.support.AgentExecuteRequest;

public class AgentSchedulerRunner implements SchedulerCallback {
    @Override
    public void doInScheduler(Map<String, Object> paras) throws Exception {
        AgentExecuteRequest request = new AgentExecuteRequestImpl((String) paras.get("agent"), StringUtil.getUUID(),
                (String) paras.get("taskName"), (String) paras.get("request"), AgentExecuteRequest.MODE.SCHEDULER);

        AgentExecuteService s = ServerUtil.getService(AgentExecuteService.class);
        s.executeAsync(request, null);
    }
}
