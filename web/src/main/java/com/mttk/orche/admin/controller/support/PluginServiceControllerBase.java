package com.mttk.orche.admin.controller.support;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;

import com.mttk.orche.admin.util.LocaleUtil;
import com.mttk.orche.util.StringUtil;

public abstract class PluginServiceControllerBase extends PersistableControllerBase {

	// 辅助方法,得到所有的plugin
	@GetMapping("/plugins")
	public Document plugins(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String criteria = (new Document()).append("type", "plugin").toJson();
		Document doc = queryByCriteria(criteria, null, request, response);
		// i18n
		List<Document> list = (List<Document>) doc.get("list");
		if (list != null && list.size() > 0) {
			// 需要做i18n转换
			List<Document> listNew = new ArrayList<>(list.size());
			for (Document d : list) {
				listNew.add(LocaleUtil.handle(d, request));
			}
			doc.append("list", listNew);
		}
		//
		return doc;
	}

	// //根据plugin key得到plugin的信�?
	// @GetMapping("/plugin/{key}")
	// public Document plugin(String key) throws Exception {
	// String criteria=(new Document()).append("type","plugin").append("key",
	// key).toJson();
	// return this.queryByCriteria(criteria, null, null);
	// }
	// 辅助方法,得到所有的定义
	@GetMapping("/defines")
	public Document defines(String plugin, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Document doc = new Document().append("type", "define");
		if (StringUtil.notEmpty(plugin)) {
			doc.append("plugin", plugin);
		}
		String criteria = doc.toJson();
		return this.queryByCriteria(criteria, null, request, response);
	}
}
