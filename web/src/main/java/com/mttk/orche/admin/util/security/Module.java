package com.mttk.orche.admin.util.security;
//模块
public class Module {
	private String key;
	private String name;
	private String icon;
	private String parent;
	//
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Module)) {
			return false;
		}
		Module o=(Module)obj;
		return o.getKey().equals(getKey());
	}
	@Override
	public String toString() {
		return "Module [key=" + key + ", name=" + name + "]";
	}
}
