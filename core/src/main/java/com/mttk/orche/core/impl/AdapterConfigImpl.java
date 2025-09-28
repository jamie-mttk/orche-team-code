package com.mttk.orche.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.impl.EntityReadonlyImpl;

/**
 * AdapterConfig实现类
 *
 */
public class AdapterConfigImpl extends EntityReadonlyImpl<AdapterConfig> implements AdapterConfig {
	public AdapterConfigImpl() {
	}

	public AdapterConfigImpl(Map<String, Object> map) {
		super(map);
	}

	@Override
	public AdapterConfig getBean(String key) {
		Map<String, Object> map = (Map<String, Object>) get(key, Map.class);
		if (map == null) {
			return null;
		} else {
			return new AdapterConfigImpl(map);
		}
	}

	@Override
	public List<AdapterConfig> getBeanList(String key) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) get(key, List.class);
		if (list == null) {
			return null;
		}
		List<AdapterConfig> newList = new ArrayList<>(list.size());
		list.forEach((m) -> {
			newList.add(new AdapterConfigImpl(m));
		});
		return newList;
	}
}
