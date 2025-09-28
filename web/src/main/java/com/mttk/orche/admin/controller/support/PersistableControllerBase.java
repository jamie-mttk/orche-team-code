package com.mttk.orche.admin.controller.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mttk.orche.admin.util.LocaleUtil;
import com.mttk.orche.admin.util.QueryUtil;
import com.mttk.orche.admin.util.controller.Pageable;
import com.mttk.orche.core.PersistService;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.core.Service;
import com.mttk.orche.util.StringUtil;

/**
 * 针对PersistService的方法转换成Restful接口
 */
public abstract class PersistableControllerBase extends CriteriaSupport {

	/**
	 * 继承者实现此方法传入一个PersistService的实�?
	 * 
	 * @return
	 */
	public Class getServiceClass() {
		return null;
	}

	/**
	 * 得到服务,可覆�?
	 * 
	 * @return
	 */
	public PersistService findPersistService() {
		Service service = ServerLocator.getServer().getService(getServiceClass());
		if (service == null) {
			throw new RuntimeException("No service is found by class:" + getServiceClass());
		}
		if (service instanceof PersistService) {
			return (PersistService) service;
		} else {
			throw new RuntimeException(getServiceClass() + " is not an PersistService");
		}
	}

	/**
	 * 查询
	 * 
	 * @param criteria 查询,条件为JSON格式
	 * @param pageable 如果size<=0代表不分�?缺省值为不分�?;分页时如果total<=0会去查找数量,否则继续使用此total
	 * @return 如果部分也返回的total=返回列表的大�?page=1,size=0
	 * @throws Exception
	 */
	@GetMapping(value = "/query")
	public Document queryByCriteria(String criteria, Pageable pageable, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//
		Map<String, Object> map = parse2Map(criteria, request);

		Bson filter = parseCriteria(map, request);

		//
		Bson sort = parseSort(map);

		// System.out.println("query filter:"+filter);
		// System.out.println("query sort:"+sort);
		// if (pageable == null || pageable.getSize() <= 0) {
		// // 不分�?
		// List<Document> list = findPersistSupport().find(filter,0,0,sort);
		// //return new Document().append("total", list.size()).append("page",
		// 1).append("size", 0).append("list", list);
		// return new Document().append("list", list);
		// } else {
		// // 分页
		// //
		// List<Document> list = null;
		// // 查找数量
		// if (pageable.getTotal() <= 0) {
		// pageable.setTotal((int) findPersistSupport().count(filter));
		// }
		// //
		// if (pageable.getTotal() > 0) {
		// list = findPersistSupport().find(filter, (pageable.getPage() - 1) *
		// pageable.getSize(), pageable.getSize(),sort);
		// }
		// // test slow response
		// // Thread.sleep(2*1000);
		// //
		//// System.out.println(new Document().append("total",
		// pageable.getTotal()).append("page", pageable.getPage())
		//// .append("size", pageable.getSize()).append("list", list));
		// return new Document().append("total", pageable.getTotal()).append("page",
		// pageable.getPage())
		// .append("size", pageable.getSize()).append("list", list);
		// }
		return QueryUtil.doQuery(findPersistService(), pageable, filter, sort);
	}

	/**
	 * 统计数量
	 * 
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/count")
	public Document count(String criteria, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Bson filter = parseCriteria(parse2Map(criteria, request));
		return new Document().append("count", findPersistService().count(filter));
	}

	/**
	 * 根据id查找
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/{id}")
	public Document byId(@PathVariable("id") String id) throws Exception {
		return findPersistService().load(id).orElse(null);
	}

	/**
	 * 根据key查找
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/byKey/{key}")
	public Document byKey(@PathVariable("key") String key, HttpServletRequest request) throws Exception {
		// return findPersistService().load("key", key).orElse(null);
		// 支持i18n
		// System.out.println("@@@@@@"+LocaleUtil.handle(key,
		// request,findPersistService()));
		return LocaleUtil.handle(key, request, findPersistService());
	}

	// 调用save,返回保存后的内容
	@PostMapping("/save")
	public Document save(@RequestBody Document doc, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String id = findPersistService().save(doc);
		if (StringUtil.notEmpty(id)) {
			return findPersistService().load(id).orElse(null);
		} else {
			return null;
		}
	}

	// 调用insert
	@PostMapping("/insert")
	public Document insert(@RequestBody Document doc, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String id = findPersistService().insert(doc);
		if (StringUtil.notEmpty(id)) {
			return findPersistService().load(id).orElse(null);
		} else {
			return null;
		}
	}

	// 调用replace
	@PostMapping(value = "/replace")
	public Document replace(@RequestBody Document doc, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String id = findPersistService().replace(doc);
		if (StringUtil.notEmpty(id)) {
			return findPersistService().load(id).orElse(null);
		} else {
			return null;
		}
	}

	// 调用update
	@PostMapping(value = "/update")
	public Document update(@RequestBody Document doc, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String id = findPersistService().update(doc);
		if (StringUtil.notEmpty(id)) {
			return findPersistService().load(id).orElse(null);
		} else {
			return null;
		}
	}

	// 删除,成功后返回{"success":true}
	@PostMapping(value = "/delete")
	public Document delete(String id) throws Exception {
		boolean result = findPersistService().delete(id);
		//
		return new Document().append("success", result);
	}

	@DeleteMapping("/{id}")
	public Document delete2(@PathVariable("id") String id) throws Exception {
		return delete(id);
	}
}
