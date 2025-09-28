package com.mttk.orche.addon.impl;

import com.mttk.orche.core.impl.CacheWrap;

/**
 * 一个标准的监听实现,用于启动时针对每一条记录都在系统启动时启动
 * 实现必须实现createObject方法创建一个实现CacheWrap的对象
 *
 * @param <T>
 */
public class AbstractListenerService<T extends CacheWrap> extends AbstractEntryService<T> {
	// 缺省服务启动时初始化所有的
	@Override
	public void doStart() throws Exception {
		getStrategy().setAutoStart(true);
		//
		super.doStart();
	}

}
