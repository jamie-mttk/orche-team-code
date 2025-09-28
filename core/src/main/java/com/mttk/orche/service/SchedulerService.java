package com.mttk.orche.service;

import java.util.List;
import java.util.Map;

import org.quartz.JobDetail;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.core.PersistService;
import com.mttk.orche.service.support.SchedulerCallback;

//实现对scheduler的维护以及任务调度
public interface SchedulerService extends PersistService {
	// 增加任务调度
	public void schedule(String job, String jobDescription, String group, String scheduler,
			Class<? extends SchedulerCallback> callbackClass, Map<String, Object> paras) throws Exception;

	// 取消任务调度
	public void unschedule(String job, String group) throws Exception;

	// 检查JOB是否存在
	public boolean jobExist(String job, String group) throws Exception;
	// //手工触发
	// public void triggerManually(String job,String group) throws Exception;

	// 增加任务调度 - 自定义scheduler config
	public void schedule(String job, String jobDescription, String group, AdapterConfig schedulerConfig,
			Class<? extends SchedulerCallback> callbackClass, Map<String, Object> paras) throws Exception;

	public List<JobDetail> listJobs(String groupName) throws Exception;
}
