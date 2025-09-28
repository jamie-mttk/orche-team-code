package com.mttk.orche.core.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.core.Server;
import com.mttk.orche.impl.util.InitUtil;
import com.mttk.orche.impl.util.OshiUtil;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.FileHelper;
import com.mttk.orche.util.NetworkIpUtil;
import com.mttk.orche.util.StringUtil;

//实现了基本的Server设置
public abstract class AbstractServerBase extends AbstractPersistWithListenerService implements Server {
	private static final String PROP_PREFIX = "server.";
	private Logger logger = LoggerFactory.getLogger(AbstractServerBase.class);
	private Map<String, Object> settings = new HashMap<String, Object>();

	// 初始化
	protected void init(String homePath) throws Exception {
		settings.put(Server.TIME_START, new Date());
		settings.put(Server.VERSION, "5.8.04");
		//
		settings.put(Server.PATH_HOME, homePath);
		settings.put(Server.PATH_TEMP, homePath + File.separator + "temp");
		settings.put(Server.PATH_CONF, homePath + File.separator + "conf");
		// 试图从配置文件读取服务器配置
		//
		settings.put(Server.CLUSTER_MODE,
				Boolean.valueOf(InitUtil.safeGetSystemProperty(PROP_PREFIX + Server.CLUSTER_MODE, "FALSE")));
		// 获取last instance id
		settings.put(Server.INSTANCE_ID_LAST, loadInstanceIdLast());
		// Instance id
		String instanceId = InitUtil.safeGetSystemProperty(PROP_PREFIX + Server.INSTANCE_ID);
		if (instanceId != null) {
			instanceId = instanceId.trim();
		}
		if (StringUtil.isEmpty(instanceId)) {
			instanceId = OshiUtil.getHostName();
		}
		settings.put(Server.INSTANCE_ID, instanceId);
		//
		saveInstanceIdLast(instanceId);
//		//测试生成不同的temp
//		logger.info("@@@@@@@@@:"+
//		homePath+File.separator+"temp"+File.separator+instanceId);

		// instance name
		// System.out.println("@@@@@@"+NetworkUtil.getHostName());
		settings.put(Server.INSTANCE_NAME, OshiUtil.getHostName());
		// instance ip
		String instanceIp = InitUtil.safeGetSystemProperty(PROP_PREFIX + Server.INSTANCE_IP);
		if (instanceIp != null) {
			instanceIp = instanceIp.trim();
		}
		if (StringUtil.isEmpty(instanceIp)) {
			instanceIp = NetworkIpUtil.getLocalHostLANAddress(true).getHostAddress();
		}
		settings.put(Server.INSTANCE_IP, instanceIp);
		// instance port
		String instancePort = InitUtil.safeGetSystemProperty(PROP_PREFIX + Server.INSTANCE_PORT);
		int port = 0;
		if (StringUtil.notEmpty(instancePort)) {
			port = Integer.parseInt(instancePort);
		} else {
			// 注意没有设置时port=0,代表没有设置
		}
		//
		settings.put(Server.INSTANCE_PORT, port);
		//
		logger.info("Init server with settings:" + settings);
	}

	/*******************************************************************
	 * 这部分是与服务变量相关的部分
	 *****************************************************************/
	@Override
	public <T> T getSetting(String key, Class<T> retClass) {
		Object value = settings.get(key);
		if (value != null && retClass.isInstance(value)) {
			return retClass.cast(value);
		} else {
			return null;
		}
	}

	private String loadInstanceIdLast() throws IOException {
		File file = getInstanceIdLastFile();
		if (!file.exists()) {
			return null;
		}
		return new String(FileHelper.readFile(file), "UTF-8");
	}

	private void saveInstanceIdLast(String instanceIdLast) throws IOException {
		File file = getInstanceIdLastFile();
		try {		
			FileHelper.createDir(file.getParentFile());
			FileHelper.writeFile(instanceIdLast.getBytes("UTF-8"), file, false);
		} catch (Exception e) {
			//出错了不报错,防止启动失败
			logger.error("Fail to save instance id last to "+file, e);
		}
	}

	private File getInstanceIdLastFile() {
		String path = ServerUtil.getPathConf(this) + File.separator + "instance_id_last";
		return new File(path);
	}

	@Override
	public String[] settingKeys() {
		return new String[] { PATH_HOME, PATH_CONF, PATH_TEMP, TIME_START, VERSION, CLUSTER_MODE, INSTANCE_ID,
				INSTANCE_ID_LAST, INSTANCE_NAME, INSTANCE_IP };
	}

}
