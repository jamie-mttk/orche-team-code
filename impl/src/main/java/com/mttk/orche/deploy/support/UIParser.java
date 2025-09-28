package com.mttk.orche.deploy.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mttk.orche.impl.util.DynaApplySupport;

public class UIParser {
	public static Document parseByClass(Class clazz, String label) throws Exception {
		// if (StringUtil.notEmpty(ui.template())) {
		// return parseByClassTemplate(clazz,label,ui.template());
		// }else if (StringUtil.notEmpty(ui.resource())) {
		// return parseByClassResource(clazz,label,ui.resource());
		// }else {
		return parseByClassBean(clazz, label);
		// }
	}

	public static Document parseByMethod(Class clazz, Method method, String label) throws Exception {
		// if (StringUtil.notEmpty(ui.template())) {
		// return parseByClassTemplate(clazz,label,ui.template());
		// }else if (StringUtil.notEmpty(ui.resource())) {
		// return parseByClassResource(clazz,label,ui.resource());
		// }else {
		return parseByMethodBean(clazz, method, label);
		// }
	}

	// private static Document parseByClassTemplate(Class clazz,String label,String
	// template) throws Exception{
	// Document ui=new Document();
	// ui.append("label", label).append("mode", "template").append("template",
	// loadTemplateContent(clazz,template));
	// return ui;
	// }
	// private static Document parseByClassResource(Class clazz,String label,String
	// resource) throws Exception{
	// String content= loadResourceContent(clazz,resource);
	// Document ui=Document.parse(content);
	// ui.append("mode", "config");
	// if (StringUtil.isEmpty(ui.getString("label"))) {
	// ui.put("label", label);
	// }
	// return ui;
	// }
	public static List<Object> gatherAllByClass(Class clazz) {
		// 得到所有的class级别annotation的列表
		// Annotation[] as=clazz.getAnnotations();
		List<Annotation> listAnnotation = new ArrayList<>(30);
		AnnotationUtil.fillAllAnnotations(listAnnotation, clazz);
		List<Object> list = new ArrayList<>(listAnnotation.size() * 2);
		//
		list.addAll(listAnnotation);

		// if("com.mttk.orche.addon.mapper.MapperAction".equals(clazz.getName())){
		// System.out.println("*******************");
		// System.out.println(listAnnotation);
		// System.out.println("~~~~~~~~~~~");
		// System.out.println(list);
		// }
		// for (int i=0;i<as.length;i++) {
		// list.add(as[i]);
		// }
		Field[] fields = DynaApplySupport.getAllFields(clazz);
		for (int i = 0; i < fields.length; i++) {
			list.add(new FieldWrap(clazz, fields[i]));
		}
		//
		// HOLDER_TYPE holderType=null;
		//
		// Annotation[] sysHolders=clazz.getAnnotationsByType(SysHolder.class);
		// for(int i=0;i<sysHolders.length;i++) {
		// //如果class没有sysHolder则加入一个缺省的
		// System.out.println("@@"+sysHolders[i]);
		// }
		// if(sysHolders.length==0) {
		// //如果class没有sysHolder则加入一个缺省的
		// list.add(new SysHolderWrap());
		// }
		// System.out.println("1=============================");
		// for(Object obj:list) {
		// System.out.println(obj);
		// }
		// System.out.println("1============================="); list.add(new
		// FieldWrap(clazz, fields[i]));
		//
		return list;

	}

	public static Document parseByClassBean(Class clazz, String label) throws Exception {
		List<Object> list = gatherAllByClass(clazz);
		return UISupport.genUI(list, label);
	}

	private static Document parseByMethodBean(Class clazz, Method method, String label) throws Exception {
		Annotation[] as = method.getAnnotations();
		//
		List<Object> list = new ArrayList<>(as.length * 2 + 1);
		for (Annotation a : as) {
			list.add(a);
		}
		//
		int i = 0;
		for (Parameter para : method.getParameters()) {
			//
			list.add(new ParaWrap(method, para, i++));
		}
		//
		return UISupport.genUI(list, label);
	}

	// //从template路径中加载内容
	// private static String loadTemplateContent(Class clazz,String template) throws
	// Exception{
	// try(InputStream is=clazz.getResourceAsStream(template)){
	// if (is==null) {
	// throw new RuntimeException("No template is found by :"+template);
	// }
	// return new String(IOUtil.toArray(is),"utf-8");
	// }
	// }
	// //从resource路径中加载内容
	// private static String loadResourceContent(Class clazz,String resource) throws
	// Exception{
	// if (".".equals(resource)) {
	// resource=clazz.getSimpleName()+".json";
	// }
	//
	// try(InputStream is=clazz.getResourceAsStream(resource)){
	// if (is==null) {
	// throw new RuntimeException("No resource is found by :"+resource);
	// }
	// return new String(IOUtil.toArray(is),"utf-8");
	// }
	// }

}
