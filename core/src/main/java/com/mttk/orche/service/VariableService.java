package com.mttk.orche.service;

import com.mttk.orche.core.PersistService;

/**
 * 管理变量
 * 
 */
public interface VariableService extends PersistService {
	/**
	 * 计算给定key变量的值
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	Object eval(String key) throws Exception;
}
