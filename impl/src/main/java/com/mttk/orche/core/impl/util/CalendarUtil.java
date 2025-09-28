package com.mttk.orche.core.impl.util;

import java.util.Calendar;

public class CalendarUtil {
	// 得到当前日期的Calendar,没有任何时间
	public static Calendar getCalendarNoTime() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}

}
