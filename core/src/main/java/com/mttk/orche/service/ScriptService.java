package com.mttk.orche.service;

import java.util.Map;

import com.mttk.orche.core.Service;

public interface ScriptService extends Service {
	// 定义一个KEY前缀,以此前缀开头的key不会自动做eval
	public static final String IGNORE_EVAL_KEY_PREFIX = "_ie_";

	// 判断给定的KEY是否会自动eval
	public boolean isAutoEval(String key);

	/**
	 * 创建一个脚本运行环境用于后续的eval方法
	 * 不带任何参数的调用会返回一个内置的环境对象,调用者不允许修改返回值
	 * 
	 * @return
	 */
	public Map<String, Object> createEnv();

	// /**
	// * 同上，但是可以带参数
	// * @param p pipeline，可以为空
	// *
	// * @return
	// */
	// public Map<String,Object> createEnv(Pipeline p);
	/**
	 * 执行脚本
	 * 
	 * @param scriptEnv 如果scriptEnv为null,是使用createEnv的返回值
	 * @param script
	 * @return
	 * @throws Exception
	 */
	public Object eval(Map<String, Object> scriptEnv, String script) throws Exception;
}
