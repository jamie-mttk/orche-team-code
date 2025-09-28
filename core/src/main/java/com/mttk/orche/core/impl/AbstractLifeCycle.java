package com.mttk.orche.core.impl;

import com.mttk.orche.core.LifeCycle;

/**
 * 基础的生命周期实现<br>
 * 继承后可以覆盖doStart()和doStop()实现自己的逻辑
 *
 */
public abstract class AbstractLifeCycle implements LifeCycle {
	private Status status = Status.STOPPED;

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public final void start() {
		if (getStatus() != Status.STOPPED
				&& getStatus() != Status.ERROR) {
			throw new RuntimeException("Can not be stated, status should be "
					+ Status.STOPPED + " or "
					+ Status.ERROR + " , now it is " + getStatus());
		}
		try {
			doStart();
			//
			status = Status.RUNNING;
		} catch (Throwable t) {
			status = Status.ERROR;
			throw new RuntimeException("Fail to start,reason:" + t.getMessage(), t);
		}
	}

	@Override
	public final void stop() {
		if (getStatus() != Status.RUNNING
				&& getStatus() != Status.ERROR) {
			throw new RuntimeException("Can not be stopped, status should be "
					+ Status.RUNNING + " or "
					+ Status.ERROR + " , now it is " + getStatus());
		}
		try {
			doStop();
			//
			status = Status.STOPPED;
		} catch (Throwable t) {
			status = Status.ERROR;
			throw new RuntimeException("Fail to stop", t);
		}
	}

	protected void doStart() throws Exception {
	};

	protected void doStop() throws Exception {
	};

}
