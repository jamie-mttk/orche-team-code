package com.mttk.orche.admin.controller;

import org.bson.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.addon.RequestTarget;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.AgentTemplateService;
import com.mttk.orche.util.StringUtil;

@RestController
@RequestMapping(value = "/util")
public class UtilController {
    // 根据配置中的target转发请求
    @PostMapping(value = "/forward")
    public Document forward(@RequestBody Document in) throws Exception {
        // 找到真是的target
        RequestTarget requestTarget = foundTarget(in);
        //
        return requestTarget.handleRequest(in);
    }

    // 找到请求bean
    private RequestTarget foundTarget(Document in) throws Exception {
        Document config = (Document) in.get("config");
        if (config == null) {
            throw new RuntimeException("No config is found in request body, it is impossible to determine target.");
        }
        // 从配置中得到target
        Document props = (Document) config.get("props");
        if (props == null) {
            throw new RuntimeException("No props is found in config, it is impossible to determine target.");
        }
        //
        String target = props.getString("target");
        //
        if (StringUtil.isEmpty(target)) {
            throw new RuntimeException("No target props is found in config, it is impossible to forward.");
        }
        // 按照句号分隔符分开
        String[] temp = target.split("\\.");
        if (temp.length != 2) {
            throw new RuntimeException("Invalid target format:" + target);
        }
        //
        Server server = ServerLocator.getServer();
        Object bean = null;
        if ("service".equalsIgnoreCase(temp[0])) {
            bean = server.getService(temp[1]);
        } else if ("agent".equalsIgnoreCase(temp[0])) {
            bean = server.getService(AgentTemplateService.class).obtainAgent(temp[1]);
        } else {
            throw new RuntimeException("Unsuported target prefix:" + target);
        }
        //
        if (bean == null) {
            throw new RuntimeException("No bean is found by:" + target);
        }
        //
        if (bean instanceof RequestTarget) {
            return (RequestTarget) bean;
        } else {
            throw new RuntimeException("Bean should implements interface [" + RequestTarget.class + "]:" + target);
        }
    }
}
