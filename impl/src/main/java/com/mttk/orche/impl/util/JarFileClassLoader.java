package com.mttk.orche.impl.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


/**
用于加载jarFile
 * 
 */
public class JarFileClassLoader extends URLClassLoader {
	private static final URL[] EMPTY_URL_ARRAY = {};	
	public JarFileClassLoader(String jarFile, ClassLoader classLoader)
			throws MalformedURLException {
		super(EMPTY_URL_ARRAY, classLoader);
		//
		this.addURL(new File(jarFile).toURI().toURL());
	}
	public JarFileClassLoader(File jarFile, ClassLoader classLoader)
			throws MalformedURLException {
		super(EMPTY_URL_ARRAY, classLoader);
		//
		this.addURL(jarFile.toURI().toURL());
	}
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		
		// First, check if the class has already been loaded
		Class c = findLoadedClass(name);
		if (c == null) {
			try {
				// Try to find class in the given URL list
				c = findClass(name);
			} catch (ClassNotFoundException e) {
				//System.out.println("#####"+name);
				// If not found, then call parent class loader
				/*
				 * System.out.println(this.toString()+ " not found for "+name
				 * +",try to use parent "+getParent());
				 */
				try {
					//当getParent()为Jetty的org.eclipse.jetty.webapp.WebAppClassLoader好像有问题
					//不会自动加载parent的class loader，所以这里自行用父亲的父亲处理
					c = getParent().loadClass(name);
				} catch (ClassNotFoundException e1) {
					if (getParent().getParent()!=null) {
						c = getParent().getParent().loadClass(name);
					}
				}
			}
		}
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}
}
