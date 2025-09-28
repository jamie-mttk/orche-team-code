package com.mttk.orche.http;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.core.impl.AbstractCacheWrap;

public class JettyServerWrap extends AbstractCacheWrap {
	private static Logger logger=LoggerFactory.getLogger(JettyServerWrap.class);
	private Server server;
	private AdapterConfig config;
	public JettyServerWrap(Server server,AdapterConfig config){
		this.server=server;		
		this.config=config;
		//
		setId(config.getId());
		setKey(config.getId());
	}
	public Server getServer() {
		return server;
	}

//	private void checkAvailable(String className) {
//	logger.error("LOADER:"+Thread.currentThread().getContextClassLoader());
//	try {
//		Class.forName(className);
//		logger.info("Class is OK:"+className);
//	}catch(Exception e) {
//		logger.error("Class is not OK:"+className,e);
//	}
//	//
//	ClassLoader loader=null;
//	try {
//		 loader=Thread.currentThread().getContextClassLoader();
//        Class cc= (loader==null ) ? Class.forName(className) : loader.loadClass(className);
//        logger.info("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOK");
//	}catch(Exception e) {
//		logger.error("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEERROR:"+loader+"==>"+className,e);
//	}
//}
	
	@Override
	public void start() throws Exception {
		// 保存当前线程的 ClassLoader
		ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
		
		try {
			// 设置当前线程的 ClassLoader 为系统 ClassLoader（包含所有 jetty 依赖）
			ClassLoader systemClassLoader = this.getClass().getClassLoader();
			Thread.currentThread().setContextClassLoader(systemClassLoader);
			
			// logger.info("JettyServerWrap start - Original ClassLoader: " + originalClassLoader);
			// logger.info("JettyServerWrap start - System ClassLoader: " + systemClassLoader);
			// logger.info("JettyServerWrap start - Server ClassLoader: " + server.getClass().getClassLoader());
			
			logger.info("Starting Jetty server: " + server);
			server.start();
			logger.info("Started Jetty server: " + server);
			
		} finally {
			// 恢复原始 ClassLoader
			Thread.currentThread().setContextClassLoader(originalClassLoader);
		}
	}
	@Override
	public void stop() throws Exception {
		logger.info("Start stop Jetty server:"+server);
		try {
			server.stop();
			server.destroy();
			logger.info("Stopped Jetty server:"+server);
		}catch(Exception e) {
			logger.error("Stop Jetty server failed:"+server,e);
		}
		
	}
}
