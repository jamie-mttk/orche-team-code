package com.mttk.orche.addon.impl;

import com.mttk.orche.addon.PluginContext;
import com.mttk.orche.addon.Pluginable;
import com.mttk.orche.core.impl.AbstractLifeCycle;
/**
 * Pluginable实现类
 *
 */
public abstract class PluginableImpl  extends AbstractLifeCycle implements Pluginable{
	protected PluginContext context=null;
	@Override
	public void setContext(PluginContext context) {
		this.context=context;
	}
	@Override	
	public PluginContext getContext() {
		return context;
	}
	
	
}
