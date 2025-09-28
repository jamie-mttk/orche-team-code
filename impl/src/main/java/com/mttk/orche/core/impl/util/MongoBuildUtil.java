package com.mttk.orche.core.impl.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mongodb.client.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mttk.orche.util.IOUtil;
import com.mttk.orche.util.StringUtil;

public class MongoBuildUtil {
	// public static MongoClient build(String configFile) throws Exception{
	// Properties props=loadProps(configFile);
	// return build(props);
	// }
	public static MongoClient build(Properties props) throws Exception {
		// Load from properties

		// ֤
		// Assert.isTrue(StringUtil.notEmpty(props.getProperty("auth.userName")),
		// "Mongodb username is null");
		// Assert.notNull(StringUtil.notEmpty(props.getProperty("auth.password")),
		// "Mongodb password is null");
		// Assert.notNull(StringUtil.notEmpty(props.getProperty("auth.database")),
		// "Mongodb authtication databse is null");
		MongoCredential credential = null;
		if (StringUtil.notEmpty(props.getProperty("auth.userName"))
				&& StringUtil.notEmpty(props.getProperty("auth.database"))
				&& StringUtil.notEmpty(props.getProperty("auth.password"))) {
			credential = MongoCredential.createCredential(props.getProperty("auth.userName"),
					props.getProperty("auth.database"), props.getProperty("auth.password").toCharArray());
		}
		//
		String servers = props.getProperty("servers");
		if (StringUtil.isEmpty(servers)) {
			servers = "localhost:27107";
		}
		String[] serverArray = servers.split(",");
		List<ServerAddress> addresses = new ArrayList<ServerAddress>();
		for (String server : serverArray) {
			ServerAddress serverAddress = null;
			String parts[] = server.split(":");
			if (parts.length == 1) {
				serverAddress = new ServerAddress(parts[0]);
			} else if (parts.length == 2) {
				serverAddress = new ServerAddress(parts[0], Integer.parseInt(parts[1]));
			} else {
				throw new RuntimeException("Invalid server list:" + servers);
			}
			addresses.add(serverAddress);
		}

		//
		// 简化版本：直接创建MongoClient，不使用复杂的设置
		// 新版本MongoDB驱动的API变化较大，暂时使用基本连接方式
		MongoClient mongoClient = null;

		// 构建连接字符串
		StringBuilder connectionString = new StringBuilder("mongodb://");
		if (credential != null) {
			connectionString.append(credential.getUserName())
					.append(":")
					.append(new String(credential.getPassword()))
					.append("@");
		}

		// 添加服务器地址
		for (int i = 0; i < addresses.size(); i++) {
			if (i > 0)
				connectionString.append(",");
			ServerAddress addr = addresses.get(i);
			connectionString.append(addr.getHost());
			if (addr.getPort() != ServerAddress.defaultPort()) {
				connectionString.append(":").append(addr.getPort());
			}
		}

		// 添加认证数据库
		if (credential != null) {
			connectionString.append("/").append(credential.getSource());
		}

		// 使用连接字符串创建MongoClient
		mongoClient = MongoClients.create(connectionString.toString());
		//

		//
		return mongoClient;
	}

	public static Properties loadProps(String file) throws Exception {
		Properties props = new Properties();
		InputStream is = new FileInputStream(file);
		try {
			props.load(is);
			return props;
		} finally {
			IOUtil.safeClose(is);
		}
	}
}
