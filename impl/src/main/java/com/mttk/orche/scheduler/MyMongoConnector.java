package com.mttk.orche.scheduler;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.novemberain.quartz.mongodb.db.MongoConnector;

public class MyMongoConnector implements MongoConnector {
	private MongoDatabase mongoDatabase=null;
	public MyMongoConnector(MongoDatabase mongoDatabase) {
		this.mongoDatabase=mongoDatabase;
	}
	@Override
	public MongoCollection<Document> getCollection(String collectionName) {
		return mongoDatabase.getCollection(collectionName);
	}
	@Override
	public void close() {
		

	}

}
