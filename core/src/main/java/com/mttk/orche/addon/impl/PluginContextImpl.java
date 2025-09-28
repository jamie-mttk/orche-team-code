package com.mttk.orche.addon.impl;

import org.slf4j.Logger;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.PluginContext;
import com.mttk.orche.core.Server;

/**
 * PluginContext实现类
 *
 */
public class PluginContextImpl extends ContextImpl implements PluginContext {
	private AdapterConfig adapterConfig = null;

	public PluginContextImpl(Server server, Logger logger, AdapterConfig adapterConfig) {
		super(logger);
		//
		this.adapterConfig = adapterConfig;
	}

	@Override
	public AdapterConfig getConfig() {
		return adapterConfig;
	}

}
