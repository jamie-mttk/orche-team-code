package com.mttk.orche.core;
/*
 * 用于生命周期管理
 */
public interface LifeCycle {
	/**
	 * 状态定义：停止,运行,出错
	 *
	 */
	enum Status {
		STOPPED, RUNNING,  ERROR
	};
	
	/**
	 * 得到当前状态
	 * @return
	 */
	Status getStatus();
	/**
	 * 启动时调用
	 */
	public void start();
	/**
	 * 停止时调用
	 */
	public void stop();
}
