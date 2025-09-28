package com.mttk.orche.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
	public static void unzip(File zipFile, File destDir) throws IOException {

		// 创建输出目录如果它不存在
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		//
		try (FileInputStream fis = new FileInputStream(zipFile)) {
			try (ZipInputStream zis = new ZipInputStream(fis)) {
				ZipEntry ze = null;
				while ((ze = zis.getNextEntry()) != null) {
					String fileName = ze.getName();
					File newFile = new File(destDir, fileName);
					if (ze.isDirectory()) {
						// 是目录
						newFile.mkdirs();
					} else {
						// 创建所有非存在的父目录 - 应该不需要
//						newFile.getParentFile().mkdirs();
						//
						try (FileOutputStream fos = new FileOutputStream(newFile)) {
							IOUtil.copy(zis, fos);
						}
					}
					// 关闭当前ZipEntry并移至下一个
					zis.closeEntry();
				}
				// 关闭最后一个ZipEntry
				zis.closeEntry();
			}
		}
	}
}
