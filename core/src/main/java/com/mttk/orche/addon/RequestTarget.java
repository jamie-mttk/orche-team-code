package com.mttk.orche.addon;

import org.bson.Document;

/**
 * 提供通用的方法用于一些动态调用,如 SAP RFC请求获取参数
 * 实现此接口后UtilController的forward可以把请求forward到此接口的handleRequest方法
 * 
 *
 */
public interface RequestTarget {
	/**
	 * 处理实际的HTTP请求
	 * 
	 * @param in 用户传入的JSON,包括config和data
	 * @return 返回给用户的JSON,可以是单个文档，也可以通过_result给出多个选择
	 * @throws Exception
	 */
	Document handleRequest(Document in) throws Exception;
}
