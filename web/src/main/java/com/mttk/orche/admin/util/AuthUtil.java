package com.mttk.orche.admin.util;

import javax.servlet.http.HttpServletRequest;

import com.mttk.orche.util.StringUtil;

public class AuthUtil {

	// 得到当前登录用户id;没有登录返回null
	public static String getLoginUserId(HttpServletRequest request) throws Exception {
		//
		String authentication = request.getHeader("Auth-Id");
		if (StringUtil.notEmpty(authentication)) {
			return authentication;
		} else {
			throw new RuntimeException("Not loggin");
		}
	}
}
