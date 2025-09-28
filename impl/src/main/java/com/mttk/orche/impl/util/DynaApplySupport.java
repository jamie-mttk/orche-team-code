package com.mttk.orche.impl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.Context;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.addon.annotation.ui.Table;
import com.mttk.orche.core.impl.AdapterConfigImpl;
import com.mttk.orche.util.BeanUtil;
import com.mttk.orche.util.ConformUtil;
import com.mttk.orche.util.StringUtil;

//根据ui的annotation设置bean或参数的值
public class DynaApplySupport {
	/**
	 * 把输入的map(往往是config)转换成一个bean,支持Control/Table annotation,以及ResourceFlag的子字段
	 * 
	 * @param context   用于有resourceFlag时回调计算属性
	 * @param beanClass Bean class
	 * @param data      可以来自config,已经完成了计算属性
	 * @return 返回生成的bean
	 * @throws Exception
	 */
	private static <T> T buildBean(Context context, Class<T> beanClass, Map<String, Object> data)
			throws Exception {

		T bean = beanClass.newInstance();
		// 得到所有字段逐个处理
		Field[] fields = getAllFields(beanClass);
		//
		for (Field field : fields) {
			Object value = null;
			if (field.isAnnotationPresent(Control.class)) {
				value = evalControl(context, data, field.getName(), field.getType());
				Control control = field.getAnnotation(Control.class);
				if (StringUtil.isEmpty(value)) {
					if (StringUtil.notEmpty(control.defaultVal())) {
						value = ConformUtil.toType(control.defaultVal(), field.getType());
					} else if (control.mandatory()) {
						throw new RuntimeException("Field " + field + " is mandatory");
					}
				}
			} else if (field.isAnnotationPresent(Table.class)) {
				// 这是一个表格
				value = evalTable(data, field.getName(), field.getGenericType());
			}
			// else
			// if(field.isAnnotationPresent(Bean.class)||field.getType().isAnnotationPresent(Bean.class))
			// {
			// //这是一个bean
			// value=buildBean(callback,field.getType(),data);
			// }
			//
			if (value != null) {
				BeanUtil.setProperty(bean, field.getName(), value);
			}
		}

		//
		return bean;
	}

	/**
	 * 智能计算parameter的值
	 * 
	 * @param context 上下文
	 * @param para    参数
	 * @param data    adapterConfig数据
	 * @param key     参数名(不直接从para获取是因为para中参数可能是错误的arg0,arg1)
	 * @return
	 * @throws Exception
	 */
	public static Object evalParaValue(Context context, Parameter para, Map<String, Object> data, String key)
			throws Exception {
		if (para.isAnnotationPresent(Control.class)) {
			// 试图计算-如果返回值是resource尝试load resource bean
			Object value = evalControl(context, data, key, para.getType());
			//
			Control control = para.getAnnotation(Control.class);

			if (StringUtil.isEmpty(value)) {
				if (StringUtil.notEmpty(control.defaultVal())) {
					value = ConformUtil.toType(control.defaultVal(), para.getType());
				} else if (control.mandatory()) {
					throw new RuntimeException("Paremeter " + key + " is mandatory");
				}
			}
			// removed by jamie@2020/2/5 - it is handled inside evalControl
			// Added by Jamie@2019/6/27
			// 如果参数是Resource则试图转换成resouce
			// if (para.getType().isAnnotationPresent(ResourceFlag.class) &&
			// StringUtil.notEmpty(value)
			// &&value instanceof String) {
			// AdapterConfig resourceConfig=context.loadResource((String)value);
			//
			// return buildBean(context, para.getType(), resourceConfig.toMap());
			// //return context.buildBean(para.getType(), resourceConfig);
			// }
			//
			return value;
		} else if (para.isAnnotationPresent(Table.class)) {
			// 这是一个表格
			return evalTable(data, key, para.getParameterizedType());
			// } else
			// if(para.isAnnotationPresent(Bean.class)||para.getType().isAnnotationPresent(Bean.class))
			// {
			// //这是一个bean
			// return buildBean(callback,para.getType(),data);
		} else {
			// 没有,则直接使用原始值
			return ConformUtil.toType(data.get(key), para.getType());
		}

	}

	/**
	 * 计算table标记的值(可能是FIELD或PARAMETER
	 * 
	 * @param data 用于填充的数据
	 * @param key  FIELD或PARAMETER的key,对应数据的key以及field/parameter名称
	 * @param type 类型，FIELD调用getGenericType()得到,PARAMETER通过getParameterizedType()得到
	 * @return
	 * @throws Exception
	 */
	private static Object evalTable(Map<String, Object> data, String key, Type type)
			throws Exception {
		Object value = data.get(key);
		if (value == null) {
			return null;
		}
		// 如果不是列表也不处理
		if (!(value instanceof List)) {
			return null;
		}
		// 这里没有判断,强行转换
		List<Map<String, Object>> valueList = (List<Map<String, Object>>) value;
		//
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			if (pt.getRawType().equals(List.class)) {
				Class clazz = (Class) pt.getActualTypeArguments()[0];
				if (clazz.isAssignableFrom(AdapterConfig.class)) {
					// 是AdapterConfigList
					List<AdapterConfig> configList = new ArrayList<>(valueList.size());
					valueList.forEach((v) -> {
						configList.add(new AdapterConfigImpl(v));
					});
					// BeanUtil.setProperty(bean, key, configList);
					return configList;
				}
			}
		}
		//
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object evalControl(Context context, Map<String, Object> data, String key, Class clazz)
			throws Exception {
		Object value = data.get(key);
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		// 检查参数是否是引用类型
		return ConformUtil.toType(value, clazz);

	}

	// 得到类的所有属性以及父级属性
	public static Field[] getAllFields(@SuppressWarnings("rawtypes") Class clazz) {
		List<Field> list = new ArrayList<Field>(16);
		while (clazz != null && !clazz.equals(Object.class)) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				list.add(field);
			}
			//
			clazz = clazz.getSuperclass();
		}
		//
		return list.toArray(new Field[list.size()]);
	}

}
