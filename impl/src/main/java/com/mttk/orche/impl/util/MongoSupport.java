package com.mttk.orche.impl.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mttk.orche.core.impl.util.MongoBuildUtil;

public class MongoSupport {
	private static Logger logger = LoggerFactory.getLogger(MongoSupport.class);
	private MongoClient mongoClient = null;
	private MongoDatabase mongoDatabase = null;

	public MongoSupport(String serverHome) {
		init(serverHome);
	}

	public MongoClient getMongoClient() {
		return mongoClient;
	}

	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}

	public void close() throws IOException {
		mongoClient.close();
	}

	//
	private void init(String serverHome) {
		String configFile = null;
		try {
			configFile = serverHome + File.separator + "conf" + File.separator + "mongo.properties";
			Properties props = MongoBuildUtil.loadProps(configFile);
			mongoClient = MongoBuildUtil.build(props);
			logger.info("Successfully create mongo client from " + configFile);
			//
			String databaseName = props.getProperty("database", "api");

			mongoDatabase = mongoClient.getDatabase(databaseName);
			logger.info("Successfully create mongo database with name  " + databaseName);
		} catch (Exception e) {
			logger.error("Fail to  create mongo client from " + configFile, e);
			// If mongoclient can not be build, no further process can be executed so raise
			// exception
			throw new RuntimeException(e);
		}
	}
}
