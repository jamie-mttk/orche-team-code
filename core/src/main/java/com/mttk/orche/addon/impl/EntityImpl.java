package com.mttk.orche.addon.impl;

import java.util.Map;

import com.mttk.orche.addon.Entity;

/**
 * Entity实现类
 *
 * @param <V>
 */
public abstract class EntityImpl<V> extends EntityReadonlyImpl<V> implements Entity<V> {
	public EntityImpl() {
		super();
	}

	public EntityImpl(Map<String, Object> map) {
		super(map);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public void put(String key, Object value) {
		map.put(key, value);
	}

	@Override
	public Object remove(String key) {
		return map.remove(key);
	}
}