package com.mttk.orche.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utiltities for convenience of manager file or directory
 */

public class FileHelper {
	/**
	 * Clear all the files under the given folder
	 * 
	 * @param folder
	 *            The folder to clear
	 * @param subFolder
	 *            if true,delete subFolder;otherwise,only files are cleard
	 * @throws IOException
	 */
	public static void clearDir(String dir, boolean subFolder)
			throws IOException {
		File fileDir = new File(dir);
		if (!fileDir.isDirectory()) {
			throw new IOException(dir + " is not a directory");
		}
		//
		File[] files = fileDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				files[i].delete();
				continue;
			}
			if (files[i].isDirectory() && subFolder) {
				clearDir(files[i].getPath(), subFolder);
				files[i].delete();
				continue;
			}
		}

	}

	/**
	 * Read file content into a byte arrary Not return String is because String
	 * is encoding sensitive
	 * 
	 * @param file
	 *            the file name to read from
	 */
	public static byte[] readFile(File file) throws IOException {
		Long size=file.length();
		ByteArrayOutputStream os=new ByteArrayOutputStream(size.intValue());
		//
		int len;
		byte[] bytes = new byte[1024];
		try(FileInputStream is = new FileInputStream(file)){
			while ((len = is.read(bytes))>0) {
					os.write(bytes, 0, len);
			}
			//
			return os.toByteArray();
		}		
	}

	/**
	 * Write byte array into file
	 * 
	 * @param bytes
	 *            byte array which will be written to file
	 * @param fileName
	 *            File name to store the byte array
	 * @param append
	 *            true,will append the file if it's exist;false,override. If
	 *            file is not exist,create a new one(Ignore isOverride).
	 */
	public static void writeFile(byte[] bytes, File file, boolean append)
			throws IOException {
		//
		//createDir(file.getParent()); // Create folder if needed
		FileOutputStream fos = new FileOutputStream(file, append);
		//
		try {
			fos.write(bytes);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * Write byte array into file
	 * 
	 * @param bytes
	 *            byte array which will be written to file
	 * @param fileName
	 *            File name to store the byte array
	 */
	private static void writeFile(byte[] bytes, File file) throws IOException {
		writeFile(bytes, file, false);
	}

	/**
	 * Copy file
	 * 
	 * @param fileName
	 * @param destPath
	 * @throws IOException
	 */
	public static void copyFile(String fileName, String destPath)
			throws IOException {
		// Read data
		byte[] data = readFile(new File(fileName));
		// Construct target file name
		File file = new File(fileName);
		String destFile = destPath + "/" + file.getName();
		// Write data
		writeFile(data, new File(destFile));
	}

	/**
	 * Move file
	 * 
	 * @param fileName
	 * @param destPath
	 * @throws IOException
	 */
	/*
	 * private static void moveFile(String fileName, String destPath) throws
	 * IOException { // Read data byte[] data = readFile(fileName); // Construct
	 * target file name File file = new File(fileName); String destFile =
	 * destPath + "/" + file.getName(); // Write data writeFile(data, destFile);
	 * // Delete file.delete(); }
	 */
	/**
	 * Move all the files/folders under the souce path (include the source path
	 * itself) into targetpath If souce is a directory,target is also regarded
	 * as a directory If souce is a file,target is also regarded as a file
	 * 
	 * @param source
	 * @param target
	 */
	public static void movePath(String source, String target)
			throws IOException {
		File souceFile = new File(source);
		File targetFile = new File(target);

		if (souceFile.isDirectory()) {
			targetFile.mkdirs();
			// Is directory
			File[] files = souceFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				movePath(files[i].getPath(), new File(targetFile, files[i]
						.getName()).getPath());
			}
			//
			souceFile.delete();
		} else {
			// Is file
			if (!souceFile.renameTo(targetFile)) {
				throw new IOException("Fail to rename from " + souceFile
						+ " to " + targetFile);
			}
		}
	}

	/**
	 * Create directory cascade
	 * @param file
	 * @return
	 * true  - create successfully
	 * false - not created
	 */
	public static boolean createDir(File file) {
		return createDir(file,100);
	}
	//加入maxLevel防止无限死循环
	//linux下目录不存在好像会死循环，不过没有模拟出来
	private static boolean createDir(File file,int maxLevel) {
		if (file==null){
			//null, no need to create
			return true;
		}
		if(maxLevel<=0) {
			return false;
		}
		if (file.exists()){
			//Already exist, return directly
			return true;
		}
		File fileParent=file.getParentFile();
		if (fileParent==null){
			//No parent,that means it is already the root folder 
			return true;
		}
	
		if (!fileParent.exists()){
			if (!createDir(fileParent,maxLevel-1)){
				//Changed by Jamie @2019/02/02
				//父亲目录不能创建有可能是并发引起的，因此继续创建子目录而不是退出
				//return false;
			}
		}
		//Create last level
		return file.mkdir();		
	}

	/**
	 * @param fileName
	 * @return
	 */
	/*
	 * private String clearFileName(String fileName) { if (fileName == null ||
	 * fileName.length() == 0) { return fileName; } // char[] chrs = new
	 * char[fileName.length()]; char chr; for (int i = 0; i < chrs.length; i++)
	 * { chr = fileName.charAt(i); // These characters is invalid in file
	 * name:\/:*?"<>| if (chr == '\\' || chr == '/' || chr == ':' || chr == '*'
	 * || chr == '?' || chr == '"' || chr == '<' || chr == '>' || chr == '|') {
	 * chr = ' '; } chrs[i] = chr; } // return new String(chrs); }
	 */

}

