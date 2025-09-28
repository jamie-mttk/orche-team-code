package com.mttk.orche.support;

import java.io.File;

import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.core.Service;

public class ServerUtil {

	public static String getPathHome(Server server) {
		return server.getSetting(Server.PATH_HOME, String.class);
	}

	public static String getPathTemp(Server server) {
		return server.getSetting(Server.PATH_TEMP, String.class);
	}

	public static String getPathData(Server server) {
		return server.getSetting(Server.PATH_HOME, String.class) + File.separator + "data";
	}

	public static String getPathConf(Server server) {
		return server.getSetting(Server.PATH_CONF, String.class);
	}

	public static Boolean getClusterMode(Server server) {
		return server.getSetting(Server.CLUSTER_MODE, Boolean.class);
	}

	public static String getInstanceId(Server server) {
		return server.getSetting(Server.INSTANCE_ID, String.class);
	}

	public static String getInstanceIdLast(Server server) {
		return server.getSetting(Server.INSTANCE_ID_LAST, String.class);
	}

	public static String getInstanceName(Server server) {
		return server.getSetting(Server.INSTANCE_NAME, String.class);
	}

	public static String getInstanceIP(Server server) {
		return server.getSetting(Server.INSTANCE_IP, String.class);
	}

	public static String getVersion(Server server) {
		return server.getSetting(Server.VERSION, String.class);
	}

	//
	public static <T extends Service> T getService(Class<T> serviceClass) {
		return ServerLocator.getServer().getService(serviceClass);
	}
}
