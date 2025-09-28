package com.mttk.orche.util.dyna;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class OptionsUtil {
//	private static void displayClassLoader(ClassLoader cl) {
//		System.out.println(cl);
//		if (cl.getParent()!=null) {
//			displayClassLoader(cl.getParent());
//		}
//	}
	//options包含所有的class path,否则编译会失败
	public static List<String> buildOptions(ClassLoader cl) {
		//displayClassLoader(cl);
		//
		List<String> options = new ArrayList<String>();
		options.add("-classpath");
		StringBuilder sb = new StringBuilder();
		appendClassPath(sb,cl);
		options.add(sb.toString());
		//Refer to https://stackoverflow.com/questions/14617340/memory-leak-when-using-jdk-compiler-at-runtime
		options.add("-XDuseUnsharedTable=true");
		//System.out.println(options);
		//
		return options;
	}
	private static void appendClassPath(StringBuilder sb,ClassLoader cl) {
		if (cl instanceof URLClassLoader){		
			URLClassLoader urlClassLoader = (URLClassLoader) cl;
			for (URL url : urlClassLoader.getURLs()) {
			    sb.append(url.getFile()).append(File.pathSeparator);
			}
		}
		if (cl.getParent()!=null) {
			appendClassPath(sb,cl.getParent());
		}		
	}
}
