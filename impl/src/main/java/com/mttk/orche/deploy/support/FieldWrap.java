package com.mttk.orche.deploy.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

//包装了类的要给字段
public class FieldWrap implements AbstractWrap{
	private Class clazz;
	private Field field;
	//
	public FieldWrap() {
		
	}
	public FieldWrap(Class clazz,Field field) {
		this.clazz=clazz;
		this.field=field;
	}
	
	//得到参数的key
	public String getKey() {
		return field.getName();
	}
	//得到参数的类型
	public Class getType() {
		return field.getType();
	}
	public AnnotatedElement getAnnotatedElement() {
		return field;
	}
	
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	@Override
	public String toString() {
		return "FieldWrap [clazz=" + clazz + ", field=" + getKey() + "]";
	}
	
}
