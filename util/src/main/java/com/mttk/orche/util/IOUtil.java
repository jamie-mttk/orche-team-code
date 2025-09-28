package com.mttk.orche.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Input/Output related function
 *
 */
public class IOUtil {
	// public static final String VALID_FILENAME_CHARS =
	// "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.@-";
	/**
	 * Copy from InputStream to OutputStream
	 * 
	 * @param is
	 * @param os
	 * @return
	 * @throws IOException
	 */
	public static int copy(InputStream is, OutputStream os) throws IOException {
		int totalCount = 0;
		byte[] buf = new byte[4096];
		int count = 0;

		while (count >= 0) {
			count = is.read(buf);
			if (count > 0) {
				totalCount += count;
				os.write(buf, 0, count);
			}
		}

		return totalCount;
	}

	/**
	 * Copy with size limited
	 * 
	 * @param is
	 * @param os
	 * @param maxSize
	 * @return
	 * @throws IOException
	 */
	public static int copy(InputStream is, OutputStream os, int maxSize)
			throws IOException {
		int totalCount = 0;
		byte[] buf = new byte[4096];
		int count = 0;

		/*
		 * Timer timer = new Timer(180); // 3 minute timeout
		 * 
		 * while (bytesIn >= 0 && totalCount < contentSize) {
		 * if (in.available() > 0) {
		 * bytesIn = in.read(buf);
		 * totalCount += bytesIn;
		 * 
		 * if (bytesIn > 0) {
		 * out.write(buf, 0, bytesIn);
		 * }
		 * timer.reset();
		 * }
		 * if (timer.isTimedOut()) {
		 * throw new IOException("Copy time-out after " + Integer.toString(totalCount) +
		 * " bytes");
		 * }
		 * }
		 */
		while ((count >= 0) && (totalCount < maxSize)) {
			count = is.read(buf);
			if (count > 0) {
				totalCount += count;
				os.write(buf, 0, count);
			}
		}

		return totalCount;
	}

	/**
	 * Read InputStream to an byte array
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] toArray(InputStream is) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int count = is.read(buf);

		while (count >= 0) {
			bOut.write(buf, 0, count);
			count = is.read(buf);
		}

		return bOut.toByteArray();
	}

	public static String convertToString(InputStream is, String charset) throws IOException {
		return new String(toArray(is), charset);
	}

	public static String convertToString(InputStream is) throws IOException {
		return convertToString(is, "UTF-8");
	}

	/**
	 * Safe close with no exception
	 * 
	 * @param is
	 */
	public static void safeClose(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Safe close with no exception
	 * 
	 * @param os
	 */
	public static void safeClose(OutputStream os) {
		try {
			if (os != null) {
				os.flush();
			}
		} catch (Throwable t) {
			// t.printStackTrace();
		}
		try {
			if (os != null) {
				os.close();
			}
		} catch (Throwable t) {
			// t.printStackTrace();
		}
	}

	/**
	 * Safe close with no exception
	 * 
	 * @param is
	 * @param os
	 */
	public static void safeClose(InputStream is, OutputStream os) {
		safeClose(is);
		safeClose(os);
	}

	/**
	 * Safe close with no exception
	 * 
	 * @param writer
	 */
	public static void safeClose(Writer writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Safe close with no exception
	 * 
	 * @param reader
	 */
	public static void safeClose(Reader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Safe close with no exception
	 * 
	 * @param reader
	 * @param writer
	 */
	public static void safeClose(Reader reader, Writer writer) {
		safeClose(reader);
		safeClose(writer);
	}
}
