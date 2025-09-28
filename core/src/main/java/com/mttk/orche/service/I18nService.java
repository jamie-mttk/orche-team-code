package com.mttk.orche.service;

import org.bson.Document;

import com.mttk.orche.core.Service;

/**
 * 处理i18n相关
 *
 */
public interface I18nService extends Service {
	/**
	 * 是否支持i18n
	 * 
	 * @return
	 */
	boolean isSupport();

	/**
	 * 得到缺省的语言,如果用户没有设置返回null
	 * 
	 * @return
	 */
	String defaultLang();

	/**
	 * 根据locale转换给定的doc
	 * 
	 * @param doc 转换前的文档
	 * @return 返回转换后的文档,如果无法转换返回null
	 * @throws Exception
	 */
	Document convert(Document doc, String locale) throws Exception;
	// /**
	// * 重置,清除缓存 - 部署后如果更新了jar报总是要启动的,所以没必要
	// */
	// public void reset() ;
}
