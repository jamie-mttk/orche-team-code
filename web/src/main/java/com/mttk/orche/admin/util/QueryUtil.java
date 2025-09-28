package com.mttk.orche.admin.util;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mttk.orche.admin.util.controller.Pageable;
import com.mttk.orche.core.PersistSupport;

public class QueryUtil {
	//
	public static Document doQuery(PersistSupport persistSupport, Pageable pageable, Bson filter, Bson sort)
			throws Exception {
		if (pageable == null || pageable.getSize() <= 0) {
			// 不分�?
			List<Document> list = persistSupport.find(filter, 0, 0, sort);
			// return new Document().append("total", list.size()).append("page",
			// 1).append("size", 0).append("list", list);
			return new Document().append("list", list);
		} else {
			// 分页
			//
			List<Document> list = null;
			// 查找数量
			if (pageable.getTotal() <= 0) {
				pageable.setTotal((int) persistSupport.count(filter));
			}
			//
			if (pageable.getTotal() > 0) {
				list = persistSupport.find(filter, (pageable.getPage() - 1) * pageable.getSize(), pageable.getSize(),
						sort);
			}
			// test slow response
			// Thread.sleep(2*1000);
			//
			// System.out.println(new Document().append("total",
			// pageable.getTotal()).append("page", pageable.getPage())
			// .append("size", pageable.getSize()).append("list", list));
			return new Document().append("total", pageable.getTotal()).append("page", pageable.getPage())
					.append("size", pageable.getSize()).append("list", list);
		}
	}

	public static Bson filterCombine(Bson filter1, Bson filter2) {
		if (filter1 == null) {
			return filter2;
		} else {
			if (filter2 == null) {
				return filter1;
			} else {
				return Filters.and(filter1, filter2);
			}
		}
	}

}
