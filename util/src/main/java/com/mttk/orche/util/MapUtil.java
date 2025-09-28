package com.mttk.orche.util;

import java.util.Map;

/**
 * 提供了对于Map的一些高级获取方法,适用于针对Pipeline或配置的数据获取
 */
public class MapUtil {
	/**
	 * 返回给定的key的字符串,如果给定key类型不是String,返回null
	 * @param map
	 * @param key
	 * @return
	 */
	public static String getString(Map<String,Object> map,String key) {
		return get(map,key,String.class);
	}
	/**
	 * 返回给定的key的Map,如果给定key类型不是Map,返回null
	 * @param map
	 * @param key
	 * @return
	 */
	public static Map<String,Object> 	getMap(Map<String,Object> map,String key) {
		return get(map,key,Map.class);
	}
	/**
	 * 试图从map中得到_id,适用于从MongoDB Document中读取
	 * @param map
	 * @return
	 */
	public static String getId(Map<String,Object> map) {
		Object obj=map.get("_id");
		if (obj==null) {
			return null;
		}else {
			return obj.toString();
		}
	}
/**
 * 返回指定key对应的值并转换成给定类型,如果指定key没有值返回null
	如果返回值不符合给定类型，返回null
 * @param map
 * @param key
 * @param retClass
 * @return
 */
	public static <T> T get(Map<String,Object> map,String key,Class<T> retClass) {
		Object value=map.get(key);		
		if (value!=null && retClass.isInstance(value)) {
			return retClass.cast(value);
		}else {
			return null;
		}
	}
	/**
	 * 返回指定类型的值，如果不存在指定key或值为null,返回defValue
	 * @param map
	 * @param key
	 * @param retClass
	 * @param defValue
	 * @return
	 */
	public static <T> T get(Map<String,Object> map,String key,Class<T> retClass,T defValue) {
		Object value=map.get(key);		
		if (value!=null && retClass.isInstance(value)) {
			return retClass.cast(value);
		}else {
			return defValue;
		}
	}
	/**
	 * 返回指定值，如果为null报异常
	 * @param map
	 * @param key
	 * @param retClass
	 * @return
	 */
	public static <T> T getMandatory(Map<String,Object> map,String key,Class<T> retClass) {
		T value=get(map,key,retClass);
		if (value==null) {
			throw new RuntimeException("No value is found for key:"+key);
		}
		return value;
	}
	/**
	 * 根据缺省值确定返回值类型,如果不存在返回defValue
	 * @param map
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static<T> T 	get(Map<String,Object> map,String key,T defValue) {
		Object value=map.get(key);
		if (value!=null) {
			return (T)value;
		}else {
			return defValue;
		}
	}
}
