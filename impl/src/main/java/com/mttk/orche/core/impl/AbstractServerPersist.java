package com.mttk.orche.core.impl;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mttk.orche.impl.util.MongoSupport;
import com.mttk.orche.support.ServerUtil;

public abstract class AbstractServerPersist extends AbstractServerBase {
	private Logger logger = LoggerFactory.getLogger(AbstractServerPersist.class);
	private MongoClient mongoClient = null;
	public MongoDatabase mongoDatabase = null;

	protected void init(String homePath) throws Exception {
		super.init(homePath);
	}

	@Override
	// 设置一个特别的名称
	protected String getCollectionName() {
		return "sysService";
	}

	/*******************************************************************
	 * 这部分是与基础存储(MongoDB)相关部分
	 *****************************************************************/
	@Override
	public MongoClient obtainMongoClient() {
		return mongoClient;
	}

	@Override
	public MongoDatabase obtainMongoDatabase() {
		return mongoDatabase;
	}

	@Override
	public MongoCollection<Document> obtainCollection(String collectionName) {
		return obtainMongoDatabase().getCollection(collectionName);
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		//
		MongoSupport mongoSupport = new MongoSupport(ServerUtil.getPathHome(server));
		mongoClient = mongoSupport.getMongoClient();
		mongoDatabase = mongoSupport.getMongoDatabase();

	}

	@Override
	protected void doStop() throws Exception {
		if (mongoClient != null) {
			try {
				mongoClient.close();
				mongoClient = null;
				logger.info("MongoClient is closed!");
			} catch (Exception e) {
				logger.error("MongoClient closed failed", e);
			}
			//
			super.doStop();
		}
	}
}
