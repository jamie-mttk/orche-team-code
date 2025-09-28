package com.mttk.orche.admin.controller;

import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.SystemConfigService;

@RestController
@RequestMapping(value = "/systemConfig")
public class SystemConfigController extends PersistableControllerBase {
	@Override
	public Class getServiceClass() {
		return SystemConfigService.class;
	}

	@GetMapping(value = "/obtain")
	public Document obtain() throws Exception {
		//
		return ServerLocator.getServer().getService(SystemConfigService.class).obtain();
	}

}
