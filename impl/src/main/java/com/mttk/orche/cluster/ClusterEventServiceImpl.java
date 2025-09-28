package com.mttk.orche.cluster;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.core.Service;
import com.mttk.orche.core.impl.AbstractCacheBeanService;
import com.mttk.orche.core.impl.AbstractPersistService;
import com.mttk.orche.service.ClusterEventService;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.StringUtil;

@ServiceFlag(key = "clusterEventService", name = "集群事件管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class ClusterEventServiceImpl extends AbstractPersistService implements ClusterEventService {
	private Logger logger = LoggerFactory.getLogger(ClusterEventServiceImpl.class);

	// 检查服务器是否有新事件的时间间隔
	@Control(label = "新事件检查间隔(秒)", mandatory = true, size = 1, defaultVal = "30")
	private Long eventCheckInterval = 30l;
	// 检查事件过长的事件的时间间隔(可以很长,缺省一个小时)
	@Control(label = "过期检查间隔(秒)", mandatory = true, size = 1, defaultVal = "900")
	private Long expireCheckInterval = 15 * 60l;
	// 过期时间 - 超过2个小时的都认为过期了
	@Control(label = "事件过期阈值(秒)", mandatory = true, size = 1, defaultVal = "7200")
	private Long expirePeriod = 2 * 60 * 60l;

	// 检查Timer
	private ScheduledExecutorService eventCheckTimer = null;
	//
	private ScheduledExecutorService expireCheckTimer = null;
	// 记录最后检查到的ObjectId
	private ObjectId lastId = null;

	@Override
	public void dataChanged(Service service, String identify, OPERATION operation) throws Exception {
		Document doc = new Document();
		doc.put("server", ServerUtil.getInstanceId(server));
		doc.put("service", service.getClass().getName());
		doc.put("eventTime", new Date());
		if (StringUtil.notEmpty(identify)) {
			doc.put("identify", identify);
		}
		if (operation != null) {
			doc.put("operation", operation.name());
		}
		//
		insert(doc);
	}

	@Override
	public void doStart() throws Exception {
		super.doStart();
		//
		if (!ServerUtil.getClusterMode(server)) {
			logger.info("Server is not running in cluster mode,custer event service does not work properly.");
			return;
		}
		//
		checkConfig();

		//
		logger.info("Cluster event service is starting: eventCheckInterval=" + eventCheckInterval
				+ ",expireCheckInterval=" + expireCheckInterval + ",expirePeriod=" + expirePeriod);
		//
		handleLastId();
		//
		eventCheckTimer = Executors.newScheduledThreadPool(1);
		eventCheckTimer.scheduleAtFixedRate(new EventCheckTask(this), 0, eventCheckInterval, TimeUnit.SECONDS);
		// eventCheckTimer.schedule(new EventCheckTask(this),0,
		// eventCheckInterval*1000);
		//
		expireCheckTimer = Executors.newScheduledThreadPool(1);
		// expireCheckTimer.schedule(new ExpireCheckTask(this),0,
		// expireCheckInterval*1000);
		expireCheckTimer.scheduleAtFixedRate(new ExpireCheckTask(this), 0, expireCheckInterval, TimeUnit.SECONDS);
	}

	@Override
	public void doStop() throws Exception {
		if (eventCheckTimer != null) {
			eventCheckTimer.shutdown();
		}
		if (expireCheckTimer != null) {
			expireCheckTimer.shutdown();
		}
	}

	// 检查参数符合要求
	private void checkConfig() {
		if (eventCheckInterval <= 0) {
			eventCheckInterval = 30l;
		}
		if (expireCheckInterval <= 0) {
			expireCheckInterval = 15 * 60l;
		}
		if (expirePeriod <= 0) {
			expireCheckInterval = 2 * 60 * 60l;
		}
	}

	public void eventCheck() {
		try {
			eventCheckInternal();
		} catch (Throwable t) {
			logger.error("Event check failed", t);
		}
	}

	private void eventCheckInternal() {
		// 试图获得需要同步的数据
		List<Bson> filters = new ArrayList<>(2);
		filters.add(Filters.ne("server", ServerUtil.getInstanceId(server)));
		if (lastId != null) {
			filters.add(Filters.gt("_id", lastId));
		}
		FindIterable<Document> fi = obtainCollection().find(Filters.and(filters)).sort(Sorts.ascending("_id"));
		MongoCursor<Document> cursor = fi.iterator();
		Document event;
		while (cursor.hasNext()) {
			event = cursor.next();
			try {
				handleEvent(event);
			} catch (Exception e) {
				logger.error("Fail to handle event:" + event, e);
			}
		}
	}

	private void handleEvent(Document event) throws Exception {
		String service = event.getString("service");
		String identify = event.getString("identify");// 不允许为空
		String temp = event.getString("operation");
		OPERATION operation = null;
		if (StringUtil.notEmpty(temp)) {
			operation = OPERATION.valueOf(temp);
		}
		//
		Service s = server.getService((Class<Service>) Class.forName(service));
		if (s instanceof AbstractCacheBeanService) {
			AbstractCacheBeanService ss = (AbstractCacheBeanService) s;
			ss.dataChanged(identify, operation);
		} else {
			logger.warn("Event is ignored since service can not accept:" + event);
		}
		//
		lastId = event.getObjectId("_id");
	}

	// 启动时试图获得当前最大的Object Id
	private void handleLastId() {
		FindIterable<Document> fi = obtainCollection().find().sort(Sorts.descending("_id")).limit(1);
		Document doc = fi.first();
		if (doc != null) {
			lastId = doc.getObjectId("_id");
		}
	}

	public void expireCheck() {
		try {
			expireCheckInternal();
		} catch (Throwable t) {
			logger.error("Expire check failed", t);
		}
	}

	public void expireCheckInternal() {
		Date expireDate = new Date(System.currentTimeMillis() - expirePeriod * 1000);
		DeleteResult result = obtainCollection().deleteMany(Filters.lt("eventTime", expireDate));
		if (result.getDeletedCount() > 0) {
			logger.info(result.getDeletedCount() + " records has been purged!");
		}
	}
	//
	// Getter/Setter
	//

	public void setEventCheckInterval(Long eventCheckInterval) {
		this.eventCheckInterval = eventCheckInterval;
	}

	public void setExpireCheckInterval(Long expireCheckInterval) {
		this.expireCheckInterval = expireCheckInterval;
	}

	public void setExpirePeriod(Long expirePeriod) {
		this.expirePeriod = expirePeriod;
	}

}

//
class EventCheckTask extends TimerTask {
	private ClusterEventServiceImpl impl;

	public EventCheckTask(ClusterEventServiceImpl impl) {
		this.impl = impl;
	}

	public void run() {
		impl.eventCheck();
		//

	}
}

//
class ExpireCheckTask extends TimerTask {
	private ClusterEventServiceImpl impl;

	public ExpireCheckTask(ClusterEventServiceImpl impl) {
		this.impl = impl;
	}

	public void run() {
		impl.expireCheck();

	}
}
