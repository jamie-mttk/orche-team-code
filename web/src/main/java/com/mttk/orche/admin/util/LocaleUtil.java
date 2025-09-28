package com.mttk.orche.admin.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.core.PersistSupport;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.service.I18nService;
import com.mttk.orche.util.StringUtil;

//提供对locale的支�?
public class LocaleUtil {
	private static Logger logger = LoggerFactory.getLogger(LocaleUtil.class);

	//
	public static Document handle(String key, HttpServletRequest request,
			PersistSupport persistSupport) throws Exception {
		Document doc = persistSupport.load("key", key).orElse(null);
		return handleInternal(key, request, doc);
	}

	//
	public static Document handle(Document doc, HttpServletRequest request) throws Exception {
		String key = doc.getString("key");
		return handleInternal(key, request, doc);
	}

	// 得到locale,null代表缺省locale
	public static Locale getLocale(HttpServletRequest request) {
		//
		String lang = getLang(request);
		if (StringUtil.isEmpty(lang)) {
			return null;
		}
		try {
			return new Locale(lang);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getLang(HttpServletRequest request) {
		return request.getHeader("x-Lang");
	}

	private static Document handleInternal(String key, HttpServletRequest request,
			Document doc) throws Exception {
		if (doc == null) {
			return null;
		}
		//
		String lang = getLang(request);
		//
		if (StringUtil.notEmpty(lang)) {
			//
			I18nService s = ServerLocator.getServer().getService(I18nService.class);
			if (s != null) {
				try {
					Document d = s.convert(doc, lang);

					if (d != null) {
						return d;
					}
				} catch (Exception e) {
					// 如果转换失败,不报错而是记录错误
					logger.error("Convert i18n failed,key:" + key + ",lang:" + lang, e);
				}
			}
		}
		//
		return doc;
	}
}
