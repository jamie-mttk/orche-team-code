package com.mttk.orche.util.dyna;

import java.util.Arrays;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;



public class DynamicLoadJavaUtil {
	public static Class<?> load(String className, String code, ClassLoader classLoader) throws Exception{
		//
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new RuntimeException(
					"No Java compiler is found, you need to start Cloud Hub with JDK instead of JRE");
		}
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
			try (MyForwardingJavaFileManager jfm = new MyForwardingJavaFileManager(fileManager)) {
				//
				MySourceJavaObject sourceObject = new MySourceJavaObject(className, code);
				Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(sourceObject);
				//
				DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
				CompilationTask task = compiler.getTask(null, jfm, diagnostics, OptionsUtil.buildOptions(classLoader),
						null, fileObjects);
				boolean result = task.call();
				if (!result) {
					//
					StringBuilder sb = new StringBuilder(512);
					for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
						sb.append("[" + d.getLineNumber() + "," + d.getColumnNumber() + "]");
						sb.append(d.getMessage(null));
						sb.append('\n');
					}
					throw new Exception("Compile failed:" + sb);
				}
				// 调用
				MyClassLoader cl = new MyClassLoader(classLoader, className, jfm.getCode());
				return cl.loadClass(className);
//			test1(clazz);
			}
		}
	}
}
