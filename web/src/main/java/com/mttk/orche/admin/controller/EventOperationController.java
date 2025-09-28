package com.mttk.orche.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.admin.util.LocaleUtil;
import com.mttk.orche.admin.util.controller.Pageable;
import com.mttk.orche.service.EventOperationService;

@RestController
@RequestMapping(value = "/eventOperation")
public class EventOperationController extends PersistableControllerBase {
	@Override
	public Class getServiceClass() {
		return EventOperationService.class;
	}

	@Override
	public Document queryByCriteria(String criteria, Pageable pageable, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Document doc = super.queryByCriteria(criteria, pageable, request, response);
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
}
