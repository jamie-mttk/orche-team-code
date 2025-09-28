package com.mttk.orche.deploy.support;

import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.core.Server;
import com.mttk.orche.util.StringUtil;
/**
 * 处理包中所有的文件
 *
 */
public class PackageParser {
	private static Logger logger=LoggerFactory.getLogger(PackageParser.class);
	public static  DeployResult parse(Server server,File file,String name,String pattern) throws Exception{
		DeployResult result=new DeployResult();		
		
		try(JarFile jarFile=new JarFile(file,false, JarFile.OPEN_READ)) {
			Pattern p=null;
			if (StringUtil.notEmpty(pattern)) {
				p=Pattern.compile(pattern);
			}
			//为jar创造一个新的classloader,因为加载的类不存在于本地的jar中
//			ClassLoader cl=Thread.currentThread().getContextClassLoader();
//			JarFileClassLoader classLoader=new JarFileClassLoader(file, Thread.currentThread().getContextClassLoader());
			ClassLoader classLoader=server.obtainClassLoader(name);
			//logger.error("###########"+classLoader);
			//
			Enumeration<JarEntry> entries=jarFile.entries();
			int count=0;
			JarEntry jarEntry=null;
			while(entries.hasMoreElements()) {
				jarEntry=entries.nextElement();
				if(handleJarEntry(result, p, classLoader,jarEntry)){
					//System.out.println("@@@"+jarEntry.getName()+"===>"+jarEntry);
					count++;
				}
			}
			//
		//	classLoader.close();			
			//
			logger.info("Totally processed (Each class may has zero or more than one action/entry) ["+count+"] classes in file:"+file);
		}
		//
		return result;
	}
	private static boolean handleJarEntry(DeployResult result,Pattern pattern,ClassLoader classLoader,JarEntry jarEntry){
		//如果是目录不处理
		if (jarEntry.isDirectory()) {
			return false;
		}
		//
		String name=jarEntry.getName();
		//如果不是以.class结尾，不处理
		if (!name.endsWith(".class")) {
			return false;
		}
	//	System.out.println("******"+name);
		if (name.startsWith("ADDON_INF")) {
			return false;
		}
		//去掉结尾的.class
		name=name.substring(0, name.length()-6);
		//把所有"/"替换成"."，成为class路径的样子
		name=name.replace('/', '.');
		//模式匹配
		if (pattern!=null) {
			if (!pattern.matcher(name).matches()) {
				return false;
			}
		}
		//
		Class clazz=null;
		try {
			//org.eclipse.jetty.server.handler.ResourceHandler a;
			clazz=classLoader.loadClass(name);
		}catch(Throwable t) {
			String errInfo="Fail to deploy class:"+name;
			result.addThrowable(new Exception(errInfo,t));
			logger.error(errInfo,t);
			return false;
		}
		//
		try {
			SingleClassParser.analysis(result,clazz);
			return true;
		}catch(Throwable t) {
			String errInfo="Fail to analysis class:"+clazz;
			result.addThrowable(new Exception(errInfo,t));
			logger.error(errInfo,t);
			return false;
		}
	}
}
