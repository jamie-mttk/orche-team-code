package com.mttk.orche.core.impl;

//代表一个可以被缓存的对象
public interface CacheWrap {
	public String getId();

	public void setId(String id);

	public String getKey();

	public void setKey(String key);

	// 对象被初始化后调用(如增加或启动时)
	public void start() throws Exception;

	// 当对象被关闭时(如对应的实体被删除或修改)调用
	public void stop() throws Exception;
}
