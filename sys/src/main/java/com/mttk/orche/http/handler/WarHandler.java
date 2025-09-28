package com.mttk.orche.http.handler;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.AdapterConfig;

import com.mttk.orche.addon.ServiceContext;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.StringUtil;

public class WarHandler extends WebAppContext {
	private ServiceContext context = null;
	private AdapterConfig configRoot = null;
	private AdapterConfig config = null;
	private String welcomeFile = null;
	private String uri = null;
	private Logger logger = LoggerFactory.getLogger(WarHandler.class);

	public WarHandler(ServiceContext context, AdapterConfig configRoot, AdapterConfig config) {
		super();
		this.context = context;
		this.configRoot = configRoot;
		this.config = config;

		// this.setAttribute("AAA", "BBBB");
		// this.setInitParameter("CCC", "DDD");
		// this.setAttribute("chub.config.root", configRoot);
		// this.setAttribute("chub.config.path", config);
		//
		String resourceBase = config.getStringMandatory("targetWar");
		resourceBase = resourceBase.replace("${serverHome}", ServerUtil.getPathHome(context.getServer()));
		File file = new File(resourceBase);
		if (file.isDirectory()) {
			this.setResourceBase(resourceBase);
		} else {
			this.setWar(resourceBase);
		}
		//
		this.welcomeFile = config.getString("welcome");
		if (StringUtil.isEmpty(welcomeFile)) {
			welcomeFile = null;
		} else {
			if (!welcomeFile.startsWith("/")) {
				welcomeFile = "/" + welcomeFile;
			}
		}
		//
		uri = config.getString("uri");
		if (uri != null || !uri.equals("/")) {
			if (StringUtil.notEmpty(welcomeFile)) {
				welcomeFile = uri + welcomeFile;
			}
			//
			this.setContextPath(uri);
		}
		// 如果设置了tempDirectory,并且setPersistTempDirectory=false
		// 为了避免重启时会发生temp目录删除不掉而失败的情况，使用随机的目录

		this.setTempDirectory(new File(ServerUtil.getPathTemp(context.getServer()) + File.separator
				+ "jetty" + File.separator + StringUtil.getUUID()));
		logger.info(configRoot.get("name") + " of [" + uri + "] is using temp directory:" + this.getTempDirectory());
		this.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		// 设置为true,缺省值为false
		// 当为false，系统重启时会试图删除临时目录，导致失败因为他还在被占用
		// this.setPersistTempDirectory(true);
	}
	// 去掉URL中无法作为目录的部分
	// private String clearUrl(String url) {
	// url=url.replaceAll("/", "_");
	// //
	// return url;
	// }
	// @Override
	// public Resource getResource(String uriInContext)
	// throws MalformedURLException{
	//
	//
	// if ("/".equals(uriInContext)) {
	// uriInContext="/index.htm";
	// }
	// Resource r=super.getResource(uriInContext);
	// System.out.println(uriInContext+"==>"+r);
	// return r;
	// }

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (welcomeFile != null && "/".equals(target)) {
			response.sendRedirect(welcomeFile);
		} else {
			toCallSuperDoHandler(target, baseRequest, request, response);
		}

	}

	// 为了能够call super handler~
	private void toCallSuperDoHandler(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		super.doHandle(target, baseRequest, request, response);
	}

	@Override
	protected void loadConfigurations()
			throws Exception {
		// if the configuration instances have been set explicitly, use them
		if (super.getConfigurations().length != 0)
			return;

		if (super.getConfigurationClasses().length == 0) {
			super.setConfigurationClasses(Configuration.ClassList.serverDefault(getServer()));
		}
		// logger.info(
		// Thread.currentThread().getContextClassLoader()+"_configurationClasses="+super.getConfigurationClasses());
		Configuration[] l = new Configuration[super.getConfigurationClasses().length];
		int i = 0;
		for (String configClass : super.getConfigurationClasses()) {
			// checkAvailable(configClass);
			// logger.info("2222222222222222222222222222222222:"+Thread.currentThread().getContextClassLoader());
			// 这里做了修改,原始的实现没法load class(重启的时候)
			Configuration c = (Configuration) Class.forName(configClass).getDeclaredConstructor().newInstance();
			l[i++] = c;
		}

		super.setConfigurations(l);
	}
	//
	// private void checkAvailable(String className) {
	// logger.error("LOADER:"+Thread.currentThread().getContextClassLoader());
	// try {
	// Class.forName(className);
	// logger.info("Class is OK:"+className);
	// }catch(Exception e) {
	// logger.error("Class is not OK:"+className,e);
	// }
	// //
	// ClassLoader loader=null;
	// try {
	// loader=Thread.currentThread().getContextClassLoader();
	// Class cc= (loader==null ) ? Class.forName(className) :
	// loader.loadClass(className);
	// logger.info("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOK");
	// }catch(Exception e) {
	// logger.error("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEERROR:"+loader+"==>"+className,e);
	// }
	// }

	@Override
	public String toString() {
		return "War handler:" + this.getWar();
	}
}
