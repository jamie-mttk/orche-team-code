package com.mttk.orche.admin.controller.support;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mttk.orche.admin.util.CriteriaUtil;

import com.mttk.orche.util.StringUtil;

public class CriteriaSupport {
	// 排序使用的key
	public static final String SORT_BY = "sortBy";
	public static final String SORT_TYPE = "sortType";

	@Autowired
	private ObjectMapper objectMapper;

	protected Bson parseCriteria(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return parseCriteria(map);
	}

	/**
	 * 根据传入的criteria条件解析出mongodb查询条件 缺省使用eq，如果值时字符串包�?使用regex
	 * 
	 * @param map
	 * @return
	 */
	protected Bson parseCriteria(Map<String, Object> map) throws Exception {
		if (map == null || map.size() == 0) {
			return null;
		}
		// 缺省实现为key/value都映射称相等
		List<Bson> list = new ArrayList<Bson>(map.size());
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
				continue;
			}
			if (SORT_BY.equals(key) || SORT_TYPE.equals(key)) {
				continue;
			}
			//
			Bson filter = parseCriteriaSingle(key, value);
			if (filter != null) {
				list.add(filter);
			}
		}

		//
		//
		if (list.size() > 0) {
			return Filters.and(list);
		} else {
			return null;
		}
	}

	protected Bson parseSort(Map<String, Object> map) throws Exception {
		if (map == null || map.size() == 0) {
			return null;
		}
		// 缺省实现为key/value都映射称相等
		String sortBy = (String) map.get(SORT_BY);
		if (StringUtil.isEmpty(sortBy)) {
			return null;
		}
		String sortType = (String) map.get(SORT_TYPE);
		if ("desc".equalsIgnoreCase(sortType)) {
			return Sorts.descending(sortBy);
		} else {
			return Sorts.ascending(sortBy);
		}
	}

	// 解析一个条件，返回null就忽略此条件 - 实现可以覆盖此方法实现特殊效�?
	protected Bson parseCriteriaSingle(String key, Object value) throws Exception {

		if (value != null && value instanceof String) {
			String val = (String) value;
			// 去掉首尾空白
			val = val.trim();
			//
			if ("_id".equalsIgnoreCase(key)) {
				// _id需要转换成ObjectId
				return Filters.eq("_id", new ObjectId(val));
			}
			//
			return CriteriaUtil.parseFilter(key, val);
		}
		//
		return Filters.eq(key, value);
	}

	// 解析到Map
	protected Map<String, Object> parse2Map(String criteria, HttpServletRequest request) throws Exception {

		Map<String, Object> map = null;
		// 试图解析criteria
		if (!StringUtil.isEmpty(criteria)) {
			map = objectMapper.readValue(criteria, Map.class);
		}
		//
		if (request != null) {
			Enumeration<String> names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				if (name.startsWith("_")) {
					if (map == null) {
						map = new HashMap<>();
					}
					map.put(name.substring(1), request.getParameter(name));
				}
			}
		}
		if (map == null) {
			map = new HashMap<>();
		}
		return map;

	}
}
