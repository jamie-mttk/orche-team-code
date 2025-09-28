package com.mttk.orche.addon;

import java.util.List;

/**
 * 事件上下文
 *
 */
public interface EventContext extends Context {
	/**
	 * 返回Event的配置
	 * 
	 * @return
	 */
	AdapterConfig getEventConfig();

	/**
	 * 是否是组合消息.组合消息是指一次通知包含多个消息.<br>
	 * true: getEvents返回消息列表,getEvent返回第一个事件<br>
	 * false:getEvent返回单个消息,getEvents返回null<br>
	 * 
	 * @return
	 */
	boolean isBatchEvent();

	/**
	 * 得到单个消息
	 * 
	 * @return
	 */
	Event getEvent();

	/**
	 * 得到消息列表
	 * 
	 * @return
	 */
	List<Event> getEvents();
}
