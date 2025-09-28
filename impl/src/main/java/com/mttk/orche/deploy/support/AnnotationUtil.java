package com.mttk.orche.deploy.support;

import java.lang.annotation.Annotation;
import java.util.List;

public class AnnotationUtil {
	//得到类以及所有父级类/接口的annotation列表,getAnnotations返回不正确
	public static void fillAllAnnotations(List<Annotation> list,Class<?> clazz){
		//得到此类的所有annotation
		Annotation[] ans=clazz.getDeclaredAnnotations();
		
//		for(Annotation an:ans) {		
//			list.add(an);
//		}
		//Changed @2025/7/18
		//在存在继承时,父类的annotation会放在前面,这样更加合理
		for(int i=0;i<ans.length;i++) {
			Annotation an=ans[i];
			list.add(i,an);
		}
		//得到此类父类的annotation
		Class<?> clazzSuper=clazz.getSuperclass();
		//System.out.println(clazz+"==>"+ans.length+"==>"+clazzSuper);
		if (clazzSuper !=null) {
			fillAllAnnotations(list,clazzSuper);
		}
		//得到此类的所有interfaces
		Class<?>[] interfaces=clazz.getInterfaces();
		for (Class<?> c:interfaces) {
			fillAllAnnotations(list,c);
		}
	}
	

}
