package com.mttk.orche.admin.util;

import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.mttk.orche.util.IOUtil;

public class DebugUtil {
	public static void debugRequest(HttpServletRequest request) {

		System.out.println("===Start request debug===" +request.getMethod()+"==>"+ request.getRequestURL());
		
		Enumeration<String> e = request.getParameterNames();

		while (e.hasMoreElements()) {
			String name = e.nextElement();
			System.out.println("request.parameter['" + name + "'] = " + request.getParameter(name));
		}
		Enumeration<String> attributeNames = request.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			Object attribute = request.getAttribute(attributeName);
			if (((String) attributeName).startsWith("javax.servlet")) {
				System.out.println("request.attribute['" + attributeName + "'] = " + attribute);
			}
		}
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			Enumeration<String> vals = request.getHeaders(headerName);
			while (vals.hasMoreElements()) {
				System.out.println("headers['" + headerName + "'] = " + vals.nextElement());
			}
		}
		// content
		try {
			try (InputStream is = request.getInputStream()) {
				byte[] data = IOUtil.toArray(is);
				System.out.println("content byte size:" + data.length);
				System.out.println("content:\n" + new String(data, "utf-8"));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
