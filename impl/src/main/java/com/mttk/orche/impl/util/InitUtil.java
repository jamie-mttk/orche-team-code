package com.mttk.orche.impl.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.util.PropertiesUtil;
import com.mttk.orche.util.StringUtil;

public class InitUtil {
	private static Logger logger = null;

	public static void init(String serverHome) {
		// System.out.println("################################");
		// System.out.println(System.getenv("server.port"));
		// System.out.println(System.getProperty("server.port"));
		// System.out.println("################################");
		initPropertyies(serverHome);
		initLogger(serverHome);

	}

	private static void initPropertyies(String serverHome) {
		try {
			// 把所有自定义环境变量加入到全局环境变量中
			Properties props = PropertiesUtil
					.load(serverHome + File.separator + "conf" + File.separator + "env.properties");
			// System.out.println("1:"+props);
			props.putAll(System.getenv());
			// System.out.println("2:"+props);
			props.putAll(System.getProperties());
			// System.out.println("3:"+props);
			System.setProperties(props);
			// 检查是否设置了server.instance.id,如果没有设置手工设置,否则日志文件名会异常
			String instanceId = safeGetSystemProperty("server.instance.id");
			if (StringUtil.isEmpty(instanceId)) {
				// 这里不能用OshiUtil.getHostName(),会导致提前初始化log4j2
				instanceId = InetAddress.getLocalHost().getHostName();
			}
			//
			instanceId = instanceId.trim();
			System.setProperty("server.instance.id", instanceId = instanceId.trim());
		} catch (Exception e) {
			System.err.println("Fail to load server properties file,using default");
			e.printStackTrace();
		}
	}

	// Initialize logger
	private static void initLogger(String serverHome) {
		//
		File logConfig = new File(serverHome + File.separator + "conf" + File.separator + "log4j2.xml");
		// DOMConfigurator.configure(logConfig);
		//
		//
		if (logConfig.exists()) {
			try {
				ConfigurationSource source = new ConfigurationSource(new FileInputStream(logConfig), logConfig);
				Configurator.initialize(null, source);
				//
				logger = LoggerFactory.getLogger(InitUtil.class);
				//
				logger.info("Log4J logger initialzied from " + logConfig);
			} catch (Throwable t) {
				System.err.println("Fail to init log4j2 from :" + logConfig);
				t.printStackTrace();
			}
		} else {
			System.err.println("Log4j2 config file is not exiested:" + logConfig);

		}
	}

	// 安全得到环境变量中指定key的值
	// 注意如果是类似server.instance.ip的key会查找server.instance.ip以及server_instance_ip
	// 找不到返回null
	public static String safeGetSystemProperty(String key) {
		//
		String value = System.getProperty(key);
		if (StringUtil.notEmpty(value)) {
			return value;
		}
		// 查找下划线的变量
		value = System.getProperty(key.replace('.', '_'));
		if (StringUtil.notEmpty(value)) {
			return value;
		}
		//
		return null;
	}

	public static String safeGetSystemProperty(String key, String defaultValue) {
		String value = safeGetSystemProperty(key);
		if (StringUtil.isEmpty(value)) {
			return defaultValue;
		} else {
			return value;
		}
	}
}
