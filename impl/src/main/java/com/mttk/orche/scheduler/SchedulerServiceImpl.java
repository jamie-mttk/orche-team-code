package com.mttk.orche.scheduler;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.core.Server.RUNNING_MODE;
import com.mttk.orche.core.impl.AbstractCacheBeanService;
import com.mttk.orche.service.SchedulerService;
import com.mttk.orche.service.support.SchedulerCallback;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.PropertiesUtil;

@ServiceFlag(key = "schedulerService", name = "定时器管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class SchedulerServiceImpl extends AbstractCacheBeanService<AdapterConfig> implements SchedulerService {
	//
	@Control(label = "配置文件", defaultVal = "", size = 1)
	private String configFile = null;
	//
	private SchedulerFactory sf = null;
	private Scheduler scheduler = null;

	// private JobDetail dummyJobDetail=null;
	private Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

	@Override
	public void doStart() throws Exception {
		super.doStart();
		//
		//
		try {
			//
			if (configFile == null) {
				configFile = ServerUtil.getPathConf(server) + File.separator + "quartz.properties";
			}
			logger.info("Initialize schedule engine with config file:" + configFile);
			//
			Properties props = PropertiesUtil.load(configFile);
			// 替换insatanceid,如果使用AUTO,则每次启动后都生成不同的ID;导致只有过期后scheduler才正常
			props.put("org.quartz.scheduler.instanceId", ServerUtil.getInstanceId(server));
			//
			sf = new org.quartz.impl.StdSchedulerFactory(props);

			scheduler = sf.getScheduler();
			// Add listener
			// scheduler.getListenerManager().addJobListener(new MyJobListener(),
			// EverythingMatcher.allJobs());
		} catch (Exception e) {
			throw new RuntimeException("Fail to init quartz scheduler", e);
		}
		//
		try {
			if (server.getRunningMode() != RUNNING_MODE.NORMAL) {
				logger.warn("Server is runing under " + server.getRunningMode() + " mode, scheduler is not started!");
				return;
			}
			scheduler.start();
		} catch (Exception e) {
			throw new RuntimeException("Fail to start quartz scheduler", e);
		}
	}

	@Override
	public void doStop() throws Exception {
		super.doStop();
		//
		try {
			// Changed by Jamie @2020/06/05
			// true:代表等待线程池结束后才关闭
			scheduler.shutdown(true);
		} catch (Exception e) {
			throw new RuntimeException("Fail to stop quartz scheduler", e);
		}
	}

	@Override
	public void schedule(String job, String jobDescription, String group, AdapterConfig schedulerConfig,
			Class<? extends SchedulerCallback> callbackClass, Map<String, Object> paras) throws Exception {
		try {

			//
			SchedulerUtil.schedule(scheduler, job, jobDescription, group, schedulerConfig, callbackClass, paras);

		} catch (Exception e) {
			throw new RuntimeException("Schedule job [" + job + "] of group [" + group + "] failed:", e);
		}
	}

	@Override
	public void schedule(String job, String jobDescription, String group, String schedulerId,
			Class<? extends SchedulerCallback> callbackClass, Map<String, Object> paras) throws Exception {
		AdapterConfig config = obtainBean(schedulerId);
		if (config == null) {
			throw new RuntimeException("No scheduler is found for ID:" + schedulerId);
		}
		schedule(job, jobDescription, group, config, callbackClass, paras);
	}

	@Override
	public List<JobDetail> listJobs(String groupName) throws Exception {
		return SchedulerUtil.listJobs(scheduler, groupName);
	}

	@Override
	public void unschedule(String job, String group) {
		try {
			//
			SchedulerUtil.unschedule(scheduler, job, group);
		} catch (Exception e) {
			throw new RuntimeException("Unschedule job [" + job + "] of group [" + group + "] failed:", e);
		}

	}

	@Override
	public boolean jobExist(String job, String group) {
		try {
			//
			return SchedulerUtil.jobExist(scheduler, job, group);
		} catch (Exception e) {
			throw new RuntimeException("Get  job [" + job + "] of group [" + group + "] failed:", e);
		}

	}

	// @Override
	// public void triggerManually(String job,String group) throws Exception{
	// try {
	// SchedulerUtil.triggerManually(scheduler, job, group);
	// }catch(Exception e) {
	// throw new RuntimeException("Mannly triiger job ["+job+"] of group ["+group+"]
	// failed:",e);
	// }
	// }
	//
	// Setter/Getter
	//
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
}
