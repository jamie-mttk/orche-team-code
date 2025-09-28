package com.mttk.orche.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.core.LifeCycle;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.Server.RUNNING_MODE;
import com.mttk.orche.core.Service.CATEGORY;
import com.mttk.orche.core.impl.StdServer;
import com.mttk.orche.impl.util.InitUtil;
import com.mttk.orche.init.Initialization;
import com.mttk.orche.util.IOUtil;

/** This class assume the class loader is ready */

public class StartServer {
	private Logger logger = null;;
	private Server server = null;

	// 0 - 正常启动
	// 1 - 启动后初始化系统
	public void process(String serverHome, Map<String, String> config) {
		boolean initMode = "init".equals(config.get("operate"));
		//
		InitUtil.init(serverHome);
		logger = LoggerFactory.getLogger(StartServer.class);
		//
		logger.info("Start config:" + config);
		//
		server = loadServer(serverHome, initMode);
		if (server == null) {
			return;
		}
		//
		// String[] serviceArray=server.listService();
		// logger.info(server.listService().length +" services are loaded.");
		// for (int i=0;i<serviceArray.length;i++){
		// logger.debug("Service "+i+":"+serviceArray[i]);
		// }
		// init
		long startTime = System.currentTimeMillis();
		// start
		logger.info("Start server");
		try {
			server.start();
		} catch (Throwable t) {
			logger.error("Server failed to start", t);
			return;
		}
		// Add shutdown monitor
		addShutdownMonitor(serverHome);
		logger.info("Shutdown monitor is added.");
		// Add shutdown hook
		ShutdownHook shutdownHook = new ShutdownHook(this);
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		logger.info("Shutdown hook is added:" + shutdownHook);
		//

		logger.info("Server started successfully in " + (System.currentTimeMillis() - startTime) + " ms");
		// 检查是否init过
		if (server.listServices(CATEGORY.SYS).size() == 0) {
			// 没有初始化过,强制初始化
			initMode = true;
			config.put("autoStop", "false");
			logger.info("No init, force init");
		}
		// Wait for exit
		if (initMode) {
			new Initialization().process(server, config);
		}
	}

	// //Initialize logger
	// private void initLogger(String serverHome){
	//
	// File logConfig=new
	// File(serverHome+File.separator+"conf"+File.separator+"log4j2.xml");
	// //DOMConfigurator.configure(logConfig);
	// //
	// //
	// if (logConfig.exists()) {
	// try {
	// ConfigurationSource source = new ConfigurationSource(new
	// FileInputStream(logConfig), logConfig);
	// Configurator.initialize(null, source);
	// //
	// logger=LoggerFactory.getLogger(StartServer.class);
	// //
	// logger.info("Log4J logger initialzied from "+logConfig);
	// }catch(Throwable t) {
	// System.err.println("Fail to init log4j2 from :"+logConfig);
	// t.printStackTrace();
	// }
	// }else {
	// System.err.println("Log4j2 config file is not exiested:"+logConfig);
	// }
	// }
	private Server loadServer(String serverHome, boolean initMode) {
		try {
			//
			String propFile = serverHome + File.separator + "system.properties";
			// load chub.properties
			File file = new File(propFile);
			if (file.exists() && file.isFile()) {
				Properties props = new Properties();
				InputStream is = new FileInputStream(file);
				try {
					props.load(is);
				} finally {
					IOUtil.safeClose(is);
				}
				//
				props.putAll(System.getProperties());
				System.setProperties(props);
				logger.info("Load server propertis from from " + propFile);
			}
			//
			StdServer server = StdServer.getInstance();
			if (initMode) {
				server.setRunningMode(RUNNING_MODE.MAINTANCE);
			} else {
				server.setRunningMode(RUNNING_MODE.NORMAL);
			}
			server.init(serverHome);
			logger.info("Server is loaded:" + server);
			return server;
		} catch (Throwable t) {
			// t.printStackTrace();
			logger.error("Server failed to load", t);
			return null;
		}
	}

	public void doStopIfNeccessary() {
		if (server.getStatus() == LifeCycle.Status.RUNNING) {
			doStop();
		}
	}

	public void doStop() {
		logger.info("Stop server");
		try {
			server.stop();
		} catch (Throwable t) {
			logger.error("Server failed to stop", t);
		}
		logger.info("Server stopped.");
		// Show thread
		// for(int i=0;i<5;i++) {
		// try {
		// Thread.sleep(1*1000);
		// }catch(Exception e ) {}
		showThreads();
		// }

	}

	private void showThreads() {
		Map<ThreadGroup, List<Thread>> map = obtainAll();
		Map<Thread, StackTraceElement[]> threadTraceMap = Thread.getAllStackTraces();
		for (ThreadGroup threadGroup : map.keySet()) {
			if (threadGroup == null) {
				continue;
			}
			List<Thread> list = map.get(threadGroup);
			if (list == null) {
				continue;
			}
			logger.info(("Thread group:" + threadGroup.getName()));
			for (Thread thread : list) {
				logger.info(("\t" + thread.getName()));
				StackTraceElement[] trace = threadTraceMap.get(thread);
				if (trace == null) {
					continue;
				}
				for (int i = 0; i < trace.length; i++) {
					logger.info(("\t\t" + trace[i]));
				}
			}
		}

	}

	private Map<ThreadGroup, List<Thread>> obtainAll() {
		Map<ThreadGroup, List<Thread>> returnMap = new HashMap<ThreadGroup, List<Thread>>();
		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		for (Thread thread : map.keySet()) {
			ThreadGroup threadGroup = thread.getThreadGroup();
			List<Thread> list = returnMap.get(threadGroup);
			if (list == null) {
				list = new ArrayList<Thread>();
				returnMap.put(threadGroup, list);
			}
			//
			list.add(thread);
		}
		//
		return returnMap;
	}

	private class ShutdownHook extends Thread {
		private StartServer startServer = null;

		public ShutdownHook(StartServer startServer) {
			this.startServer = startServer;
		}

		@Override
		public void run() {
			logger.info("Shutdown hook is called,stopped");
			startServer.doStopIfNeccessary();
		}
	}

	public void addShutdownMonitor(String serverHome) {
		final File stopFile = new File(serverHome + File.separator + "data" + File.separator + "STOPME");
		if (stopFile.exists()) {
			stopFile.delete();
		}
		final Timer timer = new Timer("Cloud hub Shutdown Monitor", false);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (stopFile.exists()) {
					// try
					// {
					logger.info("Stop file [" + stopFile + "] is found,start stopping!");
					//
					doStop();
					timer.cancel();
					// }
					// finally
					// {
					// Runtime.getRuntime().exit(0);
					// }
				}
			}
		}, 500, 500);
	}
}