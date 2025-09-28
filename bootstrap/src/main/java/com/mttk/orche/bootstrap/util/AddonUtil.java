package com.mttk.orche.bootstrap.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonUtil {
	/**
	 * 判断给定的jar包是否有独有的jar或class
	 * 
	 * @param jarFile
	 * @return true:有独有的jar或class
	 */
	public static  boolean hasAddonLib(File file) throws IOException {
		if (!file.exists() && !file.isFile()) {
			return false;
		}
		try (JarFile jarFile = new JarFile(file)) {
			// 检测是否有ADDON_INFO
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (entry.isDirectory() || entry.getSize() == 0 || !name.startsWith("ADDON_INF/")) {
					continue;
				}
				if (name.startsWith("ADDON_INF/classes/")) {
					// 说明有class文件
					return true;
				}
				if (name.startsWith("ADDON_INF/lib/") && name.endsWith(".jar")) {
					// 说明有lib文件
					return true;
				}
			}
		}
		//
		return false;
	}
}
