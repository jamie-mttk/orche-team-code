package com.mttk.orche.bootstrap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class UpgradeClassLoader extends URLClassLoader {
	private static final URL[] EMPTY_URL_ARRAY = {};	
	public UpgradeClassLoader(String serverHome,ClassLoader classLoader)throws IOException{
		super(EMPTY_URL_ARRAY, classLoader);
		//
		addFiles(serverHome,"lib");	

	}
	//把subPath下符合pattern的文件都加入到classLoader里
	private void addFiles(String serverHome,String subPath)  throws IOException{
		//确保目录存在
		String targetFolder=serverHome+File.separator+"temp"+File.separator+"lib";
		Path parentPath=Paths.get(new File(targetFolder).toURI());
		Files.createDirectories(parentPath);
		//
		addFiles(serverHome,new File(serverHome+File.separator+subPath));
	}
	private void addFiles(String serverHome,File file)  throws IOException{
		
		if (file.isFile()) {
			addFile(serverHome,file);
		}else if (file.isDirectory()) {
				for(File f:file.listFiles()) {
					//System.out.println(f+"###"+file.isFile()+":::"+file.isDirectory());
					addFiles(serverHome,f);
			}
		}
	}
	//把给定文件加入到class loader(先拷贝一份)
	private void addFile(String serverHome,File file) throws IOException{
		File  targetFile=new File(serverHome+File.separator+"temp"+File.separator+"lib"+File.separator+file.getName());
	
		//拷贝一份出来
		Files.copy(Paths.get(file.toURI()), Paths.get(targetFile.toURI()),  StandardCopyOption.REPLACE_EXISTING);
		//
		addURL(targetFile.toURI().toURL());
		//
		System.out.println("File added into class path:"+targetFile);
	}
	
}
