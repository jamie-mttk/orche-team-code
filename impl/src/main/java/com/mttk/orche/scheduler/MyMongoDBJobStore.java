package com.mttk.orche.scheduler;

import org.bson.Document;
import org.quartz.JobDetail;
import org.quartz.SchedulerConfigException;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.SchedulerSignaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.Event;
import com.mttk.orche.addon.impl.EventImpl;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.EventService;
import com.mttk.orche.util.StringUtil;
import com.novemberain.quartz.mongodb.MongoDBJobStore;
import com.novemberain.quartz.mongodb.db.MongoConnector;

public class MyMongoDBJobStore extends MongoDBJobStore {
	private static Logger logger = LoggerFactory.getLogger(MyMongoDBJobStore.class);

	public MyMongoDBJobStore() {
		
		super(createMongoConnector());
	}

	private static MongoConnector createMongoConnector() {
		//
		return new MyMongoConnector(ServerLocator.getServer().obtainMongoDatabase());
	}

	@Override
	public void initialize(ClassLoadHelper loadHelper, SchedulerSignaler signaler) throws SchedulerConfigException {
		super.initialize(loadHelper, signaler);

	}

	// Override to retry 5 times if failed and throw alert since it is quite
	// critical
	@Override
	public void triggeredJobComplete(OperableTrigger trigger, JobDetail job,
			CompletedExecutionInstruction triggerInstCode) {
		Throwable throwable = null;
		for (int i = 0; i < 5; i++) {
			// 因为如果通知失败会导致scheduler一直不执行,所以只能重试多次确保没有问题
			try {
				 super.triggeredJobComplete(trigger, job, triggerInstCode);
				//throw new RuntimeException("TEST");
				 return;
			} catch (Throwable t) {
				throwable = t;
				//
				logger.warn("Quartz fail to notify job completion try after 1 second",t);
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					// 等待1秒后再试
				}
			}
		}
		//
		throwEvent(trigger, job, throwable);
		//
		logger.error("Urgent alert: Quartz fail to notify job completion, this will cause scheduler to stop trigger.",
				throwable);
	}

	//
	private void throwEvent(OperableTrigger trigger, JobDetail job, Throwable throwable) {
		Event event = new EventImpl();
		// 公共参数
		event.put("type", "system");
		event.put("source", "scheduler");
		String jobDescription = job.getDescription();
		if (StringUtil.isEmpty(jobDescription)) {
			jobDescription = job.getJobClass().getCanonicalName() + "_" + job.getKey();
		}
		event.put("sourceSub", jobDescription);

		event.put("content",
				"Urgent alert: Quartz fail to notify job completion, this will cause scheduler to stop trigger.\n"
						+ "The only way is to delete the qrtz_lock with keyName=" + job.getKey().getName()
						+ " or restart server.");
		event.put("exception", throwable);
		//
		Document infos = new Document();
		event.put("infos", infos);
		infos.put("jobKeyName", job.getKey().getName());
		infos.put("jobKeyGroup", job.getKey().getGroup());
		infos.put("jobDescription", job.getDescription());
		infos.put("jobClass", job.getJobClass().getCanonicalName());
		infos.put("jobDataMap", job.getJobDataMap().toString());

		//
		EventService eventService = ServerLocator.getServer().getService(EventService.class);
		eventService.inform(event);
	}
}
