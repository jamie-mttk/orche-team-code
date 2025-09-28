package com.mttk.orche.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *         Generate ID
 */
public class IdGenerator {
	private static IdGenerator instance = null;

	

	private int loopValue = 0;

	//
	public static IdGenerator getInstance() {
		if (instance == null) {
			synchronized (IdGenerator.class) {
				if (instance == null) {
					instance = new IdGenerator();
				}
			}
		}
		return instance;
	}

	public synchronized String next() {
		loopValue++;
		if (loopValue >= 1000) {
			loopValue = 0;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");
		return format.format(new Date())
				+ StringUtil.leftFillString(Integer.toString(loopValue), 3,
						'0');
	}
}
