package com.mttk.orche.deploy.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.mttk.orche.impl.util.ParaUtil;
//包装了一个parameter
 class ParaWrap implements AbstractWrap{
	private Method method;
	private Parameter para;
	private int position;

	public ParaWrap() {		
	}	
	public ParaWrap(Method method, Parameter para, int position) {
		super();
		this.method = method;
		this.para = para;
		this.position = position;
	}
	//得到参数的key
	public String getKey() {
		return ParaUtil.paraName(method,para,position);
	}
	//得到参数的类型
	public Class getType() {
		return para.getType();
	}
	public AnnotatedElement getAnnotatedElement() {
		return para;
	}
	
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Parameter getPara() {
		return para;
	}
	public void setPara(Parameter para) {
		this.para = para;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	@Override
	public String toString() {
		return "ParaWrap [method=" + method.getName() + ", para=" + getKey() + ", position=" + position + "]";
	}	
}
