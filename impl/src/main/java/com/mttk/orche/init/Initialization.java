package com.mttk.orche.init;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mttk.orche.core.Server;
import com.mttk.orche.deploy.DeployServiceImpl;
import com.mttk.orche.service.DeployService;
import com.mttk.orche.service.SystemConfigService;
import com.mttk.orche.service.support.DeployStrategy;

import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.FileHelper;
import com.mttk.orche.util.IOUtil;
import com.mttk.orche.util.StringUtil;

public class Initialization {
	private static Logger logger = LoggerFactory.getLogger(Initialization.class);

	public void process(Server server, Map<String, String> config) {

		checkFolder(server);
		// logger.info("##########1:"+server.getService("httpEntryService"));
		// applyDatabase(server,operate==2);
		// 当前部分使用force模式,因为这些操作不会造成任何问题
		applyDatabase(server, null);
		//
		deployAll(server);
		// logger.info("##########2:"+server.getService("httpEntryService"));
		try {
			applyGlobalConfig(server, config);
		} catch (Exception e) {
			logger.error("Apply solution auth failed", e);
		}

		// 是否自动停止
		String autoStop = config.getOrDefault("autoStop", "true");
		if ("true".equalsIgnoreCase(autoStop)) {
			//
			logger.info("Finish initialize, server will be stopped after 5 seconds.");
			// apply service setting

			//
			try {
				Thread.sleep(5 * 1000);
			} catch (Exception e) {

			}
			// Stop scheduler - 因为调用scheduler的schedule方法会初始化,如果不关闭会导致进程无法关闭
			// SchedulerService shedulerService=server.getService(SchedulerService.class);
			// if (shedulerService!=null) {
			// logger.info("Try to stop scheduler service manually(Start first).");
			// try {
			// shedulerService.start();
			// shedulerService.stop();
			// logger.info("Successfully stop scheduler service manually.");
			// }catch(Exception e) {
			// logger.warn("Stop scheduler service failed",e);
			// }
			// }

			// Stop server -注意调用server.stop关闭不掉,因为还有一个shutdown monitor
			final File stopFile = new File(
					ServerUtil.getPathHome(server) + File.separator + "data" + File.separator + "STOPME");
			try {
				FileHelper.writeFile("STOP".getBytes(), stopFile, false);
			} catch (Exception e) {
				logger.error("Write stop file failed:" + stopFile, e);
			}
		} else {
			logger.info("Server finish initilization!");
		}
		//
		// try(Scanner scanner=new Scanner(System.in)){
		// logger.info("Finish initialize, please enter key to stop server.");
		// scanner.next();
		// //
		// server.stop();
		// }
	}

	// 部署impl.jar和所有addon
	private void deployAll(Server server) {
		String libPath = ServerUtil.getPathHome(server) + File.separator + "lib" + File.separator;
		DeployService deployService = server.getService(DeployService.class);
		// deploy service可能为空,因为数据库是空的
		boolean manualCreate = false;
		if (deployService == null) {
			deployService = new DeployServiceImpl();
			deployService.setServer(server);
			deployService.start();
			manualCreate = true;
			logger.info("No deploy service is get from Server,created manually!");
		}
		// impl - impl must be deployed first
		deploy(deployService, new File(libPath + "sys" + File.separator + "impl.jar"));
		//
		//
		logger.info("Server will be restart to reload all system services");
		server.stop();
		server.start();

		// 不是addon以及子目录下的所有包
		deployFolder(deployService, new File(libPath + File.separator + "addon"));
		deployFolder(deployService,
				new File(ServerUtil.getPathHome(server) + File.separator + "lib_user" + File.separator + "addon"));
		//
		if (manualCreate) {
			deployService.stop();
		}
		//
		logger.info("Server is restarted to reload all system and user services");
		server.stop();
		server.start();

	}

	// 不是此目录以及下面所有的子目录
	private void deployFolder(DeployService deployService, File folder) {
		if (folder == null || folder.listFiles() == null) {
			return;
		}
		logger.info("Deploy folder:" + folder);
		for (File file : folder.listFiles()) {

			if (file.isFile()) {
				deploy(deployService, file);
			} else if (file.isDirectory()) {
				deployFolder(deployService, file);
			}
		}
	}

	private Document deploy(DeployService deployService, File file) {
		try {
			DeployStrategy strategy = new DeployStrategy();
			strategy.setInitMode(true);

			//
			Document doc = deployService.deploy(file, file.getName(), null, strategy);
			// 检查是否有错误
			List<String> throwables = doc.getList("throwables", String.class);
			if (throwables == null || throwables.size() == 0) {
				logger.info("Successfully deploy file:" + file);
			} else {
				logger.warn("Successfully deploy file:" + file + " with below errors:");
				for (String t : throwables) {
					logger.warn(t);
				}
			}
			return doc;
		} catch (Exception e) {
			logger.error("Error deploy file:" + file, e);
			return null;
		}
	}

	private void applyDatabase(Server server, Boolean force) {
		// db下的文件
		applyDatabase(server, force == null ? true : force, "sysAgentTag", false);
		// applyDatabase(server, force == null ? false : force, "sysAuthentication",
		// true);// 如果force==null不更新密码
		// applyDatabase(server, force == null ? true : force, "sysChannel", false);
		// applyDatabase(server, force == null ? true : force, "sysConfigFolder",
		// false);
		// 增加索引
		server.obtainCollection("sysAgentExecuteLog").createIndex(Indexes.ascending("execute"));
		// server.obtainCollection("sysAudit").createIndex(Indexes.ascending("transaction"));
		// server.obtainCollection("sysAudit").createIndex(
		// Indexes.compoundIndex(Indexes.ascending("server", "status"),
		// Indexes.descending("retryTime")));
		// server.obtainCollection("sysEventData").createIndex(Indexes.descending("_insertTime"));
		// server.obtainCollection("sysAccessLog").createIndex(Indexes.descending("_insertTime"));

	}

	// removeLockFlag 仅仅针对authentication,如果修改去掉lockFlag
	private Document applyDatabase(Server server, boolean force, String name,
			boolean removeLockFlag) {
		String resource = "db/" + name + ".json";
		logger.info("Start apply data" + name);
		// 获取数据
		String data = null;
		try (InputStream is = getClass().getResourceAsStream(resource)) {
			data = new String(IOUtil.toArray(is), "utf-8");
		} catch (Exception e) {
			logger.error("Error load resource:" + resource, e);
			return null;
		}
		//
		MongoCollection<Document> col = server.obtainCollection(name);
		//
		Document doc = Document.parse(data);
		List<Document> list = (List<Document>) doc.get("data");
		if (list == null) {
			return null;
		}
		for (Document d : list) {

			Bson filter = Filters.eq("_id", d.get("_id"));
			// 得到已经存在的
			Document docExist = col.find(filter).first();
			if (docExist == null) {
				// insert
				col.insertOne(d);
			} else {
				if (force) {
					// replace
					col.replaceOne(filter, d);
				} else {
					if (removeLockFlag) {
						docExist.remove("lockFlag");
						col.replaceOne(filter, docExist);
					}

				}

			}
		}
		//
		logger.info("Finish apply data " + name + " ,count:" + list.size());
		//
		return doc;
	}

	// 检查需要的目录是否存在
	private void checkFolder(Server server) {
		// temp
		FileHelper.createDir(new File(server.getSetting(Server.PATH_TEMP, String.class)));
		// data
		FileHelper.createDir(new File(server.getSetting(Server.PATH_HOME, String.class) + File.separator + "data"));
		// log
		FileHelper.createDir(new File(server.getSetting(Server.PATH_HOME, String.class) + File.separator + "log"));
	}

	// 安装AuthSolution实例
	private Document applyGlobalConfig(Server server, Map<String, String> config) {
		try {
			SystemConfigService s = server.getService(SystemConfigService.class);

			Document doc = s.obtain();

			// 如果用户提供了端口则修改
			String temp = config.get("httpPort");
			if (StringUtil.notEmpty(temp)) {
				doc.append("httpPort", Integer.parseInt(temp));
				logger.info("Web port is changed to " + temp);
			}
			//
			s.save(doc);
			logger.info("Successfully apply solution auth");
			return doc;
		} catch (Exception e) {
			logger.error("Error apply solution auth", e);
			return null;
		}
	}
}
