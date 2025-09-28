package com.mttk.orche.dynaLoad;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


/**
 * A wrap of URL classloader which support jar file
 * 
 */
public class JarFileClassLoader extends URLClassLoader {
	private static final URL[] EMPTY_URL_ARRAY = {};
	private List<JarFileWrapper> jarFileList=new ArrayList<JarFileWrapper>(20);
	public JarFileClassLoader(ClassLoader classLoader)
			{
		super(EMPTY_URL_ARRAY, classLoader);
	}
	 public void addJarFile(File jarFile) throws MalformedURLException,IOException{
	   addURL(jarFile.toURI().toURL());
	   //
	   jarFileList.add(new JarFileWrapper(jarFile));
	 }
	 //变成可访问
	 @Override
	 public  void addURL(URL url) {
		 super.addURL(url);
	 }
  public List<JarFileWrapper> getJarFileList()
  {
    return jarFileList;
  }
@Override
public String toString() {
	return "JarFileClassLoader [jarFileList=" + jarFileList + "]";
}
	 
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
  
}
