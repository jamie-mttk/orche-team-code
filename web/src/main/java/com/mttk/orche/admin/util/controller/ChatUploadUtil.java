package com.mttk.orche.admin.util.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bson.Document;

import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.AgentExecuteService;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.FileHelper;
import com.mttk.orche.util.IOUtil;
import com.mttk.orche.util.StringUtil;

public class ChatUploadUtil {
    public static final File geTempPath() {
        File path = new File(ServerUtil.getPathTemp(ServerLocator.getServer()) + File.separator + "chatTemp");
        FileHelper.createDir(path);
        return path;

    }

    public static Document doUpload(HttpServletRequest request) throws Exception {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new ServletException("Upload file form should be encoded in multipart/form-data");
        }
        //
        DiskFileItemFactory factory = new DiskFileItemFactory(20 * 1024 * 1024, geTempPath());
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);
        for (FileItem item : items) {
            if (!item.isFormField()) {
                return dumpFile(item);
            }
        }
        //
        throw new ServletException("No file uploaded");
    }

    //
    private static Document dumpFile(FileItem item) throws Exception {
        File uploadPath = ServerUtil.getService(AgentExecuteService.class).getAgentUploadPath();
        // 生成一个唯一ID
        String id = StringUtil.getUUID();
        File outputFile = new File(uploadPath, id);
        try (InputStream is = item.getInputStream(); OutputStream os = new FileOutputStream(outputFile);) {
            //
            IOUtil.copy(is, os);
        }
        // 获取文件名
        return new Document().append("id", id).append("name", item.getName()).append("size", item.getSize());
    }

}
