package com.mttk.orche.admin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

//从request对象中解析出来的结果
public class UploadParseResult {
	private Map<String, String> paras = new HashMap<>();
	private List<FileItem> files = new ArrayList<>();

	//
	public Map<String, String> getParas() {
		return paras;
	}

	public List<FileItem> getFiles() {
		return files;
	}

	public FileItem getFile() {
		if (files.size() == 0) {
			throw new RuntimeException("No file is uploaded");
		}
		return files.get(0);
	}

	public void clearFiles() {
		if(files==null) {
			return;
		}
		for (FileItem fileItem : files) {
			try {
				fileItem.delete();
			} catch (Throwable ignore) {
			}
		}
	}

}
