package com.mttk.orche.service.support;

import java.util.Map;

/**
 * 用于Scheduler任务的回调并附带设置任务时的参数
 *
 */
public interface SchedulerCallback {
	void doInScheduler(Map<String, Object> paras) throws Exception;
}
