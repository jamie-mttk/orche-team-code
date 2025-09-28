package com.mttk.orche.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.service.AgentService;
import com.mttk.orche.service.LlmModelService;
import com.mttk.orche.support.ServerUtil;

@RestController
@RequestMapping(value = "/home")
public class HomeController {
    @RequestMapping(value = "/summary")
    public Map<String, Object> summary() throws Exception {
        Map<String, Object> result = new HashMap<>();
        // agent count
        result.put("agentCount", ServerUtil.getService(AgentService.class).count(null));

        // model count
        result.put("modelCount", ServerUtil.getService(LlmModelService.class).count(null));
        // task count
        result.put("taskCount", ServerUtil.getService(AgentExecuteService.class).count(null));
        return result;
    }
}
