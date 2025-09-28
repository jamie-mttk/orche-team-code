package com.mttk.orche.core;

import org.bson.Document;
/**
 * 数据持久化时抛出的事件监听接口
 *
 */
public interface PersistListener {
	/**
	 * 在插入前调用
	 * 
	 * @param document
	 */
	 public default void  preInsert(Document document)  throws Exception{}
	/**
	 * 插入后调用
	 * @param document
	 */
	  public default void  postInsert(Document document) throws Exception{}
	/**
	 * 更新前调用
	 * @param originalDocument	原始文档
	 * @param document		用户调用update和replace时这是传入的原始文档
	 * @param replace			true代表是replace，false是update
	 */
	  public default void  preUpdate(Document originalDocument,Document document,boolean replace) throws Exception{}
	/**
	 * 更新后调用
	 * @param originalDocument	原始文档
	 * @param document		用户调用update和replace时这是传入的原始文档
	 * @param replace			true代表是replace，false是update
	 */
	  public default void  postUpdate(Document originalDocument,Document document,boolean replace) throws Exception{}
	/**
	 * 删除前调用
	 * @param document
	 */
	  public default void  preDelete(Document document) throws Exception{}
	/**
	 * 删除后调用
	 * @param document
	 */
	  public default void  postDelete(Document document) throws Exception {}
}
