package com.mttk.orche.cluster;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.impl.AbstractPersistService;

import com.mttk.orche.service.ClusterService;

import com.mttk.orche.support.MongoUtil;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.NetworkIpUtil;
import com.mttk.orche.util.StringUtil;

@ServiceFlag(key = "clusterService", name = "集群管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class ClusterServiceImpl extends AbstractPersistService implements ClusterService {
	private Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
	// 心跳时间间隔
	@Control(label = "心跳间隔(秒)", mandatory = true, size = 1, defaultVal = "10")
	private Long hearBeatInterval = 10l;
	// 检查服务器是否停机时间
	@Control(label = "停机检查间隔(秒)", mandatory = true, size = 1, defaultVal = "30")
	private Long checkInterval = hearBeatInterval * 3;
	// 服务器认为是停机的时间
	@Control(label = "停机阈值(秒)", mandatory = true, size = 1, defaultVal = "70")
	private Long downThreshold = hearBeatInterval * 7;
	//
	// 缓存本服务器的心跳包信息,
	private Document heartBeatDoc;
	// 心跳Timer
	private ScheduledExecutorService heartBeatTimer = null;
	// 检查Timer
	private ScheduledExecutorService checkTimer = null;

	@Override
	public void doStart() throws Exception {
		super.doStart();
		//
		if (!ServerUtil.getClusterMode(server)) {
			logger.info("Server is not running in cluster mode,custer service does not work properly.");
			return;
		}
		// 检测MongoDB是否支持获取当前时间
		determineMongoTime();
		//
		checkConfig();
		//
		logger.info("Cluster service is starting:hearBeatInterval=" + hearBeatInterval + ",checkInterval="
				+ checkInterval + ",downThreshold=" + downThreshold);
		//
		heartBeatTimer = Executors.newScheduledThreadPool(1);

		heartBeatTimer.scheduleAtFixedRate(new HeartBeatTask(this), 0, hearBeatInterval, TimeUnit.SECONDS);

		// heartBeatTimer=new Timer("Cluster heart beat timer");
		// heartBeatTimer.schedule(new HeartBeatTask(this),0, hearBeatInterval*1000);
		//
		checkTimer = Executors.newScheduledThreadPool(1);
		// 为了安全起见,检查程序会延迟一段时间再开始检查,防止大家都关机后同时启动造成的混乱
		// 随机数也是试图防止同时检查可能引起的问题
		// long delay = 3 * hearBeatInterval;
		// delay += (new Random()).nextInt(10);
		// checkTimer.scheduleAtFixedRate(new CheckTask(this), delay, checkInterval,
		// TimeUnit.SECONDS);

	}

	@Override
	public void doStop() throws Exception {
		super.doStop();
		if (heartBeatTimer != null) {
			heartBeatTimer.shutdown();
			;
		}
		if (checkTimer != null) {
			checkTimer.shutdown();
		}
	}

	@Override
	public List<Document> listServers() throws Exception {
		List<Document> list = super.find(null, 0, -1, null);
		// 如果找得到把当前实例放到最前面
		String currentInstanceId = ServerUtil.getInstanceId(server);
		// 找到当前服务器的位置
		if (StringUtil.notEmpty(currentInstanceId)) {
			int pos = -1;
			for (int i = 0; i < list.size(); i++) {
				if (currentInstanceId.equalsIgnoreCase(list.get(i).getString("server"))) {
					pos = i;
					break;
				}
			}
			// System.out.println("#####################"+pos+"~~~"+currentInstanceId);
			// System.out.println(list);
			// 不满足条件包括两种可能
			// -1:没找到
			// 0: 本来就是第一个
			if (pos > 0) {
				// 把位置pos和位置0的数据互换
				Document doc = list.get(pos);
				list.set(pos, list.get(0));
				list.set(0, doc);
			}
		}
		//
		return list;
	}

	// 心跳
	void heartBeat() {
		try {
			heartBeatInternal();
		} catch (Throwable t) {
			logger.error("Call heart beat failed", t);
		}
	}

	private void heartBeatInternal() throws Exception {
		if (heartBeatDoc == null) {
			Optional<Document> o = load("server", ServerUtil.getInstanceId(server));
			if (o.isPresent()) {
				heartBeatDoc = o.get();
			} else {
				heartBeatDoc = new Document();
				heartBeatDoc.put("server", ServerUtil.getInstanceId(server));
				insert(heartBeatDoc);
				// return;
			}
		}
		//
		heartBeatDoc.put("name", server.getSetting(Server.INSTANCE_NAME, String.class));
		// Changed by Jamie @2021/09/30
		// IP地址可能会变,所以需要每次都获取
		// 地址需要考虑是否有强行设置,因此不考虑地址变化的情况
		heartBeatDoc.put("ip", server.getSetting(Server.INSTANCE_IP, String.class));
		heartBeatDoc.put("port", server.getSetting(Server.INSTANCE_PORT, Integer.class));

		// heartBeatDoc.put("ip",NetworkIpUtil.getLocalHostLANAddress(true).getHostAddress());
		Date serverTime = new Date();
		Date mongoTime = getMongoTime();
		heartBeatDoc.put("serverTime", serverTime);
		heartBeatDoc.put("mongoTime", mongoTime);
		heartBeatDoc.put("lastTime", getTimeSmart(mongoTime, serverTime));
		// 防止被其他服务器删除了
		Optional<Document> o = load(MongoUtil.getId(heartBeatDoc));
		if (o.isPresent()) {
			replace(heartBeatDoc);
		} else {
			insert(heartBeatDoc);
		}

	}

	// // 检查是否服务器停机
	// void check() {
	// try {
	// checkInternal();
	// } catch (Throwable t) {
	// logger.error("Call check failed", t);
	// }
	// }

	// private void checkInternal() throws Exception {
	// // 计算那个时间点算down机器
	// Date expireDate = new Date(getMongoTimeWithDefault().getTime() -
	// downThreshold * 1000);
	// List<Document> downList = find(Filters.lt("lastTime", expireDate));
	// if (downList == null || downList.size() == 0) {
	// return;
	// }
	// logger.info("Found " + downList.size() + " down sever(s),try to recover");
	// //
	// for (Document doc : downList) {
	// recover(doc, downList);
	// }
	// }

	// private void recover(Document downServer, List<Document> downList) throws
	// Exception {
	// //
	// if (isTheOne(downServer, downList)) {
	// logger.info("For " + downServer.getString("server") + " ,this server ["
	// + server.getSetting(Server.INSTANCE_ID, String.class) + "] is choosed to
	// recover ...");
	// } else {
	// logger.info("For " + downServer.getString("server") + " ,this server is NOT
	// choosed to recover ...");
	// return;
	// }
	// //
	// List<Document> recoverList = findRecoverList(downServer);
	// if (recoverList == null || recoverList.size() == 0) {
	// //
	// logger.info("For " + downServer.getString("server") + " ,no audit is found to
	// recover");
	// } else {
	// //
	// logger.info("For " + downServer.getString("server") + " ,found " +
	// recoverList.size()
	// + " audits to recover,try to determine whether this is the right server to
	// recover ...");
	// logger.info("Audit list:" + recoverList);
	// // 把要恢复服务器的所有RUN的list server修改为本服务器
	// changeRecoverServer(downServer, recoverList);
	// //
	// server.getService(DispatchService.class).executeImmediately(recoverList);
	// }
	// // 删除此服务记录否则一直重复recover
	// this.delete(MongoUtil.getId(downServer));
	// //
	// logger.info("Server " + "For " + downServer.getString("server") + " is
	// removed from active server list.");

	// }

	// // 检查是否有需要恢复的列表
	// private List<Document> findRecoverList(Document downServer) throws Exception
	// {
	// AuditService auditService = server.getService(AuditService.class);
	// return auditService.findTakeover(downServer.getString("server"));
	// }

	// private void changeRecoverServer(Document downServer, List<Document>
	// recoverList) throws Exception {
	// AuditService auditService = server.getService(AuditService.class);
	// String thisInstanceId = ServerUtil.getInstanceId(server);
	// for (Document doc : recoverList) {
	// logger.info("For " + downServer.getString("server") + " ,change audit [" +
	// MongoUtil.getId(doc)
	// + "] server to " + thisInstanceId);
	// doc.put("server", thisInstanceId);
	// auditService.replace(doc);
	// }
	// }

	// // 检查本服务器是否是合适的服务器去恢复
	// // 哈希差
	// private boolean isTheOne(Document downServer, List<Document> downList) throws
	// Exception {
	// // 计算当前服务器的差值
	// // 安全第一，如果本服务器没有发出过包则认为你不是
	// if (heartBeatDoc == null) {
	// return false;
	// }
	// int myCal = cal(heartBeatDoc, downServer);
	// //
	// List<Document> fullList = find(null);
	// for (Document doc : fullList) {
	// // 不是本服务器
	// if (MongoUtil.getId(doc).equals(MongoUtil.getId(heartBeatDoc))) {
	// continue;
	// }
	// // 不是已经down的
	// if (isInDownList(doc, downList)) {
	// continue;
	// }
	// //
	// int thisCal = cal(doc, downServer);
	// // 当前服务器哈希差大,所以不是合适的人选
	// // 如果相等，会出现都问题
	// if (myCal > thisCal) {
	// return false;
	// }
	// }
	// //
	// return true;
	// }

	// // 给定dd是否再downList中
	// private boolean isInDownList(Document d, List<Document> downList) {
	// for (Document dd : downList) {
	// if (MongoUtil.getId(d).equals(MongoUtil.getId(dd))) {
	// return true;
	// }
	// }
	// //
	// return false;
	// }

	// private int cal(Document server, Document downServer) {
	// return Math.abs(MongoUtil.getId(server).hashCode() -
	// MongoUtil.getId(downServer).hashCode());
	// }

	// 检查参数符合要求
	private void checkConfig() {
		if (hearBeatInterval <= 0l) {
			hearBeatInterval = 10l;
		}
		if (checkInterval <= hearBeatInterval) {
			hearBeatInterval = checkInterval * 3;
		}
		if (downThreshold <= hearBeatInterval) {
			downThreshold = checkInterval * 7;
		}
	}

	/*******************************************************************************
	 * 与获取MongoDB相关
	 * 
	 *******************************************************************************/
	// 用于从mongodb得到当前时间
	List<Bson> aggregatePipeline = null;

	// 检查MongoDB是否支持获取时间方法
	// 4.2以上的支持NOW系统变量
	// 4.2以上的集群支持CLUSTER_TIME系统变量
	private void determineMongoTime() {
		// 试图检查是否支持CLUSTER_TIME
		try {
			aggregatePipeline = new ArrayList<>();
			aggregatePipeline.add(Aggregates.match(Filters.eq("key", "clusterService")));
			// aggregatePipeline.add(new BasicDBObject("$unset", "ui"));
			aggregatePipeline.add(Aggregates.addFields(new Field<String>("mongoTime", "$$CLUSTER_TIME")));
			AggregateIterable<Document> iterable = server.obtainCollection("sysService").aggregate(aggregatePipeline);
			// 只有试图得到数据时才可能触发错误报警
			Document doc = iterable.first();
			logger.info("Mongodb supports using CLUSTER_TIME system variable to get current time ");
			return;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.info("Mongodb does not support CLUSTER_TIME system variable");
			//
			aggregatePipeline = null;
		}
		try {
			aggregatePipeline = new ArrayList<>();
			aggregatePipeline.add(Aggregates.match(Filters.eq("key", "clusterService")));
			// aggregatePipeline.add(new BasicDBObject("$unset", "ui"));
			aggregatePipeline.add(Aggregates.addFields(new Field<String>("mongoTime", "$$NOW")));
			AggregateIterable<Document> iterable = server.obtainCollection("sysService").aggregate(aggregatePipeline);
			// 只有试图得到数据时才可能触发错误报警
			Document doc = iterable.first();
			logger.info("Mongodb supports using NOW system variable to get current time ");
			return;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn("Mongodb does not support NOW system variable");
			aggregatePipeline = null;
		}
		// 无法从MongoDB得到当前时间,只能使用本地时间
		logger.warn("Can not get current time from MongoDB , please upgrade mongoDB to version 4.2 or above.");
		logger.warn("In this situation ,please make sure the time of all the machines in the cluster are accurate!.");
	}

	// 从MondoDB得到当前时间,如果无法得到返回null
	private Date getMongoTime() {
		if (aggregatePipeline == null) {
			return null;
		}
		try {
			AggregateIterable<Document> iterable = server.obtainCollection("sysService").aggregate(aggregatePipeline);
			// 只有试图得到数据时才可能触发错误报警
			Document doc = iterable.first();
			//
			Object date = doc.get("mongoTime");
			if (date == null) {
				return null;
			}
			if (date instanceof Date) {
				return (Date) date;
			} else if (date instanceof BsonTimestamp) {
				long l = ((BsonTimestamp) date).getTime();
				l *= 1000;
				// System.out.println("$$$$$$$$"+l);
				Date d = new Date(l);
				// System.out.println("###########"+d);
				return d;
			} else {
				throw new Exception("CLUSTER_TIME returns unsupported date type:" + date.getClass());
			}

		} catch (Exception e) {
			logger.warn("Get current time from Mongodb failed", e);
			return null;
		}
	}

	// 获取当前时间,如果能够从MongoDB获取则获取;否则取服务器本地时间
	private Date getMongoTimeWithDefault() {
		Date mongoTime = getMongoTime();
		if (mongoTime == null) {
			mongoTime = new Date();
		}
		return mongoTime;
	}

	// 如果mongoTime为null则返回服务器时间
	private Date getTimeSmart(Date mongoTime, Date serverTime) {
		if (mongoTime == null) {
			return serverTime;
		}
		return mongoTime;
	}

	// ****************************
	// *Getter/Setter
	// ***************************
	public void setHearBeatInterval(Long hearBeatInterval) {
		this.hearBeatInterval = hearBeatInterval;
	}

	public void setCheckInterval(Long checkInterval) {
		this.checkInterval = checkInterval;
	}

	public void setDownThreshold(Long downThreshold) {
		this.downThreshold = downThreshold;
	}
}

// class CheckTask extends TimerTask {
// private ClusterServiceImpl impl;

// public CheckTask(ClusterServiceImpl impl) {
// this.impl = impl;
// }

// public void run() {
// impl.check();
// // LoggerFactory.getLogger(HeartBeatTask.class).info("CHECK");
// }
// }

class HeartBeatTask extends TimerTask {
	private ClusterServiceImpl impl;

	public HeartBeatTask(ClusterServiceImpl impl) {
		this.impl = impl;
	}

	public void run() {
		impl.heartBeat();
		// LoggerFactory.getLogger(HeartBeatTask.class).info("BEAT");
	}
}
