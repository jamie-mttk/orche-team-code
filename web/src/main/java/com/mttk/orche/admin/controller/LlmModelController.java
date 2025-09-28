package com.mttk.orche.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.service.LlmModelService;

@RestController
@RequestMapping(value = "/llmModel")
public class LlmModelController extends PersistableControllerBase {
    @Override
    public Class getServiceClass() {
        return LlmModelService.class;
    }
}
