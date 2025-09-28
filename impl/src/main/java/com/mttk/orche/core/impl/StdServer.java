package com.mttk.orche.core.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.SSLServerSocketFactory;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.core.LifeCycle;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.core.Service;
import com.mttk.orche.core.Server.RUNNING_MODE;
import com.mttk.orche.core.impl.util.AddonLoaderSupport;
import com.mttk.orche.core.impl.util.ServiceLoadUtil;
import com.mttk.orche.core.impl.util.ServiceWrap;

import com.mttk.orche.service.I18nService;
import com.mttk.orche.support.MongoUtil;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.FileHelper;
import com.mttk.orche.util.StringUtil;

public class StdServer extends AbstractServerPersist {
	private Logger logger = LoggerFactory.getLogger(StdServer.class);
	//
	private Map<String, ServiceWrap> serviceMap = new HashMap<>();
	private static StdServer instance;
	//
	private AddonLoaderSupport addonLoaderSupport = null;
	//
	private RUNNING_MODE runningMode = RUNNING_MODE.NORMAL;
	//
	// init mode下，下面的服务不启动
	// private static final String[] serviceNotStartInitMode = new String[] {
	// "schedulerService", "dispatchService" };

	private StdServer() {

	}

	@Override
	public RUNNING_MODE getRunningMode() {
		return runningMode;
	}

	//
	public void setRunningMode(RUNNING_MODE runningMode) {
		this.runningMode = runningMode;
	}
	// public static void showCipher()
	// throws Exception
	// {
	// SSLServerSocketFactory ssf =
	// (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
	//
	// String[] defaultCiphers = ssf.getDefaultCipherSuites();
	// String[] availableCiphers = ssf.getSupportedCipherSuites();
	//
	// TreeMap ciphers = new TreeMap();
	//
	// for(int i=0; i<availableCiphers.length; ++i )
	// ciphers.put(availableCiphers[i], Boolean.FALSE);
	//
	// for(int i=0; i<defaultCiphers.length; ++i )
	// ciphers.put(defaultCiphers[i], Boolean.TRUE);
	//
	// System.out.println("Default\tCipher");
	// for(Iterator i = ciphers.entrySet().iterator(); i.hasNext(); ) {
	// Map.Entry cipher=(Map.Entry)i.next();
	//
	// if(Boolean.TRUE.equals(cipher.getValue()))
	// System.out.print('*');
	// else
	// System.out.print(' ');
	//
	// System.out.print('\t');
	// System.out.println(cipher.getKey());
	// }
	// }

	@Override
	public ClassLoader obtainClassLoader(String addonName) {
		return addonLoaderSupport.obtain(addonName);
	}

	public synchronized static StdServer getInstance() {
		if (instance == null) {
			instance = new StdServer();
			//
			instance.setServer(instance);
		}
		// 把服务注册Locator里，其他一个虚拟机的都能够查询到
		ServerLocator.registServer(instance);
		//
		return instance;
	}

	//
	@Override
	public void init(String homePath) throws Exception {
		super.init(homePath);

		//
		// showCipher();
	}

	// Added @2022/12/29
	private void checkRunning() throws Exception {

		// Lock file -不能放到temp目录下，会被删除
		File lockFile = new File(ServerUtil.getPathHome(this) + File.separator + "bin" + File.separator + "file.lock");
		if (lockFile.exists()) {
			lockFile.delete();
		}
		try (FileOutputStream lockFileOS = new FileOutputStream(lockFile)) {
		}
		// 注意这里不能关闭acessFile/lockChannel否则不起效
		RandomAccessFile acessFile = new RandomAccessFile(lockFile, "rw");
		FileChannel lockChannel = acessFile.getChannel();
		FileLock lock = lockChannel.tryLock();

		if (lock == null) {
			logger.error("Lock file existed, maybe another instance is running,system abort.\r\n"
					+ "If you are sure there is no running instance ,delete file " + lockFile);
			System.exit(1);
		} else {
			logger.info("Lock file check passed!");
		}

	}

	@Override
	protected void doStart() throws Exception {

		// 检查是否已经有启动的实例
		// 空数据库Init会导致退出，暂时不用
		if (getRunningMode() == RUNNING_MODE.NORMAL) {
			// Changed by jamie@2023/08/04
			// 只有正常模式下检查,维护模式(init时)不检查
			checkRunning();
		} else {
			logger.info("Running check is ignored since current running mode is " + getRunningMode());
		}
		//
		checkTempPath();
		//
		super.doStart();
		//
		// try {
		// Provider provider = ProviderUtil.initProvider();
		// logger.info("Security provider is initialized to " + provider);
		// } catch (Exception e) {
		// logger.error("Init security provider failed", e);
		// }
		// 记录下bootstrap classloader
		// logger.info(""+Thread.currentThread().getContextClassLoader());
		//
		addonLoaderSupport = new AddonLoaderSupport(this, Thread.currentThread().getContextClassLoader());
		//
		logger.info("Server " + this + " is starting,version:" + ServerUtil.getVersion(this));
		// 查找到所有服务
		List<Document> list = find(null);
		// 加载
		list.forEach((doc) -> {
			//
			ServiceLoadUtil.loadService(this, serviceMap, doc, logger);
		});
		//
		logger.info("Start handling i18n convert");
		// 试图i18n - 加载时I18nService还不存在无法实现
		// 由于i18n也会查找服务,所以不能修改serviceMap
		I18nService s = ServerLocator.getServer().getService(I18nService.class);
		if (s != null) {
			// s==null可能是初始化的时候
			Map<String, ServiceWrap> serviceMapTemp = new HashMap<>();
			// 得到缺省语言,避免多次获取引起的性能下降
			String defaultLang = s.defaultLang();
			for (String key : serviceMap.keySet()) {
				ServiceWrap sw = serviceMap.get(key);
				Document d = covertI18n(s, sw.getDoc(), defaultLang);
				serviceMapTemp.put(key, new ServiceWrap(d, sw.getService()));
				// logger.info("Handling i18n convert:"+key);
			}
			serviceMap = serviceMapTemp;
		}

		// 如果是初始化模式，部分服务不需要启动,否则可能发生启动时报错
		logger.info("Server running mode:" + runningMode);
		// 试图启动所有服务
		// ServiceLoadUtil.tryStartAllService(serviceMap,logger);
		// 试图启动服务
		logger.info("Start all system services");
		ServiceLoadUtil.tryStartServices(ServiceLoadUtil.splitServiceMap(serviceMap, CATEGORY.SYS), logger);
		// 试图启动user
		logger.info("Start all user services");
		ServiceLoadUtil.tryStartServices(ServiceLoadUtil.splitServiceMap(serviceMap, CATEGORY.USER), logger);
	}

	private Document covertI18n(I18nService s, Document doc, String locale) { //

		if (s != null) {
			try {
				Document d = s.convert(doc, locale);
				if (d != null) {
					return d;
				}
			} catch (Exception e) {
				// 如果转换失败,不报错而是记录错误
				logger.error("Convert i18n failed with default language:" + doc.toJson(), e);

			}
		}
		//
		return doc;
	}

	// 初始化模式下,部分服务不启动
	// private void handleInitMode() {
	// if (!initMode) {
	// logger.info("Server is not running under init mode");
	// return;
	// } else {
	// logger.info("Server is running under init mode");
	// }
	// //
	//
	// for (String service : serviceNotStartInitMode) {
	// logger.info("Under init mode, service [" + service + "] is temporarily set to
	// autostart=false");
	// ServiceWrap sw = serviceMap.get(service);
	// if (sw == null) {
	// logger.error("No service wrap is found by :" + service);
	// } else {
	// sw.getDoc().append("autoStart", false);
	// }
	// }
	// }

	@Override
	protected void doStop() throws Exception {
		// 试图停止所有服务
		// ServiceLoadUtil.tryStopAllService(serviceMap,true,logger);
		logger.info("Try to top all user services");
		ServiceLoadUtil.tryStopAllService(ServiceLoadUtil.splitServiceMap(serviceMap, CATEGORY.USER), true, logger);
		logger.info("All user services are stopped");

		//
		int second = 5;
		logger.info("Wait " + second + " seonds for user services to stop");
		Thread.sleep(second * 1000);

		//
		logger.info("Try to top all system services");
		ServiceLoadUtil.tryStopAllService(ServiceLoadUtil.splitServiceMap(serviceMap, CATEGORY.SYS), true, logger);
		logger.info("All system services are stopped");
		//
		super.doStop();
	}

	@Override
	public Service getService(String serviceId) {
		//
		ServiceWrap sw = serviceMap.get(serviceId);
		if (sw == null) {
			// 记录warning
			logger.warn("No service is found by id:" + serviceId);
			//
			return null;
		}
		//
		return sw.getService();
	}

	// private Service getService(Document doc) {
	// for(Document d:serviceMap.keySet()) {
	// if (MongoUtil.getId(d).equals(MongoUtil.getId(doc))) {
	// //logger.info("FOUND"+doc);
	// return serviceMap.get(d);
	// }
	// }
	// //logger.info("NOT FOUND"+doc);
	// //
	// return null;
	// }
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service> T getService(Class<T> serviceClass) {
		Iterator<ServiceWrap> iterator = serviceMap.values().iterator();
		ServiceWrap sw = null;
		while (iterator.hasNext()) {
			sw = iterator.next();
			//
			if (serviceClass.isInstance(sw.getService())) {
				return (T) (sw.getService());
			}
		}
		// 记录warning
		logger.warn("No service is found by service class:" + serviceClass);
		//
		return null;
	}

	@Override
	public List<Service> listServices(CATEGORY category) {
		List<Service> list = new ArrayList<Service>(serviceMap.size());
		for (ServiceWrap sw : serviceMap.values()) {
			if (category == null || sw.getService().getCategory().equals(category)) {
				list.add(sw.getService());
			}
		}
		return list;
	}

	@Override
	public void startService(String serviceId) {
		Service service = getService(serviceId);
		if (service == null) {
			throw new RuntimeException("No such service:" + serviceId);
		}
		if (service.getStatus() == LifeCycle.Status.RUNNING) {
			throw new RuntimeException("Service is running:" + serviceId);
		}
		// Document doc = ServiceLoadUtil.findServiceDoc(serviceMap, serviceId);
		if (!ServiceLoadUtil.allowToStart(serviceMap, serviceId)) {
			throw new RuntimeException("Service is not allow to start because of depency issue:" + serviceId);
		}
		//
		service.start();
		//

	}

	@Override
	public void stopService(String serviceId) {
		Service service = getService(serviceId);
		if (service == null) {
			throw new RuntimeException("No such service:" + serviceId);
		}
		if (service.getStatus() != LifeCycle.Status.RUNNING) {
			throw new RuntimeException("Service is not running:" + serviceId);
		}
		// Document doc = ServiceLoadUtil.findServiceDoc(serviceMap, serviceId);
		if (!ServiceLoadUtil.allowToStop(serviceMap, serviceId)) {
			throw new RuntimeException("Service is not allow to stop because of depency issue:" + serviceId);
		}
		//
		service.stop();
	}

	@Override
	public void postInsert(Document doc) {
		//
		if (getRunningMode() != RUNNING_MODE.NORMAL) {
			// 维护模式不能执行此类操作,会导致不允许启动的服务被启动
			return;
		}
		//
		// System.out.println("**"+doc.getBoolean("autoStart", true)
		// +"!!!"+startInitMode(doc));
		// System.out.println("POST INSERT:"+doc);
		// Remove && startInitMode(doc) by Jamie @2021/8/24
		// 会导致修改配置后没有正确启动，如dispatchService
		if (doc.getBoolean("autoStart", true)) {
			// 此时serviceMap里没有此service,需要创建并加入
			// System.out.println("1111:"+serviceMap.size());
			ServiceLoadUtil.loadService(this, serviceMap, doc, logger);
			// System.out.println("2222:"+serviceMap.size());
			//
			// if (ServiceLoadUtil.allowToStart(serviceMap, document)) {
			ServiceLoadUtil.startService(serviceMap, doc.getString("key"), logger);
			// } else {
			// 无法启动
			// logger.error("Service is not allowed to start because of the dependency
			// issue:" + document);
			// }
		}
	}

	// private boolean startInitMode(Document doc) {
	// for (String s : serviceNotStartInitMode) {
	// if (s.equals(doc.getString("key"))) {
	// return false;
	// }
	// }
	// //
	// return true;
	// }

	@Override
	public void postDelete(Document document) {
		//
		if (getRunningMode() != RUNNING_MODE.NORMAL) {
			// 维护模式不能执行此类操作,会导致不允许启动的服务被启动
			return;
		}
		//
		// System.out.println("in del doc:"+document);
		// System.out.println("in del serviceMap:"+serviceMap);
		//
		if (!ServiceLoadUtil.isServiceRunning(serviceMap, document.getString("key"))) {
			return;
		}
		// if (ServiceLoadUtil.allowToStop(serviceMap, document)) {
		ServiceLoadUtil.stopService(serviceMap, document.getString("key"), logger);
		// } else {
		// 无法停止
		// logger.error("Service is not allowed to stop because of the dependency
		// issue:" + document);
		// }

	}

	@Override
	public void postUpdate(Document originalDocument, Document document, boolean replace) {
		postDelete(originalDocument);
		postInsert(document);
	}

	@Override
	public String toString() {
		return getSetting(INSTANCE_ID, String.class);
	}

	// 确保temp目录存在
	private void checkTempPath() {
		// 确保temp目录存在
		String pathTemp = this.getSetting(PATH_TEMP, String.class);
		if (StringUtil.isEmpty(pathTemp)) {
			return;
		}
		//
		File file = new File(pathTemp);
		if (!file.exists()) {
			FileHelper.createDir(file);
		} else {
			try {
				FileHelper.clearDir(pathTemp, true);
			} catch (IOException ignore) {
				// 忽略错误
			}
		}
	}
}
