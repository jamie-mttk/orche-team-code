package com.mttk.orche.impl.util;

import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

public class OshiUtil {
	//使用InetAddress.getLocalHost().getHostName()会得到localhost
	//在java.net.preferIPv4Stack=true时
	//使用Oshi的方法不会
	public static String getHostName() {
		  SystemInfo si = new SystemInfo();

	        OperatingSystem os = si.getOperatingSystem();
	        return os.getNetworkParams().getHostName();
	}
}
