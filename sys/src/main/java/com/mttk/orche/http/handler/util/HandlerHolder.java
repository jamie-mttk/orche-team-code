package com.mttk.orche.http.handler.util;

import org.eclipse.jetty.server.Handler;

import com.mttk.orche.addon.AdapterConfig;

//用于存放初始化后的 Handler以及对应的 AdapterConfig
public class HandlerHolder {
	private AdapterConfig adapterConfig=null;
	private Handler handler=null;
	//
	public HandlerHolder(AdapterConfig adapterConfig, Handler handler) {
		super();
		this.adapterConfig = adapterConfig;
		this.handler = handler;
	}
	//
	public AdapterConfig getAdapterConfig() {
		return adapterConfig;
	}
	public Handler getHandler() {
		return handler;
	}
	@Override
	public String toString() {
		return handler+"\t"+adapterConfig;
	}
	
}
