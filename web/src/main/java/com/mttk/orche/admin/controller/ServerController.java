package com.mttk.orche.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.admin.util.LocaleUtil;
import com.mttk.orche.admin.util.MemoryCapture;
import com.mttk.orche.admin.util.ServerInfoUtil;
import com.mttk.orche.admin.util.SysThreadUtil;
import com.mttk.orche.admin.util.controller.Pageable;
import com.mttk.orche.core.PersistService;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.core.Service;
import com.mttk.orche.init.Initialization;
import com.mttk.orche.service.SystemConfigService;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.IOUtil;
import com.mttk.orche.util.StringUtil;
import com.sun.management.HotSpotDiagnosticMXBean;

@RestController
@RequestMapping(value = "/server")

public class ServerController extends PersistableControllerBase {
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public PersistService findPersistService() {
		return ServerLocator.getServer();
	}

	@Override
	@GetMapping(value = "/query")
	public Document queryByCriteria(String criteria, Pageable pageable, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Document doc = super.queryByCriteria(criteria, pageable, request, response);
		//
		List<Document> list = (List<Document>) doc.get("list");
		if (list != null) {
			List<Document> listNew = new ArrayList<>(list.size());
			for (Document d : list) {
				fillSingle(d);
				// i8n
				listNew.add(LocaleUtil.handle(d, request));
			}
			//
			doc.append("list", listNew);
		}
		//
		return doc;
	}

	@PostMapping(value = "/start")
	public Document start(String key) throws Exception {
		Service s = ServerLocator.getServer().getService(key);
		if (s != null) {
			//
			s.start();
			//
			return new Document().append("success", true);
		} else {
			//
			return new Document().append("success", false);
		}
	}

	@PostMapping(value = "/stop")
	public Document stop(String key) throws Exception {
		Service s = ServerLocator.getServer().getService(key);
		if (s != null) {
			//
			s.stop();
			//
			return new Document().append("success", true);
		} else {
			//
			return new Document().append("success", false);
		}
	}

	@PostMapping(value = "/update")
	public Document update(@RequestBody Document doc, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String id = ServerLocator.getServer().update(doc);
		if (StringUtil.notEmpty(id)) {
			return findPersistService().load(id).orElse(null);
		} else {
			return null;
		}
	}

	private void fillSingle(Document d) {
		Service s = ServerLocator.getServer().getService(d.getString("key"));
		if (s != null && s.getStatus() != null) {
			d.append("status", s.getStatus().name());
		}
	}

	// 服务器设�?
	@GetMapping(value = "/setting")
	public Document setting(String criteria, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//
		Document doc = new Document();
		Server server = ServerLocator.getServer();
		for (String key : server.settingKeys()) {
			doc.append(key, server.getSetting(key, Object.class));
		}
		//
		return doc;
	}

	// 服务器基础的信�?
	@GetMapping(value = "/info")
	public Document info(String criteria, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return ServerInfoUtil.fillInfo();
	}

	// 下载服务器完整信�?
	@GetMapping(value = "/infoFull")
	public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//
		Server server = ServerLocator.getServer();
		response.setCharacterEncoding("utf-8");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("Content-Type", "application/octet-stream;charset=utf-8");
		// response.setHeader("Content-Disposition", "attachment; filename=" +
		// filename+";filename*=utf-8''");
		response.setHeader("Content-Disposition",
				"attachment; filename=info_" + ServerUtil.getInstanceId(server) + ".txt");
		//
		try (OutputStream os = response.getOutputStream()) {
			os.write(ServerInfoUtil.machineInfo().getBytes("utf-8"));
			os.flush();
		}
	}

	// 检查是否初始化过
	@GetMapping(value = "/isInit")
	public Document isInit() throws Exception {
		SystemConfigService systemConfigService = ServerLocator.getServer().getService(SystemConfigService.class);
		long count = systemConfigService.count(null);
		// Thread.sleep(10 * 1000);
		return new Document().append("initFlag", count > 0);
	}

	@PostMapping(value = "/init")
	public Document init() throws Exception {
		Map<String, String> config = new HashMap<>();
		config.put("autoStop", "false");
		new Initialization().process(ServerLocator.getServer(), config);
		return new Document().append("success", true);
	}

	// private static Object lockA = new Object();
	// private static Object lockB = new Object();
	// @GetMapping(value = "/threadTest1")
	// public Document threadTest1() throws Exception{
	// synchronized (lockA) {
	// System.out.println("ThreadOne acquired lockA");
	// try {
	// Thread.sleep(10*1000); // 等待1秒，以便让ThreadTwo获得lockB
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// synchronized (lockB) {
	// System.out.println("ThreadOne acquired lockB");
	// }
	// }
	// //
	// return new Document();
	// }
	// @GetMapping(value = "/threadTest2")
	// public Document threadTes2() throws Exception{
	// synchronized (lockB) {
	// System.out.println("ThreadTwo acquired lockB");
	// try {
	// Thread.sleep(10*1000); // 等待1秒，以便让ThreadOne获得lockA
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// synchronized (lockA) {
	// System.out.println("ThreadTwo acquired lockA");
	// }
	// }
	// //
	// return new Document();
	// }

	@GetMapping(value = "/threadLocked")
	public Document threadLocked() throws Exception {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
		if (deadlockedThreads == null) {
			return new Document();
		} else {
			ArrayList<String> list = new ArrayList<>();
			for (Long l : deadlockedThreads) {
				list.add(l.toString());
			}
			return new Document("list", list);
		}
	}

	// 得到所有的threadGroup列表
	@GetMapping(value = "/threadGroups")
	public Document threadGroups() throws Exception {
		return new Document().append("list", SysThreadUtil.threadGroups());
	}

	@GetMapping(value = "/thread")
	public Document thread(String criteria) throws Exception {
		Map<String, Object> map = null;
		//
		if (!StringUtil.isEmpty(criteria)) {
			map = objectMapper.readValue(criteria, Map.class);
		}
		//
		String threadId = safeGetMap(map, "threadId");
		Long id = null;
		if (StringUtil.notEmpty(threadId)) {
			id = Long.parseLong(threadId);
		}

		Map<Thread, StackTraceElement[]> traces = SysThreadUtil.findStackTraces(id,
				safeGetMap(map, "threadName"), safeGetMap(map, "threadGroup"));
		//
		return SysThreadUtil.convert(traces);
	}

	@PostMapping("/exportThread")
	public void exportThread(@RequestBody Document doc, HttpServletResponse response) throws Exception {
		String filename = "threads_" + new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date()) + ".txt";
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		try (OutputStream os = response.getOutputStream()) {
			SysThreadUtil.exportThread(doc, os);
		}
	}

	@PostMapping("/gc")
	public Document gc() throws Exception {
		long b4 = Runtime.getRuntime().freeMemory();
		System.gc();
		long after = Runtime.getRuntime().freeMemory();
		return new Document().append("b4", b4).append("after", after)
				.append("gc", b4 - after);
	}

	// DUMP HEAP并下�?
	@PostMapping("/dumpHeap")
	public void dumpHeap(HttpServletResponse response) throws Exception {
		String filename = "dumpHeap_" + new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date()) + ".hprof";
		// 生成文件 - 临时把文件存放到tmp目录�?
		String file = ServerUtil.getPathTemp(ServerLocator.getServer()) + File.separator + filename;
		dumpHeap(file, false);
		//
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		try (ServletOutputStream os = response.getOutputStream()) {
			try (FileInputStream is = new FileInputStream(file)) {
				IOUtil.copy(is, os);
			}
		}
	}

	// 得到所有collection的信�?
	@GetMapping(value = "/colInfos")
	public Document colInfos() throws Exception {
		return new Document().append("list", allColInfo()).append("summary", dbInfo());
	}

	// 压缩一个或全部collection
	@PostMapping(value = "/colCompact")
	public Document colCompact(String colName) throws Exception {
		MongoDatabase db = ServerLocator.getServer().obtainMongoDatabase();
		if (StringUtil.notEmpty(colName)) {
			// 给出了colName则针对此colName压缩
			return db.runCommand(new Document("compact", colName));
		}
		// 否则压缩所有的colName
		// 所有的collection名称
		List<String> names = colNames();
		for (String name : names) {
			db.runCommand(new Document("compact", name));
		}
		//
		return new Document().append("ok", 1);
	}

	// 得到所有与内存相关信息
	@GetMapping(value = "/memoryCapture")
	public Document memoryCapture() throws Exception {
		return MemoryCapture.capture();
	}

	// **********************************
	// * 私有方法
	// **********************************
	// dump Java heap到文�?
	private void dumpHeap(String filePath, boolean live) throws Exception {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
				server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
		mxBean.dumpHeap(filePath, live);
	}

	// 得到所有collection的名称列�?
	private List<String> colNames() throws Exception {
		final List<String> names = new ArrayList<>(100);
		ServerLocator.getServer().obtainMongoDatabase().listCollectionNames().into(names);
		return names;
	}

	// 得到数据库的总信�?
	private Document dbInfo() throws Exception {
		MongoDatabase db = ServerLocator.getServer().obtainMongoDatabase();
		Document result = db.runCommand(new Document("dbStats", 1));
		// 计算空间利用�?
		calStorageRatioDb(result);
		//
		return result;

	}

	// 得到所有collection的详细信�?
	private List<Document> allColInfo() throws Exception {
		//
		MongoDatabase db = ServerLocator.getServer().obtainMongoDatabase();
		// 所有的collection名称
		List<String> names = colNames();
		//
		List<Document> list = new ArrayList<>(names.size());
		for (String name : names) {
			Document d = db.runCommand(new Document("collStats", name));
			// 把名字加入到返回结果�?
			d.append("_name", name);
			// 计算空间利用�?
			calStorageRatioCol(d);
			//
			list.add(d);
		}
		//
		return list;
	}

	// 计算空间利用�?
	private void calStorageRatioCol(Document line) throws Exception {
		Double result = (1.0 * line.getInteger("size", 1) / line.getInteger("storageSize", 1) * 100);
		line.append("_storageRatio", result.intValue());
	}

	// 计算空间利用�?- db
	private void calStorageRatioDb(Document line) throws Exception {
		Double result = (line.getDouble("dataSize") / line.getDouble("storageSize") * 100);
		line.append("_storageRatio", result.intValue());
	}

	//
	private String safeGetMap(Map<String, Object> map, String key) {
		if (map != null) {
			return (String) map.get(key);
		} else {
			return null;
		}
	}
}
