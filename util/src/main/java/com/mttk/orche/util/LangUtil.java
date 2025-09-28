package com.mttk.orche.util;

public class LangUtil {
	public static interface Executeble {
		public void execute() throws Throwable;
	}

	/**
	 * 
	 * Java 8 Lambda
	 * LangUtil.suppressThrowable(()->{
	 * //Your code here
	 * });
	 * 
	 * @param runnable
	 */
	public static void suppressThrowable(Executeble executeble) {
		try {
			executeble.execute();
		} catch (Throwable t) {
			// Do nothing
			// t.printStackTrace();
		}
	}

	// public static void safeClose(Closeable closeable) {
	// try {
	// if (closeable != null) {
	// closeable.close();
	// }
	// } catch (Throwable t) {
	// //t.printStackTrace();
	// }
	// }
	public static void safeClose(AutoCloseable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (Throwable t) {
			// t.printStackTrace();
		}
	}
}
