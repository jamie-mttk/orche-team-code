package com.mttk.orche.addon.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.Context;
import com.mttk.orche.addon.Event;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.core.impl.AdapterConfigImpl;
import com.mttk.orche.service.EventService;

/**
 * Context实现类
 *
 */
public abstract class ContextImpl implements Context {

	protected Logger logger = null;

	//
	public ContextImpl(Logger logger) {

		this.logger = logger;

	}

	@Override
	public Server getServer() {
		return ServerLocator.getServer();
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public AdapterConfig createAdapterConfig(Map<String, Object> map) {
		return new AdapterConfigImpl(map);
	}

	@Override
	public Event createEvent() {
		return new EventImpl();
	}

	@Override
	public void informEvent(Event event) {
		getServer().getService(EventService.class).inform(event);
	}
}
