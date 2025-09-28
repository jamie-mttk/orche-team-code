package com.mttk.orche.service;

import com.mttk.orche.addon.Event;
import com.mttk.orche.core.PersistService;

public interface EventService extends PersistService {
	/**
	 * 用于外部通知事件服务
	 * 
	 * @param event
	 */
	public void inform(Event event);
}
