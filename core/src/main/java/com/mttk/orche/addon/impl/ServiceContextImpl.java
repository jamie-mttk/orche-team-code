package com.mttk.orche.addon.impl;

import org.slf4j.Logger;

import com.mttk.orche.addon.ServiceContext;
import com.mttk.orche.core.Server;

/**
 * ServiceContext实现类
 */
public class ServiceContextImpl extends ContextImpl implements ServiceContext {

	public ServiceContextImpl(Server server, Logger logger) {
		super(logger);
	}

}
