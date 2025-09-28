package com.mttk.orche.addon.impl;

import com.mttk.orche.addon.Pluginable;
import com.mttk.orche.core.impl.AbstractCacheWrap;

/**
 * 用于 AbstractPluginService
 *
 * @param <P>
 */
public class PluginableWrap<P extends Pluginable> extends AbstractCacheWrap {
	private P pluginable;

	public PluginableWrap(P pluginable) {
		this.pluginable = pluginable;

	}

	public P getPluginable() {
		return pluginable;
	}

	@Override
	public void start() throws Exception {
		pluginable.start();
	}

	@Override
	public void stop() throws Exception {
		pluginable.stop();
	}
}
