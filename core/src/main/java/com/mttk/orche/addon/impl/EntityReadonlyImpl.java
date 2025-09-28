package com.mttk.orche.addon.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mttk.orche.addon.EntityReadonly;
import com.mttk.orche.util.StringUtil;

/**
 * EntityReadonly实现类
 *
 * @param <T>
 */
public abstract class EntityReadonlyImpl<T> implements EntityReadonly<T> {
	//
	protected Map<String, Object> map = null;

	public EntityReadonlyImpl() {
		map = new HashMap<>();
	}

	public EntityReadonlyImpl(Map<String, Object> map) {
		if (map == null) {
			this.map = new HashMap<>();
		} else {
			this.map = map;
		}
	}

	@Override
	public Object get(String key) {
		Object value = map.get(key);
		if (StringUtil.isEmpty(value)) {
			return null;
		} else {
			return value;
		}
	}

	@Override
	public Object getMandatory(String key) {
		return getMandatory(key, Object.class);
	}

	@Override
	public boolean containsKey(String key) {
		return get(key) != null;
	}

	@Override
	public String getId() {
		Object obj = get("_id");
		if (obj == null) {
			return null;
			// }else if (obj instanceof ObjectId){
			// return ((ObjectId)obj).toString();
		} else {
			return obj.toString();
		}
	}

	@Override
	public String getString(String key) {
		Object value = get(key);
		if (value != null && value instanceof String) {
			return (String) value;
		} else {
			return null;
		}
	}

	@Override
	public String getString(String key, String defValue) {
		Object value = get(key);
		if (value != null && value instanceof String) {
			return (String) value;
		} else {
			return defValue;
		}
	}

	@Override
	public String getStringMandatory(String key) {
		return getMandatory(key, String.class);
	}

	@Override
	public Boolean getBoolean(String key) {
		Object value = get(key);
		if (value != null && value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return null;
		}
	}

	@Override
	public Boolean getBoolean(String key, Boolean defValue) {
		Object value = get(key);
		if (value != null && value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return defValue;
		}
	}

	@Override
	public Integer getInteger(String key) {
		Object value = get(key);
		if (value != null && value instanceof Integer) {
			return (Integer) value;
		} else {
			return null;
		}
	}

	@Override
	public Integer getInteger(String key, Integer defValue) {
		Object value = get(key);
		if (value != null && value instanceof Integer) {
			return (Integer) value;
		} else {
			return defValue;
		}
	}

	@Override
	public Integer getIntegerMandatory(String key) {
		return getMandatory(key, Integer.class);
	}

	@Override
	public <T> T get(String key, Class<T> retClass) {
		Object value = get(key);
		if (value != null && retClass.isInstance(value)) {
			return retClass.cast(value);
		} else {
			return null;
		}
	}

	@Override
	public <T> T get(String key, Class<T> retClass, T defValue) {
		Object value = get(key);
		if (value != null && retClass.isInstance(value)) {
			return retClass.cast(value);
		} else {
			return defValue;
		}
	}

	@Override
	public <T> T getMandatory(String key, Class<T> retClass) {
		T value = get(key, retClass);
		if (value == null) {
			throw new RuntimeException("No value is found for key:" + key);
		}
		return value;
	}

	@Override
	public Set<java.lang.String> keySet() {
		return map.keySet();
	}

	@Override
	public Map<String, Object> toMap() {
		return map;
	}

	@Override
	public String toString() {
		if (map == null) {
			return null;
		} else {
			return map.toString();
		}
	}
}