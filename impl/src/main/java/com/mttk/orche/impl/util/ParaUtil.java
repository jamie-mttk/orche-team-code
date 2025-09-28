package com.mttk.orche.impl.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.addon.annotation.ui.Table;
import com.mttk.orche.util.StringUtil;

public class ParaUtil {
	/**
	 * 使用标准Java方法尽可能获取到方法参数的实际名字，而不是arg0,arg1
	 * 注意：需要编译时使用-parameters参数才能获取真实参数名
	 * 
	 * @param method 方法名
	 * @param para   Java标准参数
	 * @param i      参数在方法中的位置，从0开始
	 * @return 参数的实际名字
	 */
	public static String paraName(Method method, Parameter para, int i) {
		// 使用标准Java方法获取参数名称
		String[] paramNames = getParameterNames(method);
		String key = null;
		// Annotation优先级最高
		if (para.getAnnotation(Control.class) != null) {
			key = para.getAnnotation(Control.class).key();
		} else if (para.getAnnotation(Table.class) != null) {
			key = para.getAnnotation(Table.class).key();
		}
		// else if (para.getAnnotation(Bean.class)!=null) {
		// key=para.getAnnotation(Bean.class).key();
		// }
		if (StringUtil.notEmpty(key)) {
			return key;
		}
		// 通过标准Java方法获取的参数名称
		if (i < paramNames.length) {
			key = paramNames[i];
		}
		if (StringUtil.notEmpty(key)) {
			return key;
		}
		// 直接获取,可能是arg0,arg1之类的
		key = para.getName();
		//
		return key;
	}

	/**
	 * 使用标准Java方法获取方法参数的真实名称
	 * 注意：需要编译时使用-parameters参数才能获取真实参数名
	 * 
	 * @param method 方法对象
	 * @return 参数名称数组
	 */
	private static String[] getParameterNames(Method method) {
		Parameter[] parameters = method.getParameters();
		String[] paramNames = new String[parameters.length];

		// 尽可能返回参数名，不管是否是arg开头
		for (int i = 0; i < parameters.length; i++) {
			paramNames[i] = parameters[i].getName();
		}

		return paramNames;
	}
}
