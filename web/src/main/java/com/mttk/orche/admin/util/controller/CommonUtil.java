package com.mttk.orche.admin.util.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import com.mttk.orche.util.StringUtil;

//一些小工具
public class CommonUtil {

	/**
	 * 把double类型的小数点位数截为给定的位�?
	 * @param v
	 * @param scale
	 * @return
	 */
	public static Double scaleDouble(Double v,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
	/**
	 * 返回给定月份的第一�?0�?�?�?毫秒)
	 * @param year
	 * @param month		月份�?开始到12
	 * @return
	 */
	public static Date firstDayOfThisMonth(int year,int month) {		
		Calendar c=monthStart(year,month);
		return c.getTime();
	}
	/**
	 * 检查给定月份间隔多�?delta给出)的第一�?0�?�?�?毫秒)
	 * @param year
	 * @param month
	 * @param delta	正数是后面月�?负数是前面月�?
	 * @return
	 */
	public static Date firstDayOfMonth(int year,int month,int delta) {
		Calendar c=monthStart(year,month);
		c.add(Calendar.MONTH, delta);
		return c.getTime();
	}
	//返回给定月份的第一�?0�?�?�?毫秒)
	private static Calendar monthStart(int year,int month) {		
		
		Calendar c=Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH,month-1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}
	
	//id是否匹配；考虑到都为空的情况
	public static boolean isIdMatch(String id,String id2) {
		if (StringUtil.isEmpty(id)) {
			return StringUtil.isEmpty(id2);
		}else {
			return id.equalsIgnoreCase(id2);
		}
	}
}
