package com.mttk.orche.deploy.support;

import java.lang.reflect.AnnotatedElement;

public interface AbstractWrap {
	//给出参数的key
	public String getKey();
	//如果true,此类返回的可以会覆盖掉annotation里的key
	//public boolean keyPriority();
	//给出参数的类型
	public Class getType();
	
	public AnnotatedElement getAnnotatedElement();
	
}
