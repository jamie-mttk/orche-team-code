package com.mttk.orche.util;

import org.slf4j.Logger;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 一些与bean处理相关的
 */
public class BeanUtil {
	/**
	 * Set bean property with no error
	 * 
	 * @param bean
	 * @param name
	 * @param value
	 * @param logger
	 */
	public static void setProperty(Object bean, String name, Object value, Logger logger) {
		try {
			setProperty(bean, name, value);
		} catch (Exception e) {
			if (logger != null) {
				logger.warn("Set property for bean [" + bean + "] of property [" + name + "] with value [" + value
						+ "] failed", e);
			}
		}
	}

	/**
	 * 设置bean的属性并试图匹配目标类型
	 * 
	 * @param bean
	 * @param name
	 * @param value
	 * @throws Exception
	 */
	public static void setProperty(Object bean, String name, Object value) throws Exception {
		// 使用标准Java实现，能够访问私有变量
		setPropertyInternal(bean, name, value);
		// PropertyDescriptor pd=BeanUtils.getPropertyDescriptor(bean.getClass(),
		// propertyName);
		// if (pd==null) {
		// return;
		// }
		//
		// Method method=pd.getWriteMethod();
		// if (method!=null) {
		// //System.out.println(propValue+"==>"+propValue.getClass());
		// //propValue=smartConvert(propValue,method.getParameterTypes()[0]);
		// propValue=ConformUtil.toType(propValue, method.getParameterTypes()[0]);
		// //System.out.println(propValue+"==>"+propValue.getClass());
		// //System.out.println(method.getName()+"==>"+method.getParameterTypes()[0]);
		// method.setAccessible(true);
		// method.invoke(bean,propValue);
		// }
	}

	/**
	 * Get bean property without error
	 * 
	 * @param bean
	 * @param name
	 * @param logger
	 * @return
	 */
	public static Object getProperty(Object bean, String name, Logger logger) {
		try {
			return getProperty(bean, name);
		} catch (Exception e) {
			if (logger != null) {
				logger.warn("Get property for bean [" + bean + "] of property [" + name + "] failed", e);
			}
			//
			return null;
		}
	}

	/**
	 * Get bean property
	 * 
	 * @param bean
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Object getProperty(Object bean, String name) throws Exception {
		return getPropertyInternal(bean, name);
		// //PropertyDescriptor pd=BeanUtils.getPropertyDescriptor(bean.getClass(),
		// prop);
		// PropertyDescriptor pd=new PropertyDescriptor(prop, bean.getClass()); ;
		// //
		// Method method=pd.getReadMethod();
		// if (method==null) {
		// return null;
		// }
		// //
		// return method.invoke(bean);

	}

	/**
	 * 使用标准Java实现设置Bean属性，支持访问私有字段
	 */
	private static void setPropertyInternal(Object bean, String name, Object value) throws Exception {
		Class<?> clazz = bean.getClass();

		// 首先尝试使用PropertyDescriptor（标准JavaBean方式）
		try {
			PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
			Method writeMethod = pd.getWriteMethod();
			if (writeMethod != null) {
				// 类型转换
				Object convertedValue = ConformUtil.toType(value, writeMethod.getParameterTypes()[0]);
				writeMethod.invoke(bean, convertedValue);
				return;
			}
		} catch (Exception e) {
			// PropertyDescriptor失败，尝试直接访问字段
		}

		// 尝试直接访问字段（包括私有字段）
		Field field = findField(clazz, name);
		if (field != null) {
			field.setAccessible(true);
			// 类型转换
			Object convertedValue = ConformUtil.toType(value, field.getType());
			field.set(bean, convertedValue);
			return;
		}

		throw new NoSuchFieldException("Property or field '" + name + "' not found in class " + clazz.getName());
	}

	/**
	 * 使用标准Java实现获取Bean属性，支持访问私有字段
	 */
	private static Object getPropertyInternal(Object bean, String name) throws Exception {
		Class<?> clazz = bean.getClass();

		// 首先尝试使用PropertyDescriptor（标准JavaBean方式）
		try {
			PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
			Method readMethod = pd.getReadMethod();
			if (readMethod != null) {
				return readMethod.invoke(bean);
			}
		} catch (Exception e) {
			// PropertyDescriptor失败，尝试直接访问字段
		}

		// 尝试直接访问字段（包括私有字段）
		Field field = findField(clazz, name);
		if (field != null) {
			field.setAccessible(true);
			return field.get(bean);
		}

		throw new NoSuchFieldException("Property or field '" + name + "' not found in class " + clazz.getName());
	}

	/**
	 * 递归查找字段，包括父类
	 */
	private static Field findField(Class<?> clazz, String name) {
		while (clazz != null) {
			try {
				return clazz.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		return null;
	}
}
