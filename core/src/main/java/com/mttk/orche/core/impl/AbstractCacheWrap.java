package com.mttk.orche.core.impl;

/**
 * 基础的CacheWrap实现
 *
 */
public class AbstractCacheWrap implements CacheWrap {
	private String id;
	private String key;

	public AbstractCacheWrap() {

	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public void start() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public String toString() {
		return id;
	}
}
