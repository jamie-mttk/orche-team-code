package com.mttk.orche.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.model.Filters;
import com.mttk.orche.admin.util.LocaleUtil;
import com.mttk.orche.addon.PluginService;

import com.mttk.orche.core.ServerLocator;

@RestController
@RequestMapping(value = "/plugin")
public class PluginController {
	// 根据pluginService得到所有的plugin定义
	@GetMapping("/{pluginService}/plugins")
	public Document defines(@PathVariable("pluginService") String pluginService, HttpServletRequest request)
			throws Exception {
		//
		PluginService ps = (PluginService) ServerLocator.getServer().getService(pluginService);
		List<Document> list = ps.find(Filters.eq("type", "plugin"));
		// i18n
		if (list == null || list.size() == 0) {
			return new Document().append("list", new ArrayList<Document>());
		}
		// 需要做i18n转换
		List<Document> listNew = new ArrayList<>(list.size());
		for (Document d : list) {
			// System.out.println("BEFORE:"+d.toJson());
			listNew.add(LocaleUtil.handle(d, request));
			// System.out.println("AFTER:"+LocaleUtil.handle(d, request));
		}
		return new Document().append("list", listNew);
	}
}
