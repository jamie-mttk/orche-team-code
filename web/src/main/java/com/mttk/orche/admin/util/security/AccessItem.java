package com.mttk.orche.admin.util.security;

import java.io.Serializable;
import java.util.regex.Pattern;

//表示一条从access.json解析的访问控制项目，往往对应一个接�?
public class AccessItem  implements Serializable{
	private static final long serialVersionUID = -525633961218815421L;
	public enum METHOD{GET,POST,ALL};
	//
	//访问只允许此种类的方法通过
	private METHOD method=METHOD.GET;
	//字符串匹配项,不能为空
	private String pattern;
	//编译过的pattern，提升效�?
	// private Pattern p ;
	 public AccessItem() {
		 
	 }
	 public AccessItem(METHOD method,String pattern){
		 this.method=method;
		 this.pattern=pattern;
		 //
		 //compilePattern();
	 }
	public METHOD getMethod() {
		return method;
	}
	public void setMethod(METHOD method) {
		this.method = method;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
		//
		// compilePattern();
	}
	
//	private Pattern getP() {
//		if (p==null) {
//			compilePattern();
//		}
//		return p;
//	}
//	//编译pattern
//	private void  compilePattern() {
//		if (StringUtil.notEmpty(pattern)) {
//			this.p=Pattern.compile(pattern);
//		}else {
//			this.p=null;
//		}
//	}
	//输入的方法是否匹配
	public boolean match(String method,String path) {
		//method匹配
		if(getMethod()==METHOD.ALL||
				getMethod().name().equalsIgnoreCase(method)) {
			//method匹配
		}else {
			return false;
		}
		//path匹配
		
		if (Pattern.matches(pattern,path)) {
			return true;
		}else {
			return false;
		}
	}
	//
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AccessItem)) {
			return false;
		}
		AccessItem o=(AccessItem)obj;
		if (o.getMethod()!=this.getMethod()) {
			return false;
		}
		if (o.getPattern()!=null && o.getPattern().equals(this.getPattern())) {
			return true;
		}
		//
		return false;
	}
	@Override
	public String toString() {
		return "AccessItem [method=" + method + ", pattern=" + pattern  + "]";
	}
	
	//用于Serializable
//	private void writeObject(java.io.ObjectOutputStream out)
//		     throws IOException{
//		out.writeChars(method.name());
//		out.writeChars(pattern);
//	}
//		 private void readObject(java.io.ObjectInputStream in)
//		     throws IOException, ClassNotFoundException{
//			
//		 }
//		 private void readObjectNoData()
//		     throws ObjectStreamException{
//			 this.method=METHOD.GET;
//		 }
}
