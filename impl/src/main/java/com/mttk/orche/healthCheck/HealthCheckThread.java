package com.mttk.orche.healthCheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckThread extends Thread {
	private Logger logger = LoggerFactory.getLogger(HealthCheckThread.class);
	//
	private HealthCheckServiceImpl impl;

	public HealthCheckThread(HealthCheckServiceImpl impl) {
		this.setName("HealthCheck thread");
		this.impl = impl;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				//
				//long l=System.currentTimeMillis();
				impl.trigger();
				//logger.info("Thread check cost "+(System.currentTimeMillis()-l)+" ms");
			} catch (Throwable t) {
				logger.error("Check hardware thread failed", t);
			}
			// wait
			try {
				Thread.sleep(impl.getInterval() * 1000);
			} catch (Throwable t) {
				//
				break;
			}
		}
	}
}
