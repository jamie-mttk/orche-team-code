package com.mttk.orche.admin.util;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mttk.orche.util.PatternUtil;
import com.mttk.orche.util.StringUtil;

public class SysThreadUtil {
	/**
	 * 根据下述条件获取线程堆栈列表
	 * 
	 * @param threadName  线程名可以是相等或regex匹配;null代表匹配
	 * @param threadGroup 线程组名匹配,可以是相等或regex匹配;null代表匹配所NONE代表匹配没有设置线程池的
	 * @return
	 */
	public static Map<Thread, StackTraceElement[]> findStackTraces(Long threadId,String threadName, String threadGroup) {
		Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();
		if (threadId==null && StringUtil.isEmpty(threadName) && StringUtil.isEmpty(threadGroup)) {
			return all;
		}
		Pattern patternName=compilePattern(threadName);
		Pattern patternGroup=compilePattern(threadGroup);
		//
		Map<Thread, StackTraceElement[]> filtered =new HashMap<>();
		for (Thread t:all.keySet()) {
			if (isMatched(t,threadId,patternName,patternGroup)) {
				filtered.put(t, all.get(t));
			}
		}
		return filtered;
	}
	//转换traces为Document(符合table要求
	public static Document convert(Map<Thread, StackTraceElement[]> traces) {
		Document doc=new Document();
		//
		List<Document> list = new ArrayList<>(traces.size());		
		//
		for (Thread t:traces.keySet()) {
			list.add(convertSingle(t,traces.get(t)));
		}
		//排序
		list=list.stream().sorted((d1,d2)->{
			//第一个group+":"+name
			String key1=d1.getString("group")+":"+d1.getString("name");
			//第二个的key
			String key2=d2.getString("group")+":"+d2.getString("name");
			//
		
			return key1.compareTo(key2);
			}).collect(Collectors.toList());

		doc.append("total", list.size()).append("page", 1).append("size", list.size());
		doc.append("list",list);
		//
		return doc;
	}

	//
	public static List<String> threadGroups(){
		Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();
		Set<String> threadGroups=new HashSet<>();
		ThreadGroup tg=null;
		for (Thread t:all.keySet()) {
			tg=t.getThreadGroup();
			if (tg==null) {
				continue;
			}
			if (StringUtil.notEmpty(tg.getName())) {
				threadGroups.add(tg.getName());
			}
		}
		//
//		List<String> list=Collections.emptyList();
//		list.addAll(threadGroups);
		return new ArrayList<>(threadGroups);
	}
	
	//导入所有线程到文件
	public static void exportThread(Document doc,OutputStream os) throws Exception {
		//Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();
		String threadId=doc.getString("threadId");
		Long id=null;
		if(StringUtil.notEmpty(threadId)) {
			id=Long.parseLong(threadId);
		}
		
		Map<Thread, StackTraceElement[]> traces=SysThreadUtil.findStackTraces(id,
				doc.getString("threadName"),doc.getString("threadGroup"));
		
		//
		List<Thread> list=traces.keySet().stream().sorted((t1,t2)->{
					//第一个group+":"+name 
					//getThreadGroup可能为空，这里排序不需要考虑				
					String key1=t1.getThreadGroup()+":"+t1.getName();
					//第二个的key
					String key2=t2.getThreadGroup()+":"+t2.getName();
					return key1.compareTo(key2);
					}).collect(Collectors.toList());

				
		StackTraceElement[] stes=null;
		for(Thread t:list) {
			os.write(("Thread ID:\t"+t.getId()+"\n").getBytes("utf-8"));
			os.write(t.toString().getBytes("utf-8"));
			os.write("\n".getBytes());
			stes=t.getStackTrace();
			for(StackTraceElement ste:stes) {
				os.write(ste.toString().getBytes("utf-8"));
				os.write("\n".getBytes());
			}
			//
			os.write("\n".getBytes());
			os.write("\n".getBytes());
		}
	}
	//*****************************
	//* 私有方法
	//*****************************
	//判断一条是否匹
	private static boolean isMatched(Thread thread,Long threadId,Pattern patternName,Pattern patternGroup) {
		if (patternName!=null && !patternName.matcher(thread.getName()).matches()) {
			return false;
		}
		//
		ThreadGroup tg=thread.getThreadGroup();
		if (tg==null) {
			//按照Thread.getThreadGroup()的定返回null代表thread已经stop
			return false;
		}
		if (patternGroup!=null && !patternGroup.matcher(tg.getName()).matches()) {
			return false;
		}
		//
		if(threadId!=null) {
			return thread.getId()==threadId;
		}
		//
		return true;
	}
	//编译字符串为Pattern
	private static Pattern compilePattern(String val) {
		if (StringUtil.notEmpty(val)) {
			if (val.indexOf('*') >= 0) {
				// 模糊查询
				return Pattern.compile(PatternUtil.wildCardTransform(val), Pattern.CASE_INSENSITIVE);
			} else {
				// 自动加入首位匹配
				val="^.*"+val+".*$";
				return Pattern.compile(val, Pattern.CASE_INSENSITIVE);
			}

		} else {
			return null;
		}
	}
	//转换一条线程记录到Document
	private static Document convertSingle(Thread t,StackTraceElement[] stes) {
		Document doc=new Document();
		//
		doc.append("id", t.getId());
		doc.append("name", t.getName());
		doc.append("priority", t.getPriority());
		doc.append("state", t.getState().name());
		doc.append("group", t.getThreadGroup()==null?null:t.getThreadGroup().getName());
		doc.append("trace", stes!=null &&stes.length>0?stes[0].toString():null);
		doc.append("traces",dumpSte(stes));
		//
		return doc;
	}
	private static List<Document> dumpSte(StackTraceElement[] stes) {
		List<Document> list=new ArrayList<>(stes.length);
		for (StackTraceElement ste:stes) {
			list.add(new Document()
					.append("lineNumber", ste.getLineNumber()>0?ste.getLineNumber():null)
					.append("methodName", ste.getMethodName())					
					.append("className", ste.getClassName()));
		}
		return list;
	}
}
