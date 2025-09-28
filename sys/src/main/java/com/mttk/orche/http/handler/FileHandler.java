package com.mttk.orche.http.handler;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.mttk.orche.addon.AdapterConfig;

/**
 * 处理目标为一固定文件
 */
public class FileHandler extends ResourceHandler {
	private AdapterConfig config=null;
	public FileHandler(AdapterConfig config) {
		super();
		//
		this.config=config;
		//
		setResourceBase(config.getString("targetFile"));
		//
		setDirectoriesListed(false);
	}
	@Override
	public Resource getResource(String path) {
		//不管用户实际路径都返回根节点
		path="/";
		//
		Resource r=super.getResource(path);
		//System.out.println("@@@@@@@@@@@@@@@@@@2:"+path+"==>"+r);
		return r;
	}
	@Override
	public String toString() {
		return "File handler:"+getResourceBase();
	}
}
