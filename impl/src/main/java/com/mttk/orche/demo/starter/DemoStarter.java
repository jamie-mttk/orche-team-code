package com.mttk.orche.demo.starter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;

import com.mttk.orche.startup.StartServer;
import com.mttk.orche.support.MongoUtil;

public class DemoStarter {

	public static void main(String[] args) throws Exception {
		String serverHome = "D:\\biz\\development\\abic\\work";
		System.setProperty("server.home", serverHome);
		//
		StartServer startServer = new StartServer();
		Map<String, String> config = new HashMap<>();
		startServer.process(serverHome, config);

	}


}
