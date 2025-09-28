package com.mttk.orche.addon;

import java.util.Map;

import org.slf4j.Logger;

import com.mttk.orche.core.Server;

/**
 * 提供运行时需要的信息
 *
 */
public interface Context {
	/**
	 * 得到服务器对象
	 * 
	 * @return
	 */
	Server getServer();

	/**
	 * 得到Slf4j日志对象
	 * 
	 * @return
	 */
	Logger getLogger();

	/**
	 * 把Map包装成adapterConfig
	 * 
	 * @param map
	 * @return
	 */
	AdapterConfig createAdapterConfig(Map<String, Object> map);

	/**
	 * 创建一个报警事件通知对象
	 * 
	 * @return
	 */
	Event createEvent();

	/**
	 * 发送报警事件对象<br>
	 * 报警事件会写入"错误报警"界面以及根据报警通知调用合适的报警方式
	 * 
	 * @param event
	 */
	void informEvent(Event event);

	// /**
	// * 此方法能够创建dynamic目录下动态加载的类
	// *
	// * @param className
	// * @return
	// * @throws Exception
	// */
	// Object createObject(String className) throws Exception;

	// /**
	// * 同上，得到包含dynamic目录下jar包的类加载器
	// *
	// * @return
	// * @throws Exception
	// */
	// ClassLoader obtainClassLoader() throws Exception;

}
