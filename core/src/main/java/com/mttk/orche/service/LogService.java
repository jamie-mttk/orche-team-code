package com.mttk.orche.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.mttk.orche.core.Service;

public interface LogService extends Service {
//	public static final String NAME="name";						//文件名
//	public static final String LAST_MODIFIED="lastModified";	//文件最后修改时间,Date类型
//	public static final String SIZE="size";									//文件大小
	
	//List all log files
	//Entity里包括key:NAME,LAST_MODIFIED,SIZE  name,size,lastModify
	List<LogFile> list(String nameFilter)throws  Exception ;
	//Load log file, the caller takes responsibility of closing it
	//And, the implementation class should check that the name is a GOOD name which means there is no "..","/" or "\"
	InputStream load(String name)throws Exception ;
	public class LogFile{
		//文件名,包含子目录名
		private String name;
		//文件大小
		private long size;
		//最后修改日期
		private Date lastModified;
		public LogFile() {
			super();
		}
		public LogFile(String name, long size, Date lastModified) {
			super();
			this.name = name;
			this.size = size;
			this.lastModified = lastModified;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public long getSize() {
			return size;
		}
		public void setSize(long size) {
			this.size = size;
		}
		public Date getLastModified() {
			return lastModified;
		}
		public void setLastModified(Date lastModified) {
			this.lastModified = lastModified;
		}
		
	}	
}
