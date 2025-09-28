package com.mttk.orche.healthCheck;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.core.impl.AbstractPersistService;
import com.mttk.orche.service.HealthCheckService;
import com.mttk.orche.support.ServerUtil;

import oshi.SystemInfo;

@ServiceFlag(key = "healthCheckService", name = "健康监视管理", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class HealthCheckServiceImpl extends AbstractPersistService implements HealthCheckService {
	@Control(label = "检查间隔(秒)", mandatory = true, defaultVal = "120")
	private int interval = 60;
	@Control(label = "记录监视信息", mode = "checkbox", mandatory = true, defaultVal = "false")
	private boolean monitorEnabled = false;

	@Control(label = "负载高报警", mode = "checkbox", defaultVal = "false")
	private boolean checkCPUandMem = false;
	@Control(label = "CPU占用率", defaultVal = "80", props = "dataType:number", bindings = {
			"show:this.data.checkCPUandMem" }, description = "当CPU占用超过此百分比时报警,取值范围0到100")
	private long cpuThreshold = 80;
	@Control(label = "系统内存占用率", defaultVal = "80", props = "dataType:number", bindings = {
			"show:this.data.checkCPUandMem" }, description = "当总内存占用超过此百分比时报警,取值范围0到100")
	private long memSystemThreshold = 80;
	@Control(label = "虚拟机内存占用率", defaultVal = "80", props = "dataType:number", bindings = {
			"show:this.data.checkCPUandMem" }, description = "当虚拟机内存账用超过此百分比时报警,取值范围0到100")
	private long memVMThreshold = 80;
	//
	private HealthCheckThread healthCheckThread;
	private Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);
	private SystemInfo si = null;
	// 记录上一个周期记录的值
	private long[] prevTicks = null;
	// 记录上一次网络状况
	private Map<String, long[]> prevTrafficMap = new HashMap<>();

	@Override
	public void doStart() throws Exception {
		//
		super.doStart();
		//
		si = new SystemInfo();
		//
		startCheckThread();
	}

	@Override
	public void doStop() throws Exception {
		//
		super.doStop();
		//
		stopCheckThread();
	}

	// 找到一个新的CPU/RAM的信息数据
	void trigger() {
		//
		if (!needCheck()) {
			return;
		}
		if (prevTicks == null) {
			// 第一次,无法统计CPU,记录后直接跳出
			prevTicks = si.getHardware().getProcessor().getSystemCpuLoadTicks();
			//
			return;
		}
		//
		Document doc = new Document();
		// 处理CPU并可能报警
		prevTicks = HealthCheckUtil.handleCPU(context, doc, si, prevTicks, checkCPUandMem, cpuThreshold);
		// 处理MEM并可能报警
		HealthCheckUtil.handleMem(context, doc, si, checkCPUandMem, memSystemThreshold, memVMThreshold);
		// 记录网络状态
		HealthCheckUtil.handleNetwork(context, doc, si, prevTrafficMap);

		// 基本
		if (monitorEnabled) {
			// 补充信息
			doc.append("server", ServerUtil.getInstanceId(server));
			doc.append("logTime", new Date());
			try {
				//
				this.save(doc);
			} catch (Exception e) {
				logger.error("Save health check log failed", e);
			}
		}
	}

	//
	private void startCheckThread() {
		if (healthCheckThread != null) {
			return;
		}
		//
		healthCheckThread = new HealthCheckThread(this);
		healthCheckThread.start();
	}

	//
	private void stopCheckThread() {
		if (healthCheckThread == null) {
			return;
		}
		//
		healthCheckThread.interrupt();
		healthCheckThread = null;
	}

	// 是否需要检查
	boolean needCheck() {
		return monitorEnabled || checkCPUandMem;
	}
	// 下面方法供Thread调用

	// 检查周期
	long getInterval() {
		return interval;
	}
}
