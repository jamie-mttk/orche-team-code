package com.mttk.orche.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;


import com.mttk.orche.support.bson.Decimal128Converter;
import com.mttk.orche.support.bson.DoubleConverter;
import com.mttk.orche.support.bson.LongConverter;
import com.mttk.orche.util.StringUtil;

//处理BSON Document
public class BsonUtil {
	/**
	 * Document 转换为JSON字符串- 以后可能加入更多转换逻辑
	 * 
	 * @param doc
	 * @return
	 */
	public static String doc2json(Document doc) {
		if (doc == null) {
			return null;
		}
		JsonWriterSettings settings = buildWriterSettings();
		return doc.toJson(settings);
	}

	/**
	 * JSON到Document - 以后可能加入更多转换逻辑
	 * 
	 * @param json
	 * @return
	 */
	public static Document json2doc(String json) {
		if (StringUtil.isEmpty(json)) {
			return null;
		}
		return Document.parse(json);
	}

	//
	public static JsonWriterSettings buildWriterSettings() {
		return JsonWriterSettings.builder().indent(true)
				.doubleConverter(new DoubleConverter()).decimal128Converter(new Decimal128Converter())
				.objectIdConverter(new ObjectIdConverter()).int64Converter(new LongConverter())
				.dateTimeConverter(new DateTimeConverter())// 必须自己转换,否则系统自动转换成了UTC时间导致差8个小时
				.build();
	}

	//
	public static Document copy(Document doc, List<String> keysIgnore) {
		Document docNew = new Document();
		//
		for (String key : doc.keySet()) {
			if (keysIgnore != null && keysIgnore.contains(key)) {
				continue;
			}
			//
			docNew.append(key, doc.get(key));
		}
		//
		return docNew;
	}

	/**
	 * 将任意Object转换为Document
	 * 通过反射获取Object的getter方法，并将结果转换为Document
	 * 
	 * @param obj 要转换的Object对象
	 * @return 转换后的Document对象
	 */
	public static Document convertObjectToDocument(Object obj) {
		if (obj == null) {
			return new Document();
		}

		Document doc = new Document();
		Class<?> clazz = obj.getClass();

		// 获取所有public方法
		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			// 检查是否为getter方法
			if (isGetterMethod(method)) {
				try {
					// 获取属性名（去掉get前缀并首字母小写）
					String propertyName = getPropertyName(method.getName());

					// 调用getter方法获取值
					Object value = method.invoke(obj);

					// 处理Collection类型的返回值
					if (value instanceof Collection) {
						Collection<?> collection = (Collection<?>) value;
						List<Object> list = new ArrayList<>();
						for (Object item : collection) {
							// 如果集合中的元素也是复杂对象，递归转换
							if (isComplexObject(item)) {
								list.add(convertObjectToDocument(item));
							} else {
								list.add(item);
							}
						}
						doc.append(propertyName, list);
					} else if (isComplexObject(value)) {
						// 如果返回值是复杂对象，递归转换
						doc.append(propertyName, convertObjectToDocument(value));
					} else {
						// 基本类型直接添加
						doc.append(propertyName, value);
					}
				} catch (Exception e) {
					// 忽略无法调用的getter方法
					System.err.println("无法调用getter方法: " + method.getName() + ", 错误: " + e.getMessage());
				}
			}
		}

		return doc;
	}

	/**
	 * 判断方法是否为getter方法
	 * 
	 * @param method 方法对象
	 * @return true如果是getter方法
	 */
	private static boolean isGetterMethod(Method method) {
		String methodName = method.getName();
		return methodName.startsWith("get")
				&& methodName.length() > 3
				&& method.getParameterCount() == 0
				&& !methodName.equals("getClass");
	}

	/**
	 * 从getter方法名获取属性名
	 * 
	 * @param methodName getter方法名
	 * @return 属性名
	 */
	private static String getPropertyName(String methodName) {
		String propertyName = methodName.substring(3); // 去掉"get"
		return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
	}

	/**
	 * 判断对象是否为复杂对象（需要递归转换）
	 * 
	 * @param obj 对象
	 * @return true如果是复杂对象
	 */
	private static boolean isComplexObject(Object obj) {
		if (obj == null) {
			return false;
		}

		Class<?> clazz = obj.getClass();

		// 基本类型和包装类型
		if (clazz.isPrimitive() ||
				clazz == String.class ||
				clazz == Integer.class ||
				clazz == Long.class ||
				clazz == Double.class ||
				clazz == Float.class ||
				clazz == Boolean.class ||
				clazz == Character.class ||
				clazz == Byte.class ||
				clazz == Short.class) {
			return false;
		}

		// 日期类型
		if (obj instanceof java.util.Date || obj instanceof java.sql.Date) {
			return false;
		}

		// 其他类型视为复杂对象
		return true;
	}
}
