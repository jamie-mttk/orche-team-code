package com.mttk.orche.service;

import com.mttk.orche.addon.EventOperation;
import com.mttk.orche.addon.PluginService;

//定义了系统支持的报警方式
public interface EventOperationService extends PluginService<EventOperation> {
	// 根据事件处理的编号得到处理对象
	// EventOperation obtain(String key);
}
