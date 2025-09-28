package com.mttk.orche.admin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.admin.util.AuthUtil;
import com.mttk.orche.service.UserSettingService;

// @RestController
// @RequestMapping(value = "/userSetting")
public class UserSettingController extends PersistableControllerBase {
	@Override
	public Class getServiceClass() {
		return UserSettingService.class;
	}

	// 得到用户的info设置列表
	// @GetMapping("/auditInfoCustomize")
	// public Document auditInfoCustomize(HttpServletRequest request) throws
	// Exception {
	// Optional<Document> o = null;
	// try {
	// o = ServerLocator.getServer().getService(UserSettingService.class)
	// .load(Filters.and(Filters.eq("user", AuthUtil.getLoginUserId(request)),
	// Filters.eq("type", "auditInfoCustomize")));
	// if (o.isPresent()) {
	// return o.get();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// // 如果出错也使用缺省的
	// }
	// //
	// return defaultInfoCustomize();
	// }

	@Override
	protected Map<String, Object> parse2Map(String criteria, HttpServletRequest request) throws Exception {
		Map<String, Object> map = super.parse2Map(criteria, request);
		if (map == null) {
			map = new HashMap<>();
		}
		//
		map.put("user", AuthUtil.getLoginUserId(request));
		//
		return map;
	}

	// @PostMapping(value = "/save")
	@Override
	public Document save(@RequestBody Document doc, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		doc.put("user", AuthUtil.getLoginUserId(request));
		//
		// UserSettingService
		// service=ServerLocator.getServer().getService(UserSettingService.class) ;
		// String id = service.save(doc);
		// if (StringUtil.notEmpty(id)) {
		// return service.load(id).orElse(null);
		// } else {
		// return null;
		// }
		// System.out.println(doc.toJson());
		return super.save(doc, request, response);
	}

}