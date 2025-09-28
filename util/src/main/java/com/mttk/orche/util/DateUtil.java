package com.mttk.orche.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Locale;

public class DateUtil {
    // SimpleDateFormat不是线程安全的
    // private static final Map<String,SimpleDateFormat> formatters = new
    // HashMap<String,SimpleDateFormat> ();

    public static String formatDate(String format, Date value) {
        SimpleDateFormat df = getDateFormat(format);
        return df.format(value);
    }

    public static String formatDate(String format) {
        return formatDate(format, new Date());
    }

    public static synchronized Date parseDate(String format, String value) throws ParseException {
        SimpleDateFormat df = getDateFormat(format);
        return df.parse(value);
    }

    private static SimpleDateFormat getDateFormat(String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH);
    }
}
