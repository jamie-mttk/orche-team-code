package com.mttk.orche.admin.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.admin.controller.support.CriteriaSupport;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.LogService;
import com.mttk.orche.util.IOUtil;
import com.mttk.orche.util.StringUtil;

@RestController
@RequestMapping(value = "/log")
public class LogController extends CriteriaSupport{
//	@GetMapping(value = "/test")
//	public Document test(HttpServletRequest request) throws Exception {
//		Map<Thread, StackTraceElement[]>  map=Thread.getAllStackTraces();
//		System.out.println("##################"+map.size());
//		for (Thread t:map.keySet()) {
//			System.out.println("**************************\n"+t+"\n");
//			StackTraceElement ste[]=map.get(t);
//			for (int i=0;i<ste.length;i++) {
//				System.out.println(ste[i]);
//			}
//		}
//		//
//		return new Document();
//	}
	
	@GetMapping(value = "/query")
	public Document query(String criteria,HttpServletRequest request) throws Exception {
		Map<String, Object> map = parse2Map(criteria, request);
		String name=(String)map.get("name");
		//如果name没有给出通配符则首位添加**
		if (StringUtil.notEmpty(name) && name.indexOf('*') < 0) {
			name="*"+name+"*";
		}
		//
		List<LogService.LogFile> list=ServerLocator.getServer().getService(LogService.class).list(name);
		//根据size/page过滤list
		
		int size=getParaWithDefault(request,"size", 10);
		int page=getParaWithDefault(request,"page", 1);

		//得到指定分页区间的数�?
		List<LogService.LogFile> listResult=new ArrayList<>(size);
		//
		for(int i=0;i<size;i++) {
			int index=(page-1)*size+i;
			if(index>=list.size()) {
				break;
			}
			listResult.add(list.get(index));
		}
		//
		List<Document> listNew=listResult.stream().map(f->{
			return new Document().append("name", f.getName()).append("size", f.getSize()).append("lastModified",f.getLastModified());
			}).collect(Collectors.toList());
		
		return new Document().append("page",page).append("size",size).append("total",list.size()).append("list", listNew);
	}
	//获取参数,如果不存在返回defaultVal
	private int getParaWithDefault(HttpServletRequest request,String name,int defaultVal) {
		String value=request.getParameter(name);
		if (StringUtil.isEmpty(value)){
			return defaultVal;
		}else {
			return Integer.parseInt(value);
		}
	}
	@PostMapping("/download")
	public void download(@RequestBody Document doc,HttpServletResponse response) throws Exception {
		String name=doc.getString("name");
		try(InputStream is=ServerLocator.getServer().getService(LogService.class).load(name)){
			//
			String filename=name.replace('/','_');
			//
			response.setCharacterEncoding("Utf-8");
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			response.setHeader("Content-Type", "text/plain;charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			//
			try(OutputStream os=response.getOutputStream()){
					IOUtil.copy(is, os);
			}
		}
	}
}
