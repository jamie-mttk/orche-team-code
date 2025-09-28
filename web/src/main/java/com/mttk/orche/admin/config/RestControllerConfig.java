package com.mttk.orche.admin.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

import com.mttk.orche.util.StringUtil;
import com.mttk.orche.util.ThrowableUtil;

@Configuration
public class RestControllerConfig {
	@ControllerAdvice
	public class SpringExceptionHandler {
		private Logger logger=LoggerFactory.getLogger(SpringExceptionHandler.class);
		/**
		 * 全局处理Exception 错误的情况下返回500代码并且再body里返回json,包括 error:true exception:错误描述
		 * 
		 * @param exeption
		 * @return
		 */
		@ExceptionHandler(value = { Exception.class })
		public ResponseEntity<Object> handleOtherExceptions(final Exception exception) {
			//exception.printStackTrace();
			//
			Map<String, Object> map = new HashMap<String, Object>();
		  	String code="999";
        	String cause=exception.getClass().getSimpleName();
        	String error=exception.getMessage();
        	if (StringUtil.isEmpty(error)) {
        		error=exception.getClass().toString();
        	}
        	String detail=null;
        	if (exception instanceof HttpStatusCodeException) {
        		HttpStatusCodeException e=(HttpStatusCodeException)exception;
        		code=""+e.getStatusCode().value();
        		detail=e.getResponseBodyAsString();
        	}else {        		
        		detail=ThrowableUtil.dump2String(exception);
        	}
        	
	        //
        	map.put("code", code);
        	map.put("cause", cause);
        	map.put("error", error);
        	map.put("detail", detail);
			//记录到日志
			logger.error("Controller invoke error with code:"+code,exception);
			//
			return new ResponseEntity<Object>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
