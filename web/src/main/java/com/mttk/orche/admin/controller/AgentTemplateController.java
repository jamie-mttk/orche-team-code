package com.mttk.orche.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.service.AgentTemplateService;

@RestController
@RequestMapping(value = "/agentTemplate")
public class AgentTemplateController extends PersistableControllerBase {
    @Override
    public Class getServiceClass() {
        return AgentTemplateService.class;
    }
}
