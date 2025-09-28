package com.mttk.orche.admin.util;

import java.io.File;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;

public class UploadUtil {
	//从request得到FileItem
	public static  UploadParseResult parse(HttpServletRequest request) throws Exception {
		UploadParseResult result=new UploadParseResult();
		//
		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new ServletException("Upload file form should be encoded in multipart/form-data");
		}
		DiskFileItemFactory factory = new DiskFileItemFactory(10 * 1024,
				new File(ServerLocator.getServer().getSetting(Server.PATH_TEMP, String.class)));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		List<FileItem> items = (List<FileItem>) upload.parseRequest(request);
		//
		for (FileItem item : items) {
			if (item.isFormField()) {
				result.getParas().put(item.getFieldName(),  item.getString());
			} else {
				result.getFiles().add(item);
			}
		}
		//
		return result;
	}
	//从request得到FileItem
	public static  FileItem findUploadFile(HttpServletRequest request) throws Exception {
		UploadParseResult result=parse(request);
		if (result.getFiles().size()>0) {
			return result.getFiles().get(0);
		}else {
			throw new Exception("uploadFile is emtpy");
		}
	}
}
