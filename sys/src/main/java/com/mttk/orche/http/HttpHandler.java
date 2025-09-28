package com.mttk.orche.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mttk.orche.addon.AdapterConfig;
//用于服务,实现此接口的服务可以作为Http entry的服务目标
public interface HttpHandler {
	 public void handle(HttpServletRequest request, HttpServletResponse response,
			 AdapterConfig configHttpEntry,AdapterConfig configPath) throws Exception;
}
