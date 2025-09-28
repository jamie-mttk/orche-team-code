package com.mttk.orche.admin.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.model.Filters;
import com.mttk.orche.admin.controller.support.PersistableControllerBase;
import com.mttk.orche.admin.util.UploadUtil;
import com.mttk.orche.core.Server;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.DeployService;
import com.mttk.orche.service.support.DeployStrategy;
import com.mttk.orche.util.FileHelper;
import com.mttk.orche.util.StringUtil;

@RestController
@RequestMapping(value = "/deploy")

public class DeployController extends PersistableControllerBase {

	@Override
	public Class getServiceClass() {
		return DeployService.class;
	}

	@PostMapping(value = "/doDeploy")
	public Document doDeploy(HttpServletRequest request) throws Exception {
		FileItem uploadFile = UploadUtil.findUploadFile(request);
		Document doc = doDeploy(uploadFile);
		uploadFile.delete();
		return doc;
	}

	@Override
	protected Bson parseCriteriaSingle(String key, Object value) throws Exception {
		// 由于suppress如果通过参数传是字符类型导致无法查询�?因此修改为Boolean类型
		if ("suppress".equals(key)) {
			Boolean suppress = null;
			if (value instanceof Boolean) {
				suppress = (Boolean) value;
			} else if (value instanceof String) {
				suppress = Boolean.valueOf((String) value);
			}
			if (suppress != null) {
				return Filters.eq("suppress", suppress);
			} else {
				// 忽略此条�?
				return null;
			}
		} else {
			return super.parseCriteriaSingle(key, value);
		}
	}

	@PostMapping(value = "/suppress")
	public Document suppress(String id) throws Exception {
		boolean result = ServerLocator.getServer().getService(DeployService.class).suppress(id);
		//
		return new Document().append("success", result);
	}

	@PostMapping(value = "/unsuppress")
	public Document unsuppress(String id) throws Exception {
		boolean result = ServerLocator.getServer().getService(DeployService.class).unsuppress(id);
		//
		return new Document().append("success", result);
	}

	private Document doDeploy(FileItem uploadFile) throws Exception {
		Server server = ServerLocator.getServer();
		// 生成唯一的文件名
		File file = new File(server.getSetting(Server.PATH_TEMP, String.class) + File.separator + "deploy"
				+ File.separator + StringUtil.getUUID());
		// 确保路径存在
		FileHelper.createDir(file.getParentFile());
		//
		uploadFile.write(file);
		//
		try {

			DeployService deployService = server.getService(DeployService.class);
			return deployService.deploy(file, uploadFile.getName(), null, new DeployStrategy());
		} finally {
			if (!file.delete()) {
				file.deleteOnExit();
			}
		}
	}

}
