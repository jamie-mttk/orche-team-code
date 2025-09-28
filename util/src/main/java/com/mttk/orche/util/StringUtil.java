package com.mttk.orche.util;

import java.util.Arrays;
import java.util.UUID;
/**
 * 字符串相关
 *
 */
public class StringUtil {
	public static final String BLANK_STRING = "";

	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// Remove "-"
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23) + s.substring(24);
	}

	public static String randomString() {
		Long postfix=System.currentTimeMillis();
    	Double d=Math.random()*65536;
    	Integer prefix=d.intValue();
    	//
    	long ll=Long.parseLong(prefix.toString()+postfix.toString()) ;
    	return Long.toString(ll, 32);
    	
	}
	// private static final String BLANK_STR = " ";
	public static String leftFillString(String inStr, int length,
			char charToFill) {
		if (inStr == null) {
			return fillString(length, charToFill);
		}
		if (inStr.length() > length) {
			return inStr.substring(0, length);
		}
		if (inStr.length() < length) {
			return fillString(length - inStr.length(), charToFill) + inStr;
		}
		return inStr;
	}

	public static String rightFillString(String inStr, int length,
			char charToFill) {
		if (inStr == null) {
			return fillString(length, charToFill);
		}
		if (inStr.length() > length) {
			return inStr.substring(0, length);
		}
		if (inStr.length() < length) {
			return inStr + fillString(length - inStr.length(), charToFill);
		}
		return inStr;
	}

	public static String fillString(int length, char charToFill) {
		char[] fill = new char[length];
		Arrays.fill(fill, charToFill);
		return new String(fill);
	}

	public static boolean isEmpty(Object obj){
		return (obj==null||(obj instanceof String && ((String)obj).trim().length()==0));
	}
	public static boolean notEmpty(Object obj){
		return !isEmpty(obj);
	}
	
	public static String smartCutString(String raw,int maxLength){
		if (raw==null){
			return null;
		}
		if (raw.length()<=maxLength){
			return raw;			
		}else{
			return raw.substring(0,maxLength);
		}
	}
	public static String safeToString(Object o) {
		if(o==null) {
			return null;
		}
		return o.toString();
	}
}
