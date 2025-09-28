package com.mttk.orche.core.impl;

import org.bson.conversions.Bson;

/**
 * 定义了CacheBean的规则
 */
public class CacheBeanStrategy {
	private String identifyField;
	private boolean autoStart = false;
	private String activeField;
	private boolean suppressCache = false;
	// 如果返回true,禁用cache list,调用iterator会抛出异常UnsupportedOperationException
	// 缺省值为true
	// private boolean suppressList=true;
	//
	private Bson filter = null;
	//
	// private boolean evalAuto = true;
	// private String[] evalIgnoreFields = null;

	/**
	 * 给出用于标识对象的字段名，如果使用_id标识接返回null; 缺省值为null
	 * 
	 * @return
	 */
	public String getIdentifyField() {
		return identifyField;
	}

	public CacheBeanStrategy setIdentifyField(String identifyField) {
		this.identifyField = identifyField;
		return this;
	}

	/**
	 * 说明Bean是否自动启动,true则在系统启动时和增加时自动启动;否则在需要时才启动
	 * 
	 * @return
	 */
	public boolean isAutoStart() {
		return autoStart;
	}

	public CacheBeanStrategy setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
		return this;
	}

	/**
	 * 激活标识字段,如果null说明没有激活字段,所有记录都时是激活的
	 * 
	 * @return
	 */
	public String getActiveField() {
		return activeField;
	}

	public CacheBeanStrategy setActiveField(String activeField) {
		this.activeField = activeField;
		return this;
	}

	/**
	 * 是否禁用缓存,如果禁用了调用obtainBean和obtainBeanList会抛出异常UnsupportedOperationException
	 * 缺省值为false
	 * 
	 * @return
	 */
	public boolean isSuppressCache() {
		return suppressCache;
	}

	public CacheBeanStrategy setSuppressCache(boolean suppressCache) {
		this.suppressCache = suppressCache;
		return this;
	}

	/**
	 * 用于在doStart中过滤查询列表-如EntryData中不同的类型的EntryData都在一个collection存放,需要分开
	 * 
	 * @return
	 */
	public Bson getFilter() {
		return filter;
	}

	public CacheBeanStrategy setFilter(Bson filter) {
		this.filter = filter;
		return this;
	}

	/**
	 * 调用create之前是否对读取的document的所有字段值计算脚本<br>
	 * 缺省值为true
	 * 
	 * @return
	 */
	// public boolean isEvalAuto() {
	// return evalAuto;
	// }

	// public void setEvalAuto(boolean evalAuto) {
	// this.evalAuto = evalAuto;
	// }

	// /**
	// * 字段值计算脚本时忽略的字段列表
	// *
	// * @return
	// */
	// public String[] getEvalIgnoreFields() {
	// return evalIgnoreFields;
	// }

	// public void setEvalIgnoreFields(String[] evalIgnoreFields) {
	// this.evalIgnoreFields = evalIgnoreFields;
	// }
}
