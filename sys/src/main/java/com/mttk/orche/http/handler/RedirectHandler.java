package com.mttk.orche.http.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.mttk.orche.addon.AdapterConfig;

/**
 * 处理目标为一固定文件
 */
public class RedirectHandler extends AbstractHandler {
	private AdapterConfig config=null;
	public RedirectHandler(AdapterConfig config) {
		super();
		//
		this.config=config;
	
	}
	@Override
	public void handle(
			String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String url=config.getStringMandatory("targetRedirectUrl");
		response.sendRedirect(url);
	}
	
	@Override
	public String toString() {
		return "Redirect handler:"+config.getString("targetRedirectUrl");
	}
}
