package com.mttk.orche.core;

import java.util.List;

/**
 * 实现了此接口说明可以根据key得到bean*
 * @param <T>
 */
public interface ObtainBeanable<T> {
	T obtainBean(String key) throws Exception;
	public List<T> obtainBeanList();
}
