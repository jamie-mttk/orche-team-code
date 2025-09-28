package com.mttk.orche.admin.util;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

public class MemoryCapture {

	public static Document capture() {
		Document doc = new Document();
		// Heap total
		Document general = new Document();
		doc.append("general", general);
		// vm total
		general.append("usedMemory", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
				.append("maxMemory", Runtime.getRuntime().maxMemory());
		// heap and none-heap
		MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
		general.append("HEAP", captureMemoryUsage(memoryMxBean.getHeapMemoryUsage()));
		general.append("NON_HEAP", captureMemoryUsage(memoryMxBean.getNonHeapMemoryUsage()));
		// Loop for each memory pool
		List<Document> memoryPoolsDocs = new ArrayList<>();
		doc.append("memoryPools", memoryPoolsDocs);
		for (final MemoryPoolMXBean memoryPool : ManagementFactory.getMemoryPoolMXBeans()) {
			memoryPoolsDocs.add(captureMemoryPool(memoryPool));
		}
		// garbage collection
		List<Document> garbageCollectorsList = new ArrayList<>();
		doc.append("garbageCollectors", garbageCollectorsList);

		List<GarbageCollectorMXBean> collectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
		for (GarbageCollectorMXBean collectorMxBean : collectorMxBeans) {
			Document d = new Document();
			garbageCollectorsList.add(d);
			//
			d.append("name",collectorMxBean.getName());
			d.append("count", collectorMxBean.getCollectionCount());
			d.append("time", collectorMxBean.getCollectionTime());
			d.append("memoryPools", Arrays.asList(collectorMxBean.getMemoryPoolNames()));
		}
		//Direct memory pool
		List<Document> bufferPoolList = new ArrayList<>();
		doc.append("bufferPools", bufferPoolList);

		try {
			List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
			for (BufferPoolMXBean pool : pools) {
				Document d = new Document();
				bufferPoolList.add(d);
				d.append("name", pool.getName());
				d.append("count", pool.getCount());
				d.append("memoryUsed", pool.getMemoryUsed());
				d.append("totalCapacity", pool.getTotalCapacity());

			}
		} catch (Exception e) {
			// Ignore exception if BufferPoolMXBean is not available
			e.printStackTrace();
		}
		//
		return doc;
	}

	//
	private static Document captureMemoryPool(MemoryPoolMXBean memoryPool) {
		Document doc = new Document();
		//
		doc.append("name", memoryPool.getName());
		doc.append("managerNames", Arrays.asList(memoryPool.getMemoryManagerNames()));
		doc.append("type", memoryPool.getType().name());
		doc.append("peakUsage", captureMemoryUsage(memoryPool.getPeakUsage()));
		doc.append("collectionUsage", captureMemoryUsage(memoryPool.getCollectionUsage()));
		doc.append("usage", captureMemoryUsage(memoryPool.getUsage()));
		//
		return doc;
	}

	private static Document captureMemoryUsage(MemoryUsage memoryUsage) {
		if (memoryUsage == null) {
			return null;
		}
		//
		return new Document().append("init", memoryUsage.getInit()).append("used", memoryUsage.getUsed())
				.append("committed", memoryUsage.getCommitted()).append("max", memoryUsage.getMax());
	}
}
