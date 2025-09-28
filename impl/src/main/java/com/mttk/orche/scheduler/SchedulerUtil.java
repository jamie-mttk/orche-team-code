package com.mttk.orche.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.service.support.SchedulerCallback;
import com.mttk.orche.util.StringUtil;

public class SchedulerUtil {
	//
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void schedule(Scheduler scheduler, String job, String jobDescription, String group,
			AdapterConfig config, Class<? extends SchedulerCallback> callbackClass, Map<String, Object> paras)
			throws Exception {

		//
		JobDetail jobDetail = null;
		JobDataMap internalJobDataMap = new JobDataMap();
		internalJobDataMap.put(JobProxy.JOB_CLASS_NAME, callbackClass.getCanonicalName());
		if (paras != null) {
			internalJobDataMap.putAll(paras);
		}
		jobDetail = org.quartz.JobBuilder.newJob(JobProxy.class).withIdentity(job, group).requestRecovery(false)
				.storeDurably(false).usingJobData(internalJobDataMap).withDescription(jobDescription).build();
		//

		if ("COMBINATION".equalsIgnoreCase(config.getString("mode"))) {
			Set<Trigger> triggers = new HashSet<>();
			//
			List<AdapterConfig> list = config.getBeanList("triggers");
			for (int i = 0; i < list.size(); i++) {
				AdapterConfig a = list.get(i);
				triggers.add(buildTriggerSingle(job, (i + 1), jobDescription, group, a));
			}
			// System.out.println("AAA:"+scheduler+"==>"+scheduler.getClass());
			// System.out.println("BBB:("+triggers.size()+")"+triggers);
			//
			scheduler.scheduleJob(jobDetail, triggers, false);
		} else {
			//
			Trigger trigger = buildTriggerSingle(job, 0, jobDescription, group, config);
			//
			scheduler.scheduleJob(jobDetail, trigger);
		}

	}

	public static List<JobDetail> listJobs(Scheduler scheduler, String groupName) throws Exception {
		List<JobDetail> jobDetails = new ArrayList<>();

		// 如果groupName为空或null，则获取所有组的JobDetail
		if (StringUtil.isEmpty(groupName)) {
			List<String> jobGroupNames = scheduler.getJobGroupNames();
			for (String group : jobGroupNames) {
				jobDetails.addAll(getJobDetailsByGroup(scheduler, group));
			}
		} else {
			// 获取指定组的JobDetail
			jobDetails.addAll(getJobDetailsByGroup(scheduler, groupName));
		}

		return jobDetails;
	}

	// 检查给定的JOB是否存在
	public static boolean jobExist(Scheduler scheduler, String job, String group) throws Exception {
		JobKey jobKey = new JobKey(job, group);
		return scheduler.getJobDetail(jobKey) != null;
	}

	// 根据组名获取JobDetail列表
	private static List<JobDetail> getJobDetailsByGroup(Scheduler scheduler, String groupName) throws Exception {
		List<JobDetail> jobDetails = new ArrayList<>();
		Set<JobKey> jobKeys = scheduler.getJobKeys(org.quartz.impl.matchers.GroupMatcher.jobGroupEquals(groupName));
		for (JobKey jobKey : jobKeys) {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			if (jobDetail != null) {
				jobDetails.add(jobDetail);
			}
		}
		return jobDetails;
	}

	// 创建trigger - 单一模式的
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Trigger buildTriggerSingle(String job, int index, String jobDescription, String group,
			AdapterConfig config) throws Exception {
		//
		String mode = config.getString("mode");
		//

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String triggerKey = job;
		if (index > 0) {
			triggerKey = triggerKey + "_" + index;
		}
		TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey, group);
		triggerBuilder.withDescription(jobDescription + (index <= 0 ? "" : "_" + index));
		if (!"FIXED".equalsIgnoreCase(mode)) {
			String temp = config.getString("startTime");
			if (StringUtil.notEmpty(temp)) {
				triggerBuilder = triggerBuilder.startAt(sdf.parse(temp));
			}
			temp = config.getString("endTime");
			if (StringUtil.notEmpty(temp)) {
				triggerBuilder = triggerBuilder.endAt(sdf.parse(temp));
			}
		}
		//
		ScheduleBuilder schedulerBuilder = null;
		if ("FIXED".equalsIgnoreCase(mode)) {
			schedulerBuilder = buildScheduleFixed(triggerBuilder, config);
		} else if ("SIMPLE".equalsIgnoreCase(mode)) {
			schedulerBuilder = buildScheduleSimple(config);
		} else if ("CRON".equalsIgnoreCase(mode)) {
			schedulerBuilder = buildScheduleCron(config);
		} else if ("DAILY".equalsIgnoreCase(mode)) {
			schedulerBuilder = buildScheduleDaily(config);
		} else if ("CALENDAR".equalsIgnoreCase(mode)) {
			schedulerBuilder = buildScheduleCalendar(config);
		} else {
			throw new RuntimeException("Unsuported schedule mode:" + mode);
		}
		//
		return triggerBuilder.withSchedule(schedulerBuilder).build();
	}

	// 删除Scheduler
	public static void unschedule(Scheduler scheduler, String job, String group) throws Exception {
		JobKey jobKey = new JobKey(job, group);
		scheduler.deleteJob(jobKey);

	}
	// //手工触发
	// public static void triggerManually(Scheduler scheduler, String job, String
	// group) throws Exception {
	// JobKey jobKey = new JobKey(job, group);
	// scheduler.triggerJob(jobKey);
	// }

	//
	// 私有方法
	//
	// 固定时间运行
	private static ScheduleBuilder buildScheduleFixed(TriggerBuilder triggerBuilder, AdapterConfig config)
			throws Exception {
		//
		Date fixedTime = null;
		String temp = config.getString("fixedTime");
		if (StringUtil.notEmpty(temp)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			fixedTime = sdf.parse(temp);
		} else {
			// 不会到这里
			fixedTime = new Date();
		}
		//
		triggerBuilder.startAt(fixedTime);
		//
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withRepeatCount(0) // 设置重复次数为0（仅执行一次）
				.withMisfireHandlingInstructionFireNow();
		//
		return scheduleBuilder;

	}

	// 简单定时器
	private static ScheduleBuilder buildScheduleSimple(AdapterConfig config) {
		Integer repeatInterval = config.getInteger("repeatInterval");
		String temp = config.getString("repeatUnit");
		SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();
		if ("hour".equalsIgnoreCase(temp)) {
			ssb = ssb.withIntervalInHours(repeatInterval);
		} else if ("minute".equalsIgnoreCase(temp)) {
			ssb = ssb.withIntervalInMinutes(repeatInterval);
		} else if ("second".equalsIgnoreCase(temp)) {
			ssb = ssb.withIntervalInSeconds(repeatInterval);
		} else if ("milliSecond".equalsIgnoreCase(temp)) {
			ssb = ssb.withIntervalInMilliseconds(repeatInterval);
		} else {
			throw new RuntimeException("Unsupported repeat unit:" + temp);
		}
		// 重复次数,2025/01/13
		int repeatCount = config.getInteger("repeatCount", -1);
		if (repeatCount < 0) {
			ssb = ssb.repeatForever();
		} else {
			ssb.withRepeatCount(repeatCount);
		}
		// misFire
		String misFire = config.getString("misFire", "0");
		if ("0".equals(misFire)) {
			// 不设置为 MISFIRE_INSTRUCTION_SMART_POLICY
		} else if ("-1".equals(misFire)) {
			ssb.withMisfireHandlingInstructionIgnoreMisfires();
		} else if ("1".equals(misFire)) {
			ssb.withMisfireHandlingInstructionFireNow();
		} else if ("2".equals(misFire)) {
			ssb.withMisfireHandlingInstructionNowWithExistingCount();
		} else if ("3".equals(misFire)) {
			ssb.withMisfireHandlingInstructionNowWithRemainingCount();
		} else if ("4".equals(misFire)) {
			ssb.withMisfireHandlingInstructionNextWithRemainingCount();
		} else if ("5".equals(misFire)) {
			ssb.withMisfireHandlingInstructionNextWithExistingCount();
		} else {
			// 不可能来这里
			ssb.withMisfireHandlingInstructionFireNow();
		}

		//
		return ssb;
	}

	// CRON定时器
	private static ScheduleBuilder buildScheduleCron(AdapterConfig config) {
		CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(config.getString("cronExpression"));
		//
		// misFire
		String misFire = config.getString("misFire", "0");
		if ("0".equals(misFire)) {
			// 不设置为 MISFIRE_INSTRUCTION_SMART_POLICY
		} else if ("-1".equals(misFire)) {
			builder.withMisfireHandlingInstructionIgnoreMisfires();
		} else if ("1".equals(misFire)) {
			builder.withMisfireHandlingInstructionFireAndProceed();
		} else if ("2".equals(misFire)) {
			builder.withMisfireHandlingInstructionDoNothing();
		} else {
			//
		}
		//
		return builder;
	}

	// Daily定时器
	private static ScheduleBuilder buildScheduleDaily(AdapterConfig config) {
		DailyTimeIntervalScheduleBuilder sb = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule();
		TimeOfDay timeOfDay = null;
		//
		timeOfDay = parseTimeOfDay(config.getString("dailyStartTime"));
		if (timeOfDay != null) {
			sb.startingDailyAt(timeOfDay);
		}
		timeOfDay = parseTimeOfDay(config.getString("dailyEndTime"));
		if (timeOfDay != null) {
			sb.endingDailyAt(timeOfDay);
		}
		// 定时
		sb.withInterval(config.getIntegerMandatory("dailyRepeatInterval"),
				IntervalUnit.valueOf(config.getStringMandatory("dailyRepeatUnit").toUpperCase()));
		//
		List<String> days = (List<String>) config.get("dailyDaysOfWeek");

		sb.onDaysOfTheWeek(days.stream().map(Integer::parseInt).collect(Collectors.toSet()));
		//
		// misFire
		String misFire = config.getString("misFire", "0");
		if ("0".equals(misFire)) {
			// 不设置为 MISFIRE_INSTRUCTION_SMART_POLICY
		} else if ("-1".equals(misFire)) {
			sb.withMisfireHandlingInstructionIgnoreMisfires();
		} else if ("1".equals(misFire)) {
			sb.withMisfireHandlingInstructionFireAndProceed();
		} else if ("2".equals(misFire)) {
			sb.withMisfireHandlingInstructionDoNothing();
		} else {
			//
		}
		//
		return sb;
	}

	// Calendar定时器
	private static ScheduleBuilder buildScheduleCalendar(AdapterConfig config) {
		CalendarIntervalScheduleBuilder sb = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
		//
		sb.withInterval(config.getIntegerMandatory("calendarRepeatInterval"),
				IntervalUnit.valueOf(config.getStringMandatory("calendarRepeatUnit").toUpperCase()));
		// misFire
		String misFire = config.getString("misFire", "0");
		if ("0".equals(misFire)) {
			// 不设置为 MISFIRE_INSTRUCTION_SMART_POLICY
		} else if ("-1".equals(misFire)) {
			sb.withMisfireHandlingInstructionIgnoreMisfires();
		} else if ("1".equals(misFire)) {
			sb.withMisfireHandlingInstructionFireAndProceed();
		} else if ("2".equals(misFire)) {
			sb.withMisfireHandlingInstructionDoNothing();
		} else {
			//
		}
		//
		return sb;
	}

	// 试图从字符串解析出TimeOfDay,如果字符串为空返回null
	// 输入字符串格式为HH:mm:ss,不是此格式报错
	private static TimeOfDay parseTimeOfDay(String raw) {
		if (StringUtil.isEmpty(raw)) {
			return null;
		}
		if (raw.length() != "00:00:00".length()) {
			throw new RuntimeException("Invalid TimeOfDay format:" + raw);
		}
		//
		return new TimeOfDay(Integer.parseInt(raw.substring(0, 2)), Integer.parseInt(raw.substring(3, 5)),
				Integer.parseInt(raw.substring(6, 8)));
	}
}
