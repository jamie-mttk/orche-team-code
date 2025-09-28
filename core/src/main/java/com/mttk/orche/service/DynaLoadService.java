package com.mttk.orche.service;

import com.mttk.orche.core.Service;

/**
 * Create object even the underling jar is changed  
 * In this version we only support one dynamic load class loader for the whole server
 * Later on (once tenant concept is considered),each dynamic load is created for each tenant
 */
public interface DynaLoadService  extends Service{
	/**
	 * 创建给定类名的对象，类名必须有没有参数的构造函数
	 * @param className
	 * @return
	 * @throws Exception
	 */
  public Object createObject(String className) throws  Exception;
  /**
   * 获得ClassLoader,包含动态加载的类
   * @return
   * @throws Exception
   */
  public ClassLoader obtainClassLoader() throws Exception;
  /**
   * 重置ClassLoader
   * @throws Exception
   */
  public void resetClassLoader() throws Exception;
}
