package com.mttk.orche.addon;

import java.rmi.ServerException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 此接口实现了一个key/value对的数据对象,只读，无法修改
 */
public interface EntityReadonly<V> {
	/**
	 * 检查是否包含指定的key
	 * @param key
	 * @return
	 */
	public boolean containsKey(String  key);
	/**
	 * 得到_id的值,如果是MongodbObjectId转换成字符串
	 * @return
	 */
	public  String getId() ;
	/**
	 * 返回指定key对应的值,如果指定key没有值返回null
	 * @param key
	 * @return
	 */
	public Object 	get(String key);
	/**
	 * 返回给定key的值,如果不存在报错
	 * @param key
	 * @return
	 */
	public Object 	getMandatory(String key);

	/**
	 * 返回给定的key的字符串,如果给定key不存在或返回值类型不是String,返回null
	 * @param key
	 * @return
	 */
	public String 	getString(String key);
	/**
	 * 得到指定key的字符串，如果给定key不存在或返回值类型不是String返回defValue
	 * @param key
	 * @param defValue
	 * @return
	 */
	public String 	getString(String key,String defValue);
	/**
	 * 得到指定key的字符串，如果给定key不存在或返回值类型不是String报错
	 * @param key
	 * @return
	 */
	public String 	getStringMandatory(String key);
	/**
	 * 得到指定key的字符串，如果给定key不存在或返回值类型不是Integer,返回null
	 * @param key
	 * @return
	 */
	public Integer 	getInteger(String key);
	/**
	 * 得到指定key的字符串，如果给定key不存在或返回值类型不是Integer,返回defValue
	 * @param key
	 * @param defValue
	 * @return
	 */
	public Integer 	getInteger(String key,Integer defValue);
	/**
	 * 得到指定key的字符串，如果给定key不存在或返回值类型不是Integer,报错
	 * @param key
	 * @return
	 */
	public Integer 	getIntegerMandatory(String key);
	/**
	 * 得到指定key的布尔型，如果给定key不存在或返回值类型不是Boolean,返回null
	 * @param key
	 * @return
	 */
	public Boolean 	getBoolean(String key);
	/**
	 * 得到指定key的布尔型，如果给定key不存在或返回值类型不是Boolean,返回defValue
	 * @param key
	 * @param defValue
	 * @return
	 */
	public Boolean 	getBoolean(String key,Boolean defValue);
	

	/**
	 * 把某个key里的值(Map<String,Object>类型)包装成泛型V,根据具体实现V可能是AdapterConfig或Pipeline
	 * @param key
	 * @return
	 */
	public  V getBean(String key);
	/**
	 * 把某个key里的值(Map<String,Object>类型的列表)包装成泛型List<V>,根据具体实现V可能是AdapterConfig或Pipeline
	 * @param key
	 * @return
	 */
	public List<V> getBeanList(String key);
	/**
	 * 返回指定key对应的值并转换成给定类型,如果指定key没有值返回null
	 * @param key
	 * @param retClass
	 * @return
	 */
	public <T> T get(String key,Class<T> retClass) ;
	/**
	 * 如果不存在指定key,返回defValue
	 * @param key
	 * @param retClass
	 * @param defValue
	 * @return
	 * @throws ServerException
	 */
	public <T> T get(String key,Class<T> retClass,T defValue) ;
	/**
	 * 如果不存在指定key报错
	 * @param key
	 * @param retClass
	 * @return
	 * @throws ServerException
	 */
	public <T> T getMandatory(String key,Class<T> retClass) ;
	/**
	 * 返回键列表
	 * @return
	 */
	public Set<java.lang.String> keySet();
	/**
	 * 把此对象转换成Map
	 * 注意对于Map的修改会影响到EntityReadonly
	 * @return
	 */
	public Map<String,Object> toMap();

}
