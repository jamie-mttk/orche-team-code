package com.mttk.orche.util;

import java.io.FileInputStream;
import java.util.Properties;
/**
 * 属性文件处理相关
 * 
 *
 */
public class PropertiesUtil {

	//从文件里读出配置
	public static Properties load(String file) throws Exception{
		Properties props=new Properties();
		FileInputStream is=new FileInputStream(file);
		try {
			props.load(is);
		}finally {
			IOUtil.safeClose(is);
		}
		return props;
	}
}
