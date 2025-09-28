package com.mttk.orche.addon;

import com.mttk.orche.core.LifeCycle;

/**
 * 用户编写plugin的基础接口<br>
 * 不建议用户直接实现此接口，而是实现合适的虚拟类
 * 
 *
 */
public interface Pluginable extends LifeCycle {
	// void setServer(Server server);
	// void setConfig(AdapterConfig config);
	// AdapterConfig getConfig();
	public void setContext(PluginContext context);

	PluginContext getContext();
}
