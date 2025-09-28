package com.mttk.orche.service;

import com.mttk.orche.core.PersistService;
import com.mttk.orche.core.Service;

public interface ClusterEventService extends PersistService {
	public enum OPERATION {
		INSERT, // 数据新增
		UPDATE, // 数据更新
		DELETE // 数据删除
	}

	public void dataChanged(Service service, String identify, OPERATION operation) throws Exception;
}
