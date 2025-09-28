package com.mttk.orche.healthCheck;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mttk.orche.addon.Event;
import com.mttk.orche.addon.ServiceContext;

import com.mttk.orche.support.MongoUtil;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

public class HealthCheckUtil {

	// 记录CPU时间,并且在需要时报警
	static long[] handleCPU(ServiceContext context, Document doc, SystemInfo si, long[] prevTicks,
			boolean checkCPUandMem, long cpuThreshold) {
		HardwareAbstractionLayer hal = si.getHardware();
		CentralProcessor processor = hal.getProcessor();
		//
		long[] ticks = processor.getSystemCpuLoadTicks();
		long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
		long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
		long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
		long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
		long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
		long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
		long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
		long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
		long total = user + nice + sys + idle + iowait + irq + softirq + steal;
		double cpuUsagePercent = (1 - idle * 1.0 / total) * 100;
		doc.append("cpu_user", user);
		doc.append("cpu_nice", nice);
		doc.append("cpu_sys", sys);
		doc.append("cpu_idle", idle);
		doc.append("cpu_iowait", iowait);
		doc.append("cpu_irq", irq);
		doc.append("cpu_softirq", softirq);
		doc.append("cpu_steal", steal);
		doc.append("cpu_total", total);
		doc.append("cpu_percent", cpuUsagePercent);
		// 检查是否需要报警
		if (checkCPUandMem && cpuUsagePercent >= cpuThreshold) {
			// alert
			Event event = context.createEvent();
			event.put("type", "system");
			event.put("source", "sysCheck");
			event.put("sourceSub", "cpu");
			event.put("content", "CPU usage is too high:" + new DecimalFormat("#.##").format(cpuUsagePercent)
					+ "%. Threshold:" + cpuThreshold + "%");
			//
			context.informEvent(event);
		}
		//
		return ticks;
	}

	// 记录内存,并且在需要时报警
	static void handleMem(ServiceContext context, Document doc, SystemInfo si, boolean checkCPUandMem,
			long memSystemThreshold, long memVMThreshold) {
		HardwareAbstractionLayer hal = si.getHardware();

		// Total RAM
		GlobalMemory gm = hal.getMemory();
		long memTotal = gm.getTotal();
		long memAvailable = gm.getAvailable();
		double memSystemPerent = (memTotal - memAvailable) * 1.0 / memTotal * 100;
		doc.append("mem_total", memTotal);
		doc.append("mem_available", memAvailable);
		doc.append("mem_percent", memSystemPerent);
		if (checkCPUandMem && memSystemPerent > memSystemThreshold) {
			// alert
			Event event = context.createEvent();
			event.put("type", "system");
			event.put("source", "sysCheck");
			event.put("sourceSub", "memSystem");
			event.put("content", "System memory usage is too high:" + new DecimalFormat("#.##").format(memSystemPerent)
					+ "%. Threshold:" + memSystemThreshold + "%");
			//
			context.informEvent(event);
		}
		// VM RAM
		Runtime runtime = Runtime.getRuntime();
		long vmTotal = runtime.totalMemory();
		long vmFree = runtime.freeMemory();
		double memVMPercent = (vmTotal - vmFree) * 1.0 / vmTotal * 100;
		doc.append("mem_vm_total", vmTotal);
		doc.append("mem_vm_available", vmFree);
		doc.append("mem_vm_percent", memVMPercent);
		//
		if (checkCPUandMem && memVMPercent > memVMThreshold) {
			// alert
			Event event = context.createEvent();
			event.put("type", "system");
			event.put("source", "sysCheck");
			event.put("sourceSub", "memVM");
			event.put("content", "VM memory usage is too high:" + new DecimalFormat("#.##").format(memVMPercent)
					+ "%. Threshold:" + memVMThreshold + "%");
			//
			context.informEvent(event);
		}
	}

	// 记录网络流量
	static void handleNetwork(ServiceContext context, Document doc, SystemInfo si, Map<String, long[]> prevTrafficMap) {

		List<NetworkIF> networks = si.getHardware().getNetworkIFs(false);
		// 记录各个网卡的速度
		List<Document> list = new ArrayList<>(networks.size());
		doc.append("networks", list);
		// 记录总的网卡数量以及速度累计
		int count = 0;
		double totalSpeedRecv = 0d;
		double totalSpeedSent = 0d;
		//
		for (NetworkIF n : networks) {
			// System.out.println(networks.size()+"\t"+n.getName()+"\t"+n.getDisplayName());
			if (n.getSpeed() == 0 || (n.getIPv4addr().length == 0 && n.getIPv6addr().length == 0)) {
				// JAVA启动参数设置了 -Djava.net.preferIPv4Stack=true 后
				// 会得到很多奇怪的网卡,可以通过上面方法过滤掉
				continue;
			}

			long bytesRecv = n.getBytesRecv();
			long bytesSent = n.getBytesSent();
			if (bytesRecv == 0 || bytesSent == 0) {
				// 说明是没有用到的网卡
				// 注意判断条件用的是OR,因为VM网卡可能会有Sent但是没有Recv
				continue;
			}
			long timeStamp = n.getTimeStamp();
			//
			long[] prevTraffic = prevTrafficMap.get(n.getName());
			if (prevTraffic != null) {
				// 换算成秒
				double timeDiff = (timeStamp - prevTraffic[0]) / 1000d;
				//
				Document docNetwork = new Document();
				list.add(docNetwork);
				//
				docNetwork.append("name", n.getName());
				//
				count++;
				// 接收速度
				Double temp = (bytesRecv - prevTraffic[1]) / timeDiff;
				totalSpeedRecv += temp;
				docNetwork.append("speedReceive", temp.longValue());
				// 发送速度
				temp = (bytesSent - prevTraffic[2]) / timeDiff;
				totalSpeedSent += temp;
				docNetwork.append("speedSend", temp.longValue());
			}
			//
			// 记录供下次使用
			prevTraffic = new long[] { timeStamp, bytesRecv, bytesSent };
			prevTrafficMap.put(n.getName(), prevTraffic);
		}
		//
		if (count > 0) {
			doc.append("networkSpeedRecv", totalSpeedRecv / count);
			doc.append("networkSpeedSent", totalSpeedSent / count);
		}
	}
}
