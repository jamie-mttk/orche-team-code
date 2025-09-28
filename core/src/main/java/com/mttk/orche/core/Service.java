package com.mttk.orche.core;

import com.mttk.orche.addon.ServiceContext;
/**
 * 服务接口<br>
 * 所有方法不建议用户调用
 *
 */
public interface Service extends LifeCycle {
	/**
	 * SYS:系统级别的服务，如payloadService,runtimeService
	 *USER:用户级别的服务，如用户定义的入口服务,池服务
	 */
	public enum CATEGORY{SYS,USER};
	//
	void setServer(Server server);
	//
	void setContext(ServiceContext context);
	//
	String getKey();
	//
	String getName();
	//
	String getDescription();
	//
	CATEGORY getCategory();
	//
	void setKey(String key);
	void setName(String name);
	void setDescription(String description);
	void setCategory(CATEGORY category);
}
