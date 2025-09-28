package com.mttk.orche.admin.demo;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.startup.StartServer;


//web容器初始化时要跑的监听实�?
public class LoadServerListener implements ServletContextListener {

	String serverHome="D:\\biz\\development\\abic\\work";
	//重写   容器销毁时方法    把api impl相关服务 停止
	@Override  
	public void contextDestroyed(ServletContextEvent sce) {
	
		Server server=ServerLocator.getServer();
		try {
			server.stop();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
    //重写容器初始化时 启动api impl 相关服务 并把服务开启类的实例对象startServer 中的Server 放在容器上下文ServletContext 中
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//
		
		//
		System.out.println("Start loading from "+serverHome);
		// 
		System.setProperty("server.home", serverHome);
		//
		StartServer startServer=new StartServer();
		//
		Map<String,String> config=new HashMap<>();
		startServer.process(serverHome,config);
	}
}
