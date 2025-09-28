package com.mttk.orche.bootstrap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.mttk.orche.bootstrap.util.AddonUtil;


/**
 * A URL classloader to load class from the jar files under a path This class
 * will try load class from the given URL list first, and then try to use the
 * parent classloader if not found
 * 
 */
public class BootstrapClassLoader extends URLClassLoader {
	private static final URL[] EMPTY_URL_ARRAY = {};	
	public BootstrapClassLoader(String serverHome, ClassLoader classLoader)
			throws MalformedURLException,IOException {
		super(EMPTY_URL_ARRAY, classLoader);
		//
		addJars(serverHome+ File.separator + "lib");
		addJars(serverHome+ File.separator + "lib_user");
		//
		
	}
	//underAddon - 是否是addon目录下的(如果是addon的下级子目录,此值也为true)
	 public void addJars(String path)throws MalformedURLException,IOException {
			//System.out.println("Adding:"+path);
			File deployFile = new File(path);
			if (!deployFile.exists()) {
				return;
			}
			File[] files =deployFile.listFiles();

			//File[] files = deployFile.listFiles();
			if (files!=null){
				for (int i = 0; i < files.length; i++) {				
					if (files[i].isFile()) {
						if (files[i].getName().endsWith(".jar")){							
							if (AddonUtil.hasAddonLib(files[i])) {
								continue;
							}
							//过滤掉bootstrap.jar
							if (files[i].getName().equalsIgnoreCase("bootstrap.jar")) {
								//System.out.println("#####################"+files[i]);
								continue;
							}
							//
							addURL(files[i].toURI().toURL());		
							//System.out.println("ADDED:"+files[i].toURI().toURL());		
						}
					}else if (files[i].isDirectory()){
							addJars(files[i].getPath());
					}
				}	
			}
		}
//	 @Override
//	protected synchronized Class<?> loadClass(String name, boolean resolve)
//			throws ClassNotFoundException {
//		
//		// First, check if the class has already been loaded
//		Class c = findLoadedClass(name);
//		if (c == null) {
//			try {
//				// Try to find class in the given URL list
//				c = findClass(name);
//			} catch (ClassNotFoundException e) {
//				//System.out.println("#####"+name);
//				// If not found, then call parent class loader
//				/*
//				 * System.out.println(this.toString()+ " not found for "+name
//				 * +",try to use parent "+getParent());
//				 */
//				c = getParent().loadClass(name);
//			}
//		}
//		if (resolve) {
//			resolveClass(c);
//		}
//		return c;
//	}
//	 @Override
//	 public URL findResource(final String name) {
//		 URL url=super.findResource(name);
//		 if (name.endsWith(".json")) {
//		 System.out.println(name+"==>"+url+"\t"+this.getURLs());
//		 }
//		 return url;
//	 }
	 @Override
	 public String toString() {
		 StringBuilder sb=new StringBuilder(1024);
		 sb.append("Cloud Hub Bootstrap class loader:");
		 for(URL url:getURLs()) {
			 sb.append(url+"\t");
		 }
		 //
		 return sb.toString();
	 }
}
