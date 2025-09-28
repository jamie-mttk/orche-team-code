package com.mttk.orche.addon;

/**
 * 此接口实现了一个key/value对的数据对象
 */
public interface Entity<V> extends EntityReadonly<V>{
	/**
	 * 清除所有内容
	 */
	public void clear();
	
	/**
	 * 把值设置到key里
	 * @param key
	 * @param value
	 */
	public void put(String key,Object value) ;
	
	/**
	 * 删除指定key。如果删除成功返回删除掉的值
	 * @param key
	 * @return
	 */
	public Object remove(String key) ;

}
