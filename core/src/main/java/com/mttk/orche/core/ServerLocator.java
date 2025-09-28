package com.mttk.orche.core;

/**
 * 得到服务的对象实例
 * 不建议直接调用此方法，建议从Context获取
 *
 */
public class ServerLocator {
	//static Logger logger=LoggerFactory.getLogger(ServerLocator.class);
	private static Server server=null;
	public static Server getServer() {
		//logger.info("~~~~~~~~~~~~~~~~~~~~~"+server);
		if (server!=null) {
			return server;
		}
		throw new RuntimeException("No server is found,is it registered?");
	}
	public static void registServer(Server _server) {
		server=_server;	
	}
}
