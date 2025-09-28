package com.mttk.orche.util.dyna;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;

public class MyForwardingJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	private byte[] code;

	//
	public MyForwardingJavaFileManager(StandardJavaFileManager manager) {
		super(manager);

	}

	// private void displayClassLoader(ClassLoader cl) {
	// System.out.println(cl);
	// if (cl.getParent()!=null) {
	// displayClassLoader(cl.getParent());
	// }
	// }

	// @Override
	// public ClassLoader getClassLoader(JavaFileManager.Location location) {
	// //
	// System.out.println("##:"+location.getName()+"~~"+super.getClassLoader(location));
	// // displayClassLoader(super.getClassLoader(location));
	// // return super.getClassLoader(location);
	// try {
	// //test
	// try {
	// Class
			// clazz=context.obtainClassLoader().loadClass("com.mttk.orche.util.StringUtil");
	// System.out.println("~~~~~~~~~~~"+clazz);
	// }catch(Exception e) {
	// e.printStackTrace();
	// }
	// //必须使用此class load(或更上一层),否则自动使用了系统的classloader,得不到几乎所有的对象
	// return context.obtainClassLoader();
	// }catch(Exception e) {
	// e.printStackTrace();
	// return super.getClassLoader(location);
	// }
	// }
	//
	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
			JavaFileObject.Kind kind, FileObject sibling) throws IOException {
		if (kind == JavaFileObject.Kind.CLASS) {
			return new SimpleJavaFileObject(URI.create(className + ".class"), JavaFileObject.Kind.CLASS) {
				public OutputStream openOutputStream() {
					return new FilterOutputStream(new ByteArrayOutputStream()) {
						public void close() throws IOException {
							ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
							// bytes.put(className, bos.toByteArray());
							code = bos.toByteArray();
							//
							out.close();
						}
					};
				}
			};
		} else {
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

	public byte[] getCode() {
		return code;
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.code = null;
	}
}
