package com.mttk.orche.util;

//匹配相关的方法
public class PatternUtil {
	// 把通配符匹配转换成正则表达式匹配,支持用|分割的多个pattern匹配
	public static String wildCardTransform(String pattern) {
		if (StringUtil.isEmpty(pattern)) {
			return null;
		}
		String[] ps = pattern.split("\\|");
		StringBuffer sb = new StringBuffer(128);
		for (int i = 0; i < ps.length; i++) {
			if (sb.length() > 0) {
				sb.append('|');
			}
			sb.append(wildCardTransformSingle(ps[i]));
		}
		// return sb.toString();
		return "^" + sb.toString() + "$";
	}

	public static String wildCardTransformSingle(String pattern) {
		StringBuilder result = new StringBuilder(pattern.length() * 2);
		result.append("^");
		char metachar[] = { '$', '^', '[', ']', '(', ')', '{', '|', '+', '?', '.', '\\' };
		for (int i = 0; i < pattern.length(); i++) {
			char ch = pattern.charAt(i);
			boolean isMeta = false;
			for (int j = 0; j < metachar.length; j++) {
				if (ch == metachar[j]) {
					result.append("\\").append(ch);
					isMeta = true;
					break;
				}
			}
			if (!isMeta) {
				if (ch == '*') {
					result.append(".*");
				} else {
					result.append(ch);
				}

			}
		}
		result.append("$");
		return result.toString();
	}
}
