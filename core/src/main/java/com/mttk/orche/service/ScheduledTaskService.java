package com.mttk.orche.service;

import com.mttk.orche.core.PersistService;

/**
 * 管理定时任务
 * 
 *
 */
public interface ScheduledTaskService extends PersistService {

	//激活
	public void active(String id) throws Exception;
	//禁用
	public void deactive(String id) throws Exception;
}
