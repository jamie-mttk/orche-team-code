package com.mttk.orche.agent.javaCode.util;

import com.mttk.orche.agent.javaCode.support.JavaCodeBase;
import com.mttk.orche.agent.javaCode.support.JavaCodeContext;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComplieCodeUti {

    /**
     * 编译Java代码到类
     * 
     * @param javaCodeContext Java代码上下文
     * @return 编译后的类
     * @throws Exception 编译异常
     */
    public static void compileCode(JavaCodeContext javaCodeContext) throws Exception {
        String code = javaCodeContext.getCode();
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("代码内容不能为空");
        }

        // 获取类名
        String className = extractClassName(code);
        if (className == null) {
            throw new IllegalArgumentException("无法从代码中提取类名");
        }

        // 获取包名
        String packageName = extractPackageName(code);
        String fullClassName = packageName != null ? packageName + "." + className : className;

        // 编译代码
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new RuntimeException("无法获取Java编译器，请确保运行在JDK环境中");
        }

        // 创建内存文件管理器
        MemoryFileManager fileManager = new MemoryFileManager(
                compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8));

        // 创建Java源文件对象
        JavaFileObject javaFileObject = new StringJavaFileObject(fullClassName, code);

        // 编译选项 - 使用当前线程classloader的所有类路径
        String classpath = buildClasspath();
        List<String> options = Arrays.asList("-cp", classpath);
        // System.out.println("compile options:\n" + options);

        // 创建诊断监听器来收集编译错误信息
        CompilationErrorCollector errorCollector = new CompilationErrorCollector();

        // 执行编译
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, // Writer out
                fileManager, // JavaFileManager fileManager
                errorCollector, // DiagnosticListener<? super JavaFileObject> diagnosticListener
                options, // Iterable<String> options
                null, // Iterable<String> classes
                Arrays.asList(javaFileObject) // Iterable<? extends JavaFileObject> compilationUnits
        );

        boolean success = task.call();
        if (!success) {
            String errorMessage = errorCollector.getErrorMessage();
            throw new RuntimeException("代码编译失败:\n" + errorMessage);
        }

        // 加载编译后的类
        ClassLoader classLoader = new MemoryClassLoader(fileManager.getOutputFiles(),
                ComplieCodeUti.class.getClassLoader());
        Class<?> clazz = classLoader.loadClass(fullClassName);

        // 验证类是否实现了JavaCodeBase接口
        if (!JavaCodeBase.class.isAssignableFrom(clazz)) {
            throw new ClassCastException("生成的类必须实现JavaCodeBase接口");
        }

        // 保存编译后的类到上下文
        @SuppressWarnings("unchecked")
        Class<? extends JavaCodeBase> javaCodeClass = (Class<? extends JavaCodeBase>) clazz;
        javaCodeContext.setCompiledClass(javaCodeClass);

    }

    /**
     * 从代码中提取类名
     * 
     * @param code Java代码
     * @return 类名
     */
    private static String extractClassName(String code) {
        Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 从代码中提取包名
     * 
     * @param code Java代码
     * @return 包名
     */
    private static String extractPackageName(String code) {
        Pattern pattern = Pattern.compile("package\\s+([\\w.]+);");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 内存Java文件对象
     */
    private static class StringJavaFileObject extends SimpleJavaFileObject {
        private final String code;

        protected StringJavaFileObject(String className, String code) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return code;
        }
    }

    /**
     * 内存文件管理器
     */
    private static class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final Map<String, JavaFileObject> outputFiles = new HashMap<>();

        protected MemoryFileManager(StandardJavaFileManager standardManager) {
            super(standardManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
                FileObject sibling) throws IOException {
            JavaFileObject fileObject = new MemoryJavaFileObject(className, kind);
            outputFiles.put(className, fileObject);
            return fileObject;
        }

        public Map<String, JavaFileObject> getOutputFiles() {
            return outputFiles;
        }
    }

    /**
     * 内存Java文件对象（用于输出）
     */
    private static class MemoryJavaFileObject extends SimpleJavaFileObject {
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        protected MemoryJavaFileObject(String className, Kind kind) {
            super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return outputStream;
        }

        public byte[] getBytes() {
            return outputStream.toByteArray();
        }
    }

    /**
     * 内存类加载器
     */
    private static class MemoryClassLoader extends ClassLoader {
        private final Map<String, JavaFileObject> classFiles;

        public MemoryClassLoader(Map<String, JavaFileObject> classFiles, ClassLoader parent) {
            super(parent);
            this.classFiles = classFiles;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            // 首先尝试从内存中的编译结果加载类
            JavaFileObject fileObject = classFiles.get(name);
            if (fileObject != null) {
                try {
                    byte[] bytes = ((MemoryJavaFileObject) fileObject).getBytes();
                    return defineClass(name, bytes, 0, bytes.length);
                } catch (Exception e) {
                    throw new ClassNotFoundException("无法加载类: " + name, e);
                }
            }

            // 如果内存中没有找到，尝试从父classloader加载
            try {
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                throw new ClassNotFoundException("类未找到: " + name, e);
            }
        }
    }

    /**
     * 构建包含当前线程classloader中所有类的classpath
     * 
     * @return 完整的classpath字符串
     */
    private static String buildClasspath() {
        StringBuilder classpath = new StringBuilder();
        Set<String> allPaths = new LinkedHashSet<>();

        // 添加系统类路径
        String systemClassPath = System.getProperty("java.class.path");
        if (systemClassPath != null && !systemClassPath.isEmpty()) {
            String[] paths = systemClassPath.split(File.pathSeparator);
            for (String path : paths) {
                if (path != null && !path.trim().isEmpty()) {
                    allPaths.add(path);
                }
            }
        }

        // 获取当前线程的classloader
        ClassLoader currentClassLoader = ComplieCodeUti.class.getClassLoader();
        // System.out.println("@@@@@@@@@@@@@@当前线程的classloader: " +
        // Thread.currentThread().getContextClassLoader());
        // System.out.println("@@@@@@@@@@@@@@当前类的classloader: " +
        // ComplieCodeUti.class.getClassLoader());
        if (currentClassLoader != null) {
            // 遍历classloader层次结构，收集所有URL
            collectClassLoaderUrls(currentClassLoader, allPaths);
        }

        // 尝试通过反射获取更多classloader信息
        try {
            collectClassLoaderUrlsViaReflection(currentClassLoader, allPaths);
        } catch (Exception e) {
            // 忽略反射异常，继续使用其他方法
        }

        // 构建最终的classpath
        for (String path : allPaths) {
            if (classpath.length() > 0) {
                classpath.append(File.pathSeparator);
            }
            classpath.append(path);
        }

        return classpath.toString();
    }

    /**
     * 递归收集classloader及其父classloader中的所有URL
     * 
     * @param classLoader 要收集的classloader
     * @param paths       用于存储路径的集合
     */
    private static void collectClassLoaderUrls(ClassLoader classLoader, Set<String> paths) {
        // System.out.println("@@@@@@@@@@@@@@收集classloader中的URL: " + classLoader);
        if (classLoader == null) {
            return;
        }

        // 如果是URLClassLoader，直接获取URLs
        if (classLoader instanceof java.net.URLClassLoader) {
            java.net.URLClassLoader urlClassLoader = (java.net.URLClassLoader) classLoader;
            try {
                for (URL url : urlClassLoader.getURLs()) {
                    String path = convertUrlToPath(url);
                    if (path != null && !path.isEmpty()) {
                        paths.add(path);
                    }
                }
            } catch (Exception e) {
                // 忽略异常，继续处理
            }
        }

        // 递归处理父classloader
        collectClassLoaderUrls(classLoader.getParent(), paths);
    }

    /**
     * 通过反射收集classloader中的URL（适用于Java 9+）
     * 
     * @param classLoader 要收集的classloader
     * @param paths       用于存储路径的集合
     */
    private static void collectClassLoaderUrlsViaReflection(ClassLoader classLoader, Set<String> paths) {
        if (classLoader == null) {
            return;
        }

        try {
            // 尝试通过反射获取URLs
            java.lang.reflect.Method getURLsMethod = classLoader.getClass().getMethod("getURLs");
            if (getURLsMethod != null) {
                URL[] urls = (URL[]) getURLsMethod.invoke(classLoader);
                if (urls != null) {
                    for (URL url : urls) {
                        String path = convertUrlToPath(url);
                        // System.out.println(url + " ==> " + path);
                        if (path != null && !path.isEmpty()) {
                            paths.add(path);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 忽略反射异常
        }

        // 递归处理父classloader
        collectClassLoaderUrlsViaReflection(classLoader.getParent(), paths);
    }

    /**
     * 将URL转换为文件路径
     * 
     * @param url URL对象
     * @return 文件路径字符串
     */
    private static String convertUrlToPath(URL url) {
        if (url == null) {
            return null;
        }
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        String urlString = url.toString();

        // 处理file:协议的URL
        if (urlString.startsWith("file:")) {
            String path = urlString.substring(5); // 移除"file:"前缀

            // 在Windows系统下，将Unix风格的路径分隔符转换为Windows风格
            if (isWindows) {
                path = path.replace('/', File.separatorChar);
            }
            if (isWindows && path.startsWith("\\")) {
                path = path.substring(1);
            }
            return path;
        }

        // 处理jar:协议的URL
        if (urlString.startsWith("jar:")) {
            String jarPath = urlString.substring(4); // 移除"jar:"前缀
            int exclamationIndex = jarPath.indexOf('!');
            if (exclamationIndex > 0) {
                jarPath = jarPath.substring(0, exclamationIndex);
            }
            if (jarPath.startsWith("file:")) {
                try {
                    return convertUrlToPath(java.net.URI.create(jarPath).toURL());
                } catch (Exception e) {
                    return jarPath;
                }
            }
        }

        return urlString;
    }

    /**
     * 编译错误收集器
     * 用于收集和格式化编译过程中的错误信息
     */
    private static class CompilationErrorCollector implements DiagnosticListener<JavaFileObject> {
        private final StringBuilder errorMessage = new StringBuilder();

        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                if (errorMessage.length() > 0) {
                    errorMessage.append("\n");
                }

                // 获取错误位置信息
                String sourceName = "未知文件";
                if (diagnostic.getSource() != null) {
                    sourceName = diagnostic.getSource().getName();
                }

                // 格式化错误信息
                errorMessage.append(String.format("错误位置: %s:%d:%d",
                        sourceName,
                        diagnostic.getLineNumber(),
                        diagnostic.getColumnNumber()));
                errorMessage.append("\n");
                errorMessage.append("错误类型: ").append(diagnostic.getKind());
                errorMessage.append("\n");
                errorMessage.append("错误信息: ").append(diagnostic.getMessage(null));
                errorMessage.append("\n");

                // 如果有代码片段，显示相关代码
                if (diagnostic.getLineNumber() > 0) {
                    errorMessage.append("代码位置: 第").append(diagnostic.getLineNumber()).append("行");
                    if (diagnostic.getColumnNumber() > 0) {
                        errorMessage.append("，第").append(diagnostic.getColumnNumber()).append("列");
                    }
                    errorMessage.append("\n");
                }
            }
        }

        /**
         * 获取格式化的错误信息
         * 
         * @return 错误信息字符串
         */
        public String getErrorMessage() {
            if (errorMessage.length() == 0) {
                return "编译失败，但未收集到具体错误信息";
            }
            return errorMessage.toString();
        }
    }
}
