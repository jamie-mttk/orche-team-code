package com.mttk.orche.http.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.ServiceContext;
import com.mttk.orche.http.handler.util.HandlerHolder;
import com.mttk.orche.http.handler.util.HandlerHolderComparator;
import com.mttk.orche.http.handler.util.HandlerUtil;
import com.mttk.orche.util.StringUtil;

public class JettyHandler extends HandlerCollection {
	private static Logger logger = LoggerFactory.getLogger(JettyHandler.class);
	private AdapterConfig configRoot = null;
	private ServiceContext context = null;
	// private com.mttk.api.core.Server apiServer=null;
	// Record all the handlers
	// private Map<AdapterConfig, Handler> handlerMap = null;
	// // 应为map的key,按照匹配先后顺序
	// private List<AdapterConfig> handlerConfigList = null;
	// 包括所有初始化的Handler
	private List<HandlerHolder> handlerHolderList = null;

	public JettyHandler(ServiceContext context, AdapterConfig configRoot) throws Exception {
		this.context = context;
		this.configRoot = configRoot;
		//
		init();
	}

	private void init() throws Exception {
		if (!configRoot.containsKey("paths")) {
			return;
		}
		List<AdapterConfig> paths = configRoot.getBeanList("paths");
		//
		handlerHolderList = HandlerUtil.init(context, configRoot, paths);
		// 必须把所有handler设置到父亲handler,否则warHandler会返回404
		for (HandlerHolder handlerHolder : handlerHolderList) {
			addHandler(handlerHolder.getHandler());
		}

	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		//
		String uriMatchMode = configRoot.getString("uriMatchMode", "STARTWITH");

		//
		HandlerUtil.matchAndExecute(handlerHolderList, uriMatchMode, target, baseRequest, request, response);

	}
}
