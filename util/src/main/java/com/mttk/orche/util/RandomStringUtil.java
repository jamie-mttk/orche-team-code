package com.mttk.orche.util;

import java.util.Random;

/**
 * 生成随机的字符串
 *
 */
public class RandomStringUtil {
    private static final Random RANDOM = new Random();
    private static char[] CHARS=new char[] {
    		'0','1','2','3','4','5','6','7','8','9',
    		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
    		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	/**
	 * Generate random string,only letters and numbers and allowed
	 * @param length
	 * @return
	 */
	public static String random(int length) {
		return randomInternal(length,CHARS,0,CHARS.length);
	}
	public static String random(int length,boolean noUpperCase) {
		return randomInternal(length,CHARS,0,CHARS.length-26);
	}
	private static String randomInternal(int length,char[] chars,int start,int end) {
		 final StringBuilder sb = new StringBuilder(length);
		 for(int i=0;i<length;i++) {
			 sb.append(chars[start+RANDOM.nextInt(end-start)]);
		 }
		return sb.toString();
	}
}
