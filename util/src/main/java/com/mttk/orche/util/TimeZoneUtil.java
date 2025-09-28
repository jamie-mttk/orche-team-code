package com.mttk.orche.util;

import java.util.TimeZone;
/**
 * 时区相关
 *
 */
public class TimeZoneUtil {
	//返回当前时区对于标准时区的时差，如中国时区返回8
	public static int defaultOffset() {
		return TimeZone.getDefault().getRawOffset()/(60*60*1000);
		//return TimeZone.getTimeZone("America/Los_Angeles").getRawOffset()/(60*60*1000);
	}
	//返回当前时区的标准值,如+08代表中国
	public static String defaultStdTimeZone() {
		String result=null;
		int offset=defaultOffset();
		if (offset>0) {
			result="+";
		}else {
			result="-";
			offset=-1*offset;
		}
		if (offset<10) {
			result+="0";
		}
		result+=offset;
		return result;
	}
}
