package com.mttk.orche.addon;

/**
 * 实现此接口后支持激活/禁用
 *
 */
public interface Activeable {
	/**
	 * 激活服务的实例
	 * 
	 * @param instance 服务实例
	 */
	public void active(String instance) throws Exception;

	/**
	 * 禁用服务的实例
	 * 
	 * @param instance 服务实例
	 */
	public void deactive(String instance) throws Exception;
}
