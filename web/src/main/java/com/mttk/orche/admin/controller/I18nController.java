package com.mttk.orche.admin.controller;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.core.Server;
import com.mttk.orche.service.I18nService;


@RestController
@RequestMapping(value = "/i18n")
public class I18nController {
	@Autowired Server server;
	//返回是否支持多语种以及缺省多语种
	@GetMapping(value = "/info")
	public Document info() throws Exception {
		I18nService s=server.getService(I18nService.class);
		Document doc = new Document();
		//
		doc.append("support", s.isSupport());
		doc.append("defaultLang", s.defaultLang());
		//
		return doc;
	}

}
