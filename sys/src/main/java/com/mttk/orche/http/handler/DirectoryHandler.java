package com.mttk.orche.http.handler;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.util.StringUtil;

/**
 * 此handler处理目录访问相关,类似一个简单的web服务器实现
 */
public class DirectoryHandler extends ResourceHandler {
	private AdapterConfig config=null;	
	public DirectoryHandler(AdapterConfig config) {
		super();
		//
		this.config=config;
		//
		setResourceBase(config.getStringMandatory("targetDirectory"));
		//setResourceBase("C:\\java\\apache-tomcat-8.5.4\\webapps\\examples\\");
		if (config.containsKey("indexFile")) {
			String indexFile=config.getString("indexFile");
			if (StringUtil.notEmpty(indexFile)) {
				setWelcomeFiles(indexFile.split(","));
			}
		}
		//
		setDirectoriesListed(config.getBoolean("dirListing", false));
	}
	@Override
	public Resource getResource(String path) {
		String uri=config.getString("uri");
		//System.out.println("@@@@@@@@@@@@@@@@@@:"+path+"==>"+uri);
		if (path.startsWith(uri)) {
			path=path.substring(uri.length());
		}
		if (StringUtil.isEmpty(path)) {
			path="/";
		}
		Resource r=super.getResource(path);
		//System.out.println("@@@@@@@@@@@@@@@@@@2:"+path+"==>"+r);
		return r;
	}
	@Override
	public String toString() {
		return "Directory handler:"+getResourceBase();
	}
}
