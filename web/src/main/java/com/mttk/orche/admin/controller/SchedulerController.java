package com.mttk.orche.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.quartz.JobDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.SchedulerService;
import com.mttk.orche.util.StringUtil;

@RestController
@RequestMapping(value = "/scheduler")
public class SchedulerController extends PersistableControllerBase {

	// 用于接收JSON请求体的内部类
	public static class CancelJobRequest {
		private String jobName;
		private String jobGroup;

		public String getJobName() {
			return jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		public String getJobGroup() {
			return jobGroup;
		}

		public void setJobGroup(String jobGroup) {
			this.jobGroup = jobGroup;
		}
	}

	@Override
	public Class getServiceClass() {
		return SchedulerService.class;
	}

	// @GetMapping(value = "/running")
	// public Document running(HttpServletRequest request) throws Exception {
	// // System.out.println("inParas="+inParas);
	// Server server = ServerLocator.getServer();
	// List<Document> list = new ArrayList<>(10);
	// Bson sort = Sorts.descending("time");
	// FindIterable<Document> iterable =
	// server.obtainCollection("qrtz_locks").find().sort(sort);
	// for (Document doc : iterable) {
	// list.add(doc);
	// //
	// Bson filter = Filters.and(Filters.eq("keyGroup", doc.getString("keyGroup")),
	// Filters.eq("keyName", doc.getString("keyName")));
	// FindIterable<Document> fi =
	// server.obtainCollection("qrtz_jobs").find(filter);
	// Optional<Document> o = Optional.ofNullable(fi.first());
	// if (o.isPresent()) {
	// doc.append("keyDescription", o.get().getString("jobDescription"));
	// }
	// }
	// //
	// Document doc = new Document();
	// doc.append("list", list);
	// return doc;
	// }

	// @PostMapping("/removeRunning")
	// public Document removeRunning(String id) throws Exception {
	// if (StringUtil.isEmpty(id)) {
	// throw new RuntimeException("No id is provided");
	// }
	// Bson filter = Filters.eq("_id", new ObjectId(id));
	// Server server = ServerLocator.getServer();
	// DeleteResult result =
	// server.obtainCollection("qrtz_locks").deleteOne(filter);
	// return new Document().append("count", result.getDeletedCount());

	// }

	// @GetMapping(value = "/all")
	// public Document all(String criteria, HttpServletRequest request) throws
	// Exception {

	// Map<String, Object> map = parse2Map(criteria, request);
	// if (StringUtil.notEmpty(map.get("name"))) {
	// // 需要把查询中name修改为description
	// map.put("description", map.get("name"));
	// map.remove("name");
	// }
	// Bson filter = parseCriteria(map);

	// //
	// Server server = ServerLocator.getServer();
	// List<Document> list = new ArrayList<>(10);
	// Bson sort = Sorts.descending("nextFireTime");
	// MongoCollection<Document> col = server.obtainCollection("qrtz_triggers");
	// FindIterable<Document> iterable = null;
	// if (filter == null) {
	// iterable = col.find().sort(sort);
	// } else {
	// iterable = col.find(filter).sort(sort);
	// }
	// for (Document doc : iterable) {
	// list.add(doc);
	// //
	// Bson filter1 = Filters.eq("_id", doc.get("jobId"));
	// FindIterable<Document> fi =
	// server.obtainCollection("qrtz_jobs").find(filter1);
	// Optional<Document> o = Optional.ofNullable(fi.first());
	// if (o.isPresent()) {
	// doc.append("keyDescription", o.get().getString("jobDescription"));
	// }
	// }
	// //
	// Document doc = new Document();
	// doc.append("list", list);
	// return doc;
	// }
	@GetMapping(value = "/listJobs")
	public Document listJobs() throws Exception {
		List<JobDetail> list = ServerLocator.getServer().getService(SchedulerService.class).listJobs(null);
		//
		List<Document> listNew = new ArrayList<>(list.size());
		for (JobDetail jobDetail : list) {
			Document doc = new Document();
			// 获取JobKey信息
			doc.append("jobName", jobDetail.getKey().getName());
			doc.append("jobGroup", jobDetail.getKey().getGroup());
			// 获取描述信息
			doc.append("description", jobDetail.getDescription());
			// 获取JobDataMap的所有key/value
			Map<String, Object> jobDataMap = jobDetail.getJobDataMap();
			for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
				doc.append(entry.getKey(), entry.getValue());
			}
			listNew.add(doc);
		}
		//
		return new Document().append("list", listNew);
	}

	@PostMapping(value = "/cancelJob")
	public Document cancelJob(@RequestBody CancelJobRequest request) throws Exception {
		if (StringUtil.isEmpty(request.getJobName())) {
			throw new RuntimeException("jobName不能为空");
		}
		if (StringUtil.isEmpty(request.getJobGroup())) {
			throw new RuntimeException("jobGroup不能为空");
		}

		// 调用SchedulerService的unschedule方法取消定时任务
		ServerLocator.getServer().getService(SchedulerService.class).unschedule(request.getJobName(),
				request.getJobGroup());

		return new Document().append("success", true);
	}
}
