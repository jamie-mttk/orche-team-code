package com.mttk.orche.core.impl.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.bootstrap.util.AddonUtil;
import com.mttk.orche.core.Server;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.FileHelper;
import com.mttk.orche.util.IOUtil;
import com.mttk.orche.util.StringUtil;

public class AddonLoaderSupport {
	//
	private Server server = null;
	// chub的class loader
	private ClassLoader parentClassLoader = null;
	// 缺省的classloader
	// private ClassLoader classLoaderDefault = null;
	// 存放addon文件名对应的ClassLoader
	private Map<String, ClassLoader> map = null;
	//
	private static Logger logger = LoggerFactory.getLogger(AddonLoaderSupport.class);

	//
	public AddonLoaderSupport(Server server, ClassLoader parentClassLoader) throws Exception {
		this.server = server;
		this.parentClassLoader = parentClassLoader;
		//
		// this.classLoaderDefault = buildDefaultClassLoader();
		//
		buildClassLoaderMap();
		//

	}

	public ClassLoader obtain(String name) {
		if (StringUtil.isEmpty(name)) {
			return parentClassLoader;
		}
		ClassLoader cl = map.get(name);
		if (cl == null) {
			cl = parentClassLoader;
		}
		// System.out.println(name+"==>"+cl);
		return cl;
	}

	// 创建一个缺省的addon class loader,不包括带有自身class或jar的addon
	// private ClassLoader buildDefaultClassLoader() throws Exception {
	// List<URL> urls = new ArrayList<>(20);
	// File[] files = new File(ServerUtil.getPathHome(server) + File.separator +
	// "lib" + File.separator + "addon")
	// .listFiles();
	// for (File file : files) {
	// if (file.isFile()) {
	// if (!hasAddonLib(file)) {
	// addFiles(urls, file);
	// }
	// } else if (file.isDirectory()) {
	// addFiles(urls, file);
	// }
	// }
	// //
	// return new URLClassLoader(urls.toArray(new URL[0]), parentClassLoader);
	// }

	// private void addFiles(List<URL> urls, File file) throws Exception {
	// if (file.isFile()) {
	// urls.add(file.toURI().toURL());
	// } else if (file.isDirectory()) {
	// File[] files = file.listFiles();
	// for (File f : files) {
	// addFiles(urls, f);
	// }
	// }
	// }

	private void buildClassLoaderMap() throws Exception {
		map = new ConcurrentHashMap<>();
		//
		buildClassLoaderIfNeeded(
				new File(ServerUtil.getPathHome(server) + File.separator + "lib" + File.separator + "addon"));
		buildClassLoaderIfNeeded(
				new File(ServerUtil.getPathHome(server) + File.separator + "lib_user" + File.separator + "addon"));
	}

	// 检查Folder以及子目录下的所有jar包,如果是需要创建独立的classloader的，则创建并放到map里
	private void buildClassLoaderIfNeeded(File folder) throws Exception {
		// 得到所有addonJar
		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		//
		for (File file : files) {
			// logger.info("Start build classloader map for:"+addon);
			if (file.isDirectory()) {
				buildClassLoaderIfNeeded(file);
				continue;
			}
			if (!file.isFile()) {
				continue;
			}
			if (!AddonUtil.hasAddonLib(file)) {
				logger.info("No need to create seperate classloader for:" + file);
				continue;
			}
			//
			ClassLoader cl = buildClassloader(ServerUtil.getPathTemp(server), parentClassLoader, file);
			logger.info("Create seperate classloader [" + cl + "] for:" + file);
			//
			map.put(file.getName(), cl);
		}
	}

	// 把jarFile中的ADDON_INF/classes和ADDON_INF/jar解压到sADDON_INF/classes(下面子目录)下后创建class
	// loader
	private ClassLoader buildClassloader(String tempPath, ClassLoader parent, File file) throws Exception {
		// 得到存放临时文件的目录
		String rootPath = tempPath + File.separator + "addonLib" + File.separator + file.getName();
		if (rootPath.endsWith(".jar")) {
			rootPath = rootPath.substring(0, rootPath.length() - 4);
		}

		// 解压
		extract(rootPath, file);
		ArrayList<URL> urls = new ArrayList<>(10);
		// 本文件
		urls.add(file.toURI().toURL());

		// classes
		urls.add(new File(rootPath + File.separator + "classes").toURI().toURL());
		// jar files
		File libDir = new File(rootPath + File.separator + "lib");
		File[] files = libDir.listFiles();
		for (File f : files) {
			urls.add(f.toURI().toURL());
		}
		// 创建class loader
		URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]), parent);
		for (URL url : urls) {
			logger.info("Sepereate addon classloader: " + cl + " has " + url);
		}
		// if ("odooAddon.jar".equals(file.getName())) {
		// try {
		// cl.loadClass("org.apache.xmlrpc.client.XmlRpcClientConfig");
		// logger.info("@@@@@@@@@@");
		// }catch(Exception e) {
		// logger.info("########");
		// e.printStackTrace();
		// }
		// }
		//
		return cl;
	}

	private void extract(String rootPath, File file) throws IOException {
		try (JarFile jarFile = new JarFile(file)) {
			// 检测是否有ADDON_INFO
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (entry.isDirectory() || entry.getSize() == 0 || !name.startsWith("ADDON_INF/")) {
					continue;
				}
				String finalPath = null;
				if (name.startsWith("ADDON_INF/classes/")) {
					// 说明是class文件
					finalPath = name.substring("ADDON_INF".length());
				}
				if (name.startsWith("ADDON_INF/lib/") && name.endsWith(".jar")) {
					// 说明是lib文件
					finalPath = name.substring("ADDON_INF".length());
				}
				if (StringUtil.notEmpty(finalPath)) {
					//
					File finalFile = new File(rootPath + finalPath);
					FileHelper.createDir(finalFile.getParentFile());
					//
					try (OutputStream os = new FileOutputStream(finalFile)) {
						try (InputStream is = jarFile.getInputStream(entry)) {
							IOUtil.copy(is, os);
						}
					}
				}
			}
		}
	}

	//
	public static void main(String args[]) throws Exception {
		String path = "D:\\biz\\development\\abic\\work\\lib\\addon\\";
		// System.out.println("simpleAddon.jar==>" + hasAddonLib(path +
		// "simpleAddon.jar"));
		// System.out.println("tempAddon.jar==>"+hasAddonLib(path+"tempAddon.jar"));
		// System.out.println("utilAddon.jar==>"+hasAddonLib(path+"utilAddon.jar"));
		// System.out.println("noAddon.jar==>"+hasAddonLib(path+"noAddon.jar"));
		ClassLoader cl = new AddonLoaderSupport(null, null).buildClassloader("D:\\biz\\development\\abic\\work\\temp",
				Thread.currentThread().getContextClassLoader(), new File(path + "simpleAddon.jar"));

		System.out.println("1:" + cl.loadClass("com.mttk.orche.simulate.simple.Simple1").getClassLoader());
		System.out.println("2:" + cl.loadClass("com.mttk.orche.simulate.simple.Simple2"));
		//
		Thread.currentThread().setContextClassLoader(cl);
		Class.forName("com.mttk.orche.simulate.simple.Simple1");
	}
}
