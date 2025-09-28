package com.mttk.orche.addon.impl;

import com.mongodb.client.model.Filters;
import com.mttk.orche.addon.EntryService;
import com.mttk.orche.addon.annotation.ServiceFlag;

/**
 * 基础的入口服务基类
 * 
 * @param <T>
 */
public class AbstractEntryService<T> extends AbstractActiveableService<T> implements EntryService {

	/**
	 * 所有的数据都存储在sysEntryData collection里 使用final防止用户随意修改导致无法维护
	 */
	@Override
	protected final String getCollectionName() {
		return "sysEntryData";
	}

	@Override
	public void doStart() throws Exception {
		// getStrategy().setActiveField("active").setEvalAuto(true);
		// 试图从EntryFlag的annotation中过滤
		ServiceFlag entryFlag = getClass().getAnnotation(ServiceFlag.class);
		if (entryFlag != null) {
			getStrategy().setFilter(Filters.eq("belongTo", entryFlag.key()));
		}
		//
		super.doStart();
	}
}
