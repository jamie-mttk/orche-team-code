package com.mttk.orche.upgrade;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.impl.util.MongoSupport;
import com.mttk.orche.util.FileHelper;
import com.mttk.orche.util.IOUtil;
import com.mttk.orche.util.PatternUtil;
import com.mttk.orche.util.StringUtil;

public class UpgradeSupport implements Closeable {
	private static Logger logger = LoggerFactory.getLogger(UpgradeSupport.class);
	private static final String INFO_FILE="upgrade.json";
	private String serverHome;
	private MongoSupport mongoSupport;
	public UpgradeSupport(String serverHome) {
		this.serverHome=serverHome;
	}
	@Override
	public void  close() {
		if (mongoSupport!=null) {
			try {
			mongoSupport.close();
			}catch(Exception ignore) {
				
			}
		}
	}
	//
	public void process(File[] files) throws Exception {
		
		for (File file : files) {
			if (!file.isFile()||!file.getName().endsWith(".zip")) {
				logger.info("Since it is not a upgrade file,ignore:" + file);
			} else {
				process( file);
				logger.info("Finish process upgrade package:"+file);
			}
		}
	}

	private void process(File file) throws Exception {
		try (ZipFile zipFile = new ZipFile(file)) {
			//package.json
			Document info = loadInfo(zipFile, file);
			if (info == null) {
				logger.error("No "+INFO_FILE+" is found in file:" + file);
				return;
			}
			//
			executeCommands(info);
			// 
			copyFiles(zipFile);
		}
		//
		File fileNew=new File(file.getPath()+".done");
		boolean result=file.renameTo(fileNew);
		logger.info("Rename file to :"+fileNew
				+" returns:"+ result);
		
		//System.out.println(serverHome + "==>" + file);
	}

	private Document loadInfo(ZipFile zipFile, File file) throws Exception {
		ZipEntry packageEntry = zipFile.getEntry(INFO_FILE);
		if (packageEntry == null) {

			return null;
		}
		try (InputStream is = zipFile.getInputStream(packageEntry)) {
			byte[] data = IOUtil.toArray(is);
			return Document.parse(new String(data, "utf-8"));
		}
	}

	private void executeCommands(Document info) throws Exception {
		List<Document> commands = (List<Document>) info.get("commands");
		if (commands == null || commands.size() == 0) {
			logger.info("No command is found");
			return;
		}
		for (Document command : commands) {
			executeCommand(command);
		}
	}

	private void executeCommand(Document command) throws Exception {
		String type=command.getString("type");
		if ("remove".equalsIgnoreCase(type)) {
			executeCommandRemove(command);
		}else if ("db".equalsIgnoreCase(type)) {
			executeCommandDb(command);
		}else {
			logger.error("Unsuported command type:"+command);
		}
	}
	private void executeCommandRemove(Document command) throws Exception {
		Pattern pattern=buildPattern(command);
		if (StringUtil.isEmpty(pattern)) {
			//it is not allowed to move all files under server home
			logger.error("No pattern is found in remove command:"+command);			
			return;
		}
		//
		String folder=command.getString("folder");
		if (StringUtil.isEmpty(folder)) {
			//it is not allowed to move all files under server home
			logger.error("No folder parameter is found in remove command:"+command);			
			return;
		}
		File f=new File(serverHome+File.separator+folder);
		
		for(File file:f.listFiles()) {
			if (!file.isFile()) {
				//
				continue;
			}		
			//
			if(pattern.matcher(file.getName()).matches()) {
				if(!file.delete()) {
					logger.error("Delete file failed:"+file+",set it to be delete during JVM shutdown");
					file.deleteOnExit();
				}else {
					logger.info("Delete file:"+file);
				}
			}
		}
	}
	private Pattern buildPattern(Document command) {
		String p=command.getString("pattern");
		if (StringUtil.isEmpty(p)) {
			return null;
		}
		p=PatternUtil.wildCardTransform(p);
		//
		return Pattern.compile(p);
	}
private void executeCommandDb(Document command) throws Exception {
		if (mongoSupport==null) {
			mongoSupport=new MongoSupport(serverHome);
		}
		//
		Document c=(Document)command.get("command");
		if (c==null) {
			logger.error("Invalid db command:"+command);
			return;
		}
		logger.info("Start execute DB command:"+c);
		try {
			//Command 语法参考
			//https://docs.mongodb.com/manual/reference/command/delete/
			Document result=mongoSupport.getMongoDatabase().runCommand(c);
			logger.info("DB command returns:"+result);
		}catch(Exception e) {
			logger.error("Execute DB command failed",e);
		}
	}
private void copyFiles(ZipFile zipFile) throws Exception {
	Enumeration<? extends ZipEntry> e=zipFile.entries();
	ZipEntry zipEntry=null;
	while(e.hasMoreElements()) {
		//System.out.println(zipEntry);
		zipEntry=e.nextElement();
		if (zipEntry.isDirectory()) {
			continue;
		}
		//
		if (INFO_FILE.equals(zipEntry.getName())) {
			continue;
		}
		if(!zipEntry.getName().startsWith("copy/")) {
			continue;
		}
		String name=zipEntry.getName();
		name=name.substring("copy/".length());
		//
		File target=new File(serverHome+File.separator+name);
		//
		//ȷ
		FileHelper.createDir(target.getParentFile());
		try (InputStream is = zipFile.getInputStream(zipEntry)) {
			try(OutputStream os=new FileOutputStream(target)){
				IOUtil.copy(is, os);
			}
		}
		//
		logger.info("File copied:"+target);
	}	
}
}
