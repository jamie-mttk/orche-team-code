package com.mttk.orche.addon;

/**
 * 用于用户编写新的报警通知方式接口
 *
 */
public interface EventOperation extends Pluginable {
	public void execute(EventContext c) throws Exception;
}
