package com.mttk.orche.service;

import java.util.Map;

import com.mttk.orche.core.PersistService;
import com.mttk.orche.service.support.IHouseKeeping;

public interface AccessLogService extends PersistService, IHouseKeeping {
	/**
	 * 系统是否支持记录Logger
	 * 用户在生成Logger前先调用,如果不需要记录则可以不生成日志记录
	 * 
	 * @return
	 */
	public boolean getNeedLog();

	// public void setNeedLog(boolean needLog);
	/**
	 * 记录日志如果needLog为false什么都不做
	 * 
	 * @param log
	 * @return 如果needLog为false,返回false;否则记录后返回true
	 * @throws Exception
	 */
	public boolean handle(Map<String, Object> log) throws Exception;

}
