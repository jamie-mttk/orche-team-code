package com.mttk.orche.core.impl.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.impl.ServiceContextImpl;
import com.mttk.orche.core.LifeCycle;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.Service;
import com.mttk.orche.core.Service.CATEGORY;

import com.mttk.orche.support.MongoUtil;
import com.mttk.orche.util.StringUtil;

public class ServiceLoadUtil {
	// 从doc中构建一个服务对象
	public static Service loadService(Server server, Document doc, Logger logger) throws Exception {
		String implClass = doc.getString("implClass");
		@SuppressWarnings("rawtypes")
		// Class clazz=Class.forName(implClass);
		Class clazz = server.obtainClassLoader(doc.getString("_package_name")).loadClass(implClass);
		@SuppressWarnings("unchecked")
		Object object = clazz.getConstructor().newInstance();

		if (!(object instanceof Service)) {
			throw new RuntimeException("Bean [" + implClass + "] must implement " + Service.class.getName());
		}
		// 处理属性
		MongoUtil.fillProps(object, doc.get("props", Document.class), logger);
		//
		Service service = (Service) object;
		//
		service.setKey(doc.getString("key"));
		service.setName(doc.getString("name"));
		service.setDescription(doc.getString("description"));
		service.setCategory(CATEGORY.valueOf(doc.get("category", "SYS")));
		//
		service.setServer(server);
		//
		service.setContext(new ServiceContextImpl(server, LoggerFactory.getLogger(clazz)));
		//
		logger.info("Service " + service + "is loaded");
		//
		return service;
	}

	// 同上，加入到serviceMap里；出错时记录错误日志后函数不报错
	public static boolean loadService(Server server, Map<String, ServiceWrap> serviceMap, Document doc, Logger logger) {
		try {
			serviceMap.put(doc.getString("key"), new ServiceWrap(doc, loadService(server, doc, logger)));
			return true;
		} catch (Throwable t) {
			logger.error("Fail to load service:" + doc, t);
			return false;
		}
	}

	// 视图启动所有服务
	public static boolean tryStartServices(Map<String, ServiceWrap> serviceMap, Logger logger) {
		final Set<String> keySet = new HashSet<>();
		// 浅复制
		for (String key : serviceMap.keySet()) {
			// 只有有autoStart标记的才自动启动
			if (serviceMap.get(key).getDoc().getBoolean("autoStart", true)) {
				keySet.add(key);
			}
		}
		//
		logger.info("Found " + keySet.size() + " service(s) to start:" + keySet);
		// 最多循环最大数量的次数，运气最差的情形
		// 必须先保存，因为docSet大小会变
		int maxLoop = keySet.size();
		for (int i = 0; i < maxLoop; i++) {
			Set<String> runningSet = startServices(serviceMap, keySet, logger);
			if (runningSet == null || runningSet.size() == 0) {
				// 没有任何服务可以启动，结束
				break;
			}
		}
		//
		if (keySet.size() > 0) {
			logger.error("There are " + keySet.size() + " services as below can not be started!");
			for (String key : keySet) {
				Document doc = serviceMap.get(key).getDoc();
				logger.error("key:" + doc.get("key") + "\tname:" + doc.get("name"));
			}
			//
			return false;
		} else {
			return true;
		}

	}

	// 尽可能启动给定的服务(如果其依赖已经启动的前提下),返回启动成功的列表或空列表
	// 启动成功的会从serviceDocSet移除
	private static Set<String> startServices(Map<String, ServiceWrap> serviceMap, Set<String> keySet, Logger logger) {
		// 先查找那些是可以运行的
		Set<String> foundSet = new HashSet<>();
		for (String key : keySet) {
			// 得到depends
			if (allowToStart(serviceMap, key)) {
				foundSet.add(key);
			}
		}

		//
		if (foundSet.isEmpty()) {
			return foundSet;
		}
		//
		Set<String> runningSet = new HashSet<>();
		for (String key : foundSet) {
			if (startService(serviceMap, key, logger)) {
				//
				runningSet.add(key);
			}
		}
		//
		keySet.removeAll(runningSet);
		//
		return runningSet;

	}

	public static boolean startService(Map<String, ServiceWrap> serviceMap, String key, Logger logger) {
		ServiceWrap sw = serviceMap.get(key);
		try {
			logger.info("Try to start service " + sw.getDoc().getString("name") + "[" + sw.getService().getKey() + "]");
			sw.getService().start();
			//
			// logger.info(sw.getDoc().getString("name")+"["+sw.getService().getKey()+"]"+"
			// is started.");
			// if ("dispatchService".equalsIgnoreCase(sw.getService().getKey())) {
			// try {
			// throw new RuntimeException("AAA");
			// }catch(Exception e) {
			// e.printStackTrace();
			// }
			// }
			//
			logger.info("Successfully start service " + sw.getDoc().getString("name") + "[" + sw.getService().getKey()
					+ "]");
			//
			return true;
		} catch (Exception e) {
			logger.error("Fail to start service from :" + sw.getDoc(), e);
			return false;
		}
	}

	// 检查其依赖是否已经启动
	public static boolean allowToStart(Map<String, ServiceWrap> serviceMap, String key) {
		String depends = serviceMap.get(key).getDoc().getString("depends");
		if (StringUtil.isEmpty(depends)) {
			return true;
		}
		//
		for (String s : depends.split(",")) {
			// String d=findServiceDoc(serviceMap,s);
			// if (d==null) {
			// //不存在的依赖，允许继续(可能是手滑打错了或这个服务删除了)
			// return true;
			// }
			// 如果依赖没有运行不允许启动
			if (!isServiceRunning(serviceMap, s)) {
				return false;
			}
		}
		//
		return true;

	}

	// 根据服务名找到服务文档
	// public static Document findServiceDoc(Map<String,ServiceWrap>
	// serviceMap,String serviceId) {
	// ServiceWrap sw=serviceMap.get(serviceId);
	// if(sw==null) {
	// return null;
	// }
	// //
	// return sw.getDoc();
	// }
	// 判断服务是否在运行中
	public static boolean isServiceRunning(Map<String, ServiceWrap> serviceMap, String key) {
		ServiceWrap sw = serviceMap.get(key);
		if (sw == null) {
			return false;
		}
		return sw.getService().getStatus() == LifeCycle.Status.RUNNING;
	}

	//
	// stop
	//
	// 试图关闭所有服务
	// arbitraryStop:如果服务有依赖也强制停止
	public static boolean tryStopAllService(Map<String, ServiceWrap> serviceMap, boolean arbitraryStop, Logger logger) {
		final Set<String> keySet = new HashSet<>();
		// 浅复制
		// 得到所有正在运行的服务
		for (String key : serviceMap.keySet()) {
			if (isServiceRunning(serviceMap, key)) {
				keySet.add(key);
			}
		}
		logger.info("Found " + keySet.size() + " service(s) to stop");

		// 最多循环最大数量的次数，运气最差的情形
		// 必须先保存，因为docSet大小会变
		int maxLoop = keySet.size();
		for (int i = 0; i < maxLoop; i++) {
			Set<String> stoppedSet = stopAllService(serviceMap, keySet, logger);
			if (stoppedSet == null || stoppedSet.size() == 0) {
				// 没有任何服务可以停止，结束
				break;
			} else {
				for (String key : stoppedSet) {
					keySet.remove(key);
				}
			}
		}
		//
		if (keySet.size() > 0) {
			logger.error("There are " + keySet.size() + " services as below can not be stopped!");
			for (String key : keySet) {
				if (arbitraryStop) {
					logger.error("Service is stopped arbitrarily:" + key);
					//
					stopService(serviceMap, key, logger);
				} else {
					logger.error("Service is still running:" + key);
				}
			}
			//
			return false;
		} else {
			return true;
		}
	}

	// 尽可能停止给定的服务(如果其依赖已经启动的前提下),返回停止成功的列表或空列表
	// 启动成功的会从serviceDocSet移除
	private static Set<String> stopAllService(Map<String, ServiceWrap> serviceMap, Set<String> serviceKeySet,
			Logger logger) {
		// 先查找那些是可以停止的的
		Set<String> foundSet = new HashSet<>();
		for (String key : serviceKeySet) {
			// 得到depends
			if (allowToStop(serviceMap, key)) {
				foundSet.add(key);
			}
		}
		//
		if (foundSet.isEmpty()) {
			return foundSet;
		}
		//
		Set<String> stoppedSet = new HashSet<>();
		for (String key : foundSet) {
			if (stopService(serviceMap, key, logger)) {
				//
				stoppedSet.add(key);
			}
		}
		//
		// serviceDocSet.removeAll(stoppedSet);
		//
		return stoppedSet;

	}

	// 检查服务是否被其他服务引用
	public static boolean allowToStop(Map<String, ServiceWrap> serviceMap, String key) {
		for (String k : serviceMap.keySet()) {
			if (isServiceRunning(serviceMap, k)) {
				//
				String depends = serviceMap.get(k).getDoc().getString("depends");
				if (StringUtil.isEmpty(depends)) {
					continue;
				}
				//
				for (String s : depends.split(",")) {
					if (s.equals(key)) {
						return false;
					}
				}
			}
		}
		//
		return true;
	}

	public static boolean stopService(Map<String, ServiceWrap> serviceMap, String key, Logger logger) {
		try {
			ServiceWrap sw = serviceMap.get(key);
			if (sw != null) {
				sw.getService().stop();
			}
			// 从serviceMap里删除掉
			serviceMap.remove(key);
			//
			return true;
		} catch (Exception e) {
			logger.error("Fail to stop service from :" + key, e);
			return false;
		}
	}

	// 把输入的serviceMap中给定category的筛选出来
	public static Map<String, ServiceWrap> splitServiceMap(Map<String, ServiceWrap> serviceMap, CATEGORY category) {
		Map<String, ServiceWrap> map = new HashMap<>();
		serviceMap.keySet().forEach((key) -> {
			if (category.name().equals(serviceMap.get(key).getDoc().getString("category"))) {
				map.put(key, serviceMap.get(key));
			}
		});
		return map;
	}
}
