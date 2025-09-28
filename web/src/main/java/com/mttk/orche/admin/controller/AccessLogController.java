package com.mttk.orche.admin.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.model.Filters;
import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.admin.util.controller.Pageable;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.AccessLogService;

@RestController
@RequestMapping(value = "/accessLog")
public class AccessLogController extends PersistableControllerBase {
	@Override
	public Class getServiceClass() {
		return AccessLogService.class;
	}

	@Override
	protected Bson parseCriteriaSingle(String key, Object value) throws Exception {
		if ("hist".equals(key)) {
			// 这是历史数据库标�?
			return null;
		} else if (key.equalsIgnoreCase("timeStart") || key.equalsIgnoreCase("timeEnd")) {
			//
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse((String) value);
			//
			if ("timeStart".equals(key)) {
				return Filters.gte("_insertTime", date);
			} else {
				return Filters.lte("_insertTime", date);
			}

		} else {
			return super.parseCriteriaSingle(key, value);
		}
	}

	@Override
	public Document queryByCriteria(String criteria, Pageable pageable, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		AccessLogService accessLogService = ServerLocator.getServer().getService(AccessLogService.class);
		Map<String, Object> map = parse2Map(criteria, request);
		Bson filter = parseCriteria(map);
		Bson sort = parseSort(map);

		String histName = (String) map.get("hist");
		// System.out.println("HIST histName:" + histName);
		// System.out.println("HIST query filter:" + filter);
		// System.out.println("HIST query sort:" + sort);
		if (pageable == null || pageable.getSize() <= 0) {
			// 不分�?
			List<Document> list = accessLogService.find(histName, filter, 0, 0, sort);
			return new Document().append("total", list.size()).append("page", 1).append("size", 0).append("list", list);
		} else {
			// 分页
			//
			List<Document> list = null;
			// 查找数量
			if (pageable.getTotal() <= 0) {
				pageable.setTotal((int) accessLogService.count(histName, filter));
			}
			//
			if (pageable.getTotal() > 0) {
				list = accessLogService.find(histName, filter, (pageable.getPage() - 1) * pageable.getSize(),
						pageable.getSize(), sort);
			}
			// test slow response
			// Thread.sleep(2*1000);
			//
			// System.out.println("HIST:" + new Document().append("total",
			// pageable.getTotal())
			// .append("page", pageable.getPage()).append("size",
			// pageable.getSize()).append("list", list));
			return new Document().append("total", pageable.getTotal()).append("page", pageable.getPage())
					.append("size", pageable.getSize()).append("list", list);
		}
	}

	@GetMapping(value = "/histList")
	public Document histList() throws Exception {
		AccessLogService service = ServerLocator.getServer().getService(AccessLogService.class);

		return new Document().append("list", service.listHist());
	}

}
