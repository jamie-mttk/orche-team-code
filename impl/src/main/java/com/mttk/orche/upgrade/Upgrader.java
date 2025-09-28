package com.mttk.orche.upgrade;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.impl.util.InitUtil;

public class Upgrader {
	private static Logger logger=null;;
	
	public void process(String serverHome) throws Exception{
		InitUtil.init(serverHome);
		logger=LoggerFactory.getLogger(Upgrader.class);
		//
		//
		File[] files=findPackage(serverHome);
		if (files==null||files.length==0) {
			logger.info("No upgrade package is found.Upgrade exit.");
		}else {
			try(UpgradeSupport upgradeSupport=new UpgradeSupport(serverHome)) {
				upgradeSupport.process(files);
			}
			//
			logger.info("Upgrade finished!");
		}
	}
	
	//
	private  File[] findPackage(String serverHome){
		String upgradePath=serverHome+File.separator+"data"+File.separator+"upgrade";
		File file=new File(upgradePath);
		if (!file.exists()||!file.isDirectory()) {
			return null;
		}
		//
		return file.listFiles();
	}
}
