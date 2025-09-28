package com.mttk.orche.http.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.handler.ErrorHandler;

public class MyErrorHandler extends ErrorHandler {

	@Override
	protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message,
			boolean showStacks) throws IOException {
		//showStacks = true;
		super.writeErrorPage(request, writer, code, message, showStacks);

	}

	@Override
	protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message,
			boolean showStacks) throws IOException {
		super.writeErrorPageBody(request, writer, code, message, showStacks);
		//System.out.println("~~~~~~" + code + ":" + message);
	}

	protected void writeErrorPageMessage(HttpServletRequest request, Writer writer, int code, String message,
			String uri) throws IOException {
		super.writeErrorPageMessage(request, writer, code, message, uri);
		//System.out.println("@@@@@" + code + ":" + message);
		ByteArrayOutputStream os=new ByteArrayOutputStream(2048);
		OutputStreamWriter w=new OutputStreamWriter(os);
		super.writeErrorPageMessage(request, w, code, message, uri);
		w.flush();
	//	System.out.println("###"+new String(os.toByteArray()));

	}
}
