package com.mttk.orche.core.impl;

import com.mttk.orche.addon.ServiceContext;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.Service;
import com.mttk.orche.util.StringUtil;

/**
 * 基础服务实现<br>
 *
 */
public class AbstractService extends AbstractLifeCycle implements Service {
	// 实现类可以得到服务对象
	protected Server server = null;
	protected ServiceContext context = null;
	private String key, name, description;
	private CATEGORY category = CATEGORY.SYS;

	//
	@Override
	public void setContext(ServiceContext context) {
		this.context = context;
	}

	//
	public String getKey() {
		if (StringUtil.notEmpty(key)) {
			return key;
		} else {
			return this.getClass().getName();
		}
	}

	public String getName() {
		if (StringUtil.notEmpty(name)) {
			return name;
		} else {
			return getKey();
		}
	}

	public String getDescription() {
		return description;
	}

	public CATEGORY getCategory() {
		return category;
	}

	//
	public void setServer(Server server) {
		this.server = server;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCategory(CATEGORY category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return getName() + "[" + getKey() + "]";
	}
}
