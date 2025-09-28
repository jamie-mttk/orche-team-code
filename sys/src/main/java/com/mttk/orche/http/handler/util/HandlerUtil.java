package com.mttk.orche.http.handler.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.ServiceContext;
import com.mttk.orche.http.handler.DirectoryHandler;
import com.mttk.orche.http.handler.FileHandler;

import com.mttk.orche.http.handler.RedirectHandler;
import com.mttk.orche.http.handler.WarHandler;
import com.mttk.orche.util.StringUtil;

public class HandlerUtil {
	private static Logger logger = LoggerFactory.getLogger(HandlerUtil.class);

	// 初始化handlerMap和handlerConfigList
	public static List<HandlerHolder> init(ServiceContext context, AdapterConfig configRoot, List<AdapterConfig> paths)
			throws Exception {
		List<HandlerHolder> handlerHolderList = new ArrayList<>(paths.size());
		//
		for (AdapterConfig c : paths) {
			if (!c.getBoolean("active", false)) {
				continue;
			}
			HandlerHolder handlerHolder = HandlerUtil.initSingle(context, configRoot, c);
			//
			handlerHolderList.add(handlerHolder);
			//
			// logger.info("Handler is mapped " + c.getString("uri") + " to " + handler);

		}
		//
		return handlerHolderList;
	}

	public static HandlerHolder initSingle(ServiceContext context, AdapterConfig configRoot, AdapterConfig c) {
		//
		String type = c.getString("type");
		Handler handler = null;
		if ("directory".equalsIgnoreCase(type)) {
			handler = new DirectoryHandler(c);
			// handler = new TestDirectoryHandler();
		} else if ("redirect".equalsIgnoreCase(type)) {
			handler = new RedirectHandler(c);
		} else if ("file".equalsIgnoreCase(type)) {
			handler = new FileHandler(c);
		} else if ("war".equalsIgnoreCase(type)) {
			// WarHandler warHandler = new WarHandler(context, configRoot, c);
			// warHandler.start();
			// handler =warHandler;
			handler = new WarHandler(context, configRoot, c);
		} else {
			throw new RuntimeException("Invalid path type:" + type);
		}
		//
		return new HandlerHolder(c, handler);
	}

	public static void matchAndExecute(List<HandlerHolder> handlerHolderList, String uriMatchMode, String target,
			Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// HandlerHolder handlerHolder = tryMatch(target, request.getMethod());

		HandlerHolder handlerHolder = tryMatch(handlerHolderList, uriMatchMode, target, request.getMethod());

		if (handlerHolder == null) {
			logger.error("Unable to match input target [" + target + "] with method[" + request.getMethod() + "],"
					+ "handlerConfigList=" + handlerHolderList);
			handledNotFound(target, baseRequest, request, response);
			return;
		}
		//
		executeHandler(handlerHolder, target, baseRequest, request, response);
	}

	// 找到匹配的handler holder
	private static HandlerHolder tryMatch(List<HandlerHolder> handlerHolderList, String uriMatchMode, String target,
			String method) {
		if (handlerHolderList == null) {
			return null;
		}
		//
		if ("STARTWITH".equals(uriMatchMode)) {
			return tryMatch_mode1and2(handlerHolderList, target, method, true);
		} else if ("EQUAL".equals(uriMatchMode)) {
			// return tryMatch_equal(target,method);
			return tryMatch_mode1and2(handlerHolderList, target, method, false);
			// } else if ("SPRING".equals(uriMatchMode)) {
			// // return tryMatch_spring(target,method);
			// return tryMatch_spring(handlerHolderList, target, method);
		} else {
			throw new RuntimeException("Unsuported URI match mode:" + uriMatchMode);
		}
	}

	// 执行handler
	public static void executeHandler(HandlerHolder handlerHolder, String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		//
		Handler handler = handlerHolder.getHandler();
		// System.out.print("~~~~##############"+target+"===>"+handler);
		if (logger.isDebugEnabled()) {
			logger.debug("Forward " + target + " to " + handler);
		}

		if (handler != null) {

			handler.handle(target, baseRequest, request, response);

		}
	}

	// 针对字符串开头以及完全匹配的判断
	private static HandlerHolder tryMatch_mode1and2(List<HandlerHolder> handlerHolderList, String target, String method,
			boolean startWithMode) {

		// for (AdapterConfig path : paths) {
		// if (isFullyMatched(target, method, path)) {
		// //System.out.println("###FULL:"+target+"==>"+path);
		// return path;
		// }
		// }
		for (HandlerHolder holder : handlerHolderList) {
			if (isMatched(target, method, holder.getAdapterConfig(), startWithMode)) {
				// System.out.println("###PART:"+target+"==>"+path);
				return holder;
			}
		}
		// Not matched
		return null;
	}

	// startWithMode true,按照uri开头匹配的方式
	// false 严格相等
	private static boolean isMatched(String target, String method, AdapterConfig config, boolean startWithMode) {
		String methodConfig = config.getString("method");
		// 检查method是否匹配,配置的为空或为*,或与当前请求匹配
		if (StringUtil.isEmpty(methodConfig) || "*".equals(methodConfig) || method.equalsIgnoreCase(methodConfig)) {
			String uri = config.getString("uri");
			// if (url.endsWith("/")) {
			//
			// }else {
			// if (target.equalsIgnoreCase(url)||target.startsWith(url+"/")) {
			// return true;
			// }
			// }
			if (startWithMode) {
				if (target.startsWith(uri)) {
					return true;
				}
			} else {
				if (target.equals(uri)) {
					return true;
				}
			}
		}

		//
		return false;
	}

	//
	private static void handledNotFound(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not matched");
		baseRequest.setHandled(true);
	}
}
