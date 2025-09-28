package com.mttk.orche.bootstrap;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Bootstrap {
	// Server home
	private String serverHome;

	/**
	 * Are we starting a new server?
	 */
	protected boolean starting = false;

	/**
	 * Are we stopping an existing server?
	 */
	protected boolean stopping = false;
	/**
	 * Init
	 */
	protected boolean initing = false;
	/**
	 * Upgrade
	 */
	protected boolean upgrading = false;
	// 其他参数,根据不同的处理模式不同
	private Map<String, String> config = new HashMap<>();

	public static void main(String[] args) {
		(new Bootstrap()).process(args);
	}

	/**
	 * The instance main program.
	 * 
	 */
	public void process(String args[]) {

		try {
			initServerHomePath();
			//

			//
			if (arguments(args)) {

				execute();

			}
			//
			// Thread.sleep(10*1000);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}

	private void initServerHomePath() throws Exception {
		String serverHome = null;
		String path = System.getProperty("server.home");

		if (path == null || "".equals(path)) {
			path = Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().getFile();
			/*
			 * The 1.4 JDK munges the code source file with URL encoding so run this path
			 * through the decoder so that is JBoss starts in a path with spaces we don't
			 * come crashing down.
			 */
			path = java.net.URLDecoder.decode(path, "UTF-8");
			File runJar = new File(path);
			File homeFile = runJar.getParentFile().getParentFile();
			serverHome = homeFile.getCanonicalPath();
		} else {
			try {
				serverHome = new File(path).getCanonicalPath();
			} catch (Exception e) {
				throw new Exception("Fail to init server home:" + path, e);
			}
		}
		//
		System.setProperty("server.home", serverHome);

		//
		this.setServerHomePath(serverHome);
		//
		System.out.println("Server home is set to " + serverHome);
	}

	private void setServerHomePath(String serverHome) {

		if (serverHome.charAt(serverHome.length() - 1) == File.separatorChar) {
			this.serverHome = serverHome.substring(0, serverHome.length() - 1);
		} else {
			this.serverHome = serverHome;
		}

	}

	/**
	 * Process the specified command line arguments, and return <code>true</code> if
	 * we should continue processing; otherwise return <code>false</code>.
	 * 
	 * @param args Command line arguments to process
	 */
	protected boolean arguments(String args[]) {
		// boolean isConfig = false;
		if (args.length < 1) {
			usage();
			return (false);
		}

		for (int i = 0; i < args.length; i++) {
			// if (isConfig) {
			// configFile = args[i];
			// isConfig = false;
			// } else if (args[i].equals("-config")) {
			// isConfig = true;
			// } else
			if (args[i].equals("-help")) {
				usage();
				return false;
			} else if (args[i].equals("start")) {
				starting = true;
			} else if (args[i].equals("stop")) {
				stopping = true;
			} else if (args[i].equals("init")) {
				initing = true;
			} else if (args[i].equals("upgrade")) {
				upgrading = true;
			} else if (args[i].startsWith("-")) {
				// 减号开头的是参数
				if (i >= args.length - 1) {
					// 说明这个参数是最后一个，后面没有带参数值,报错
					throw new RuntimeException("No config value is found for " + args[i]);
				}
				config.put(args[i].substring(1), args[i + 1]);
				// 跳过一个参数
				i++;
			} else {
				usage();
				return false;
			}
		}

		return (true);

	}

	/**
	 * Print usage information for this application.
	 */
	protected void usage() {

		System.out.println("usage: " + this.getClass().toString() + " { start | stop | init | upgrade }");
	}

	/**
	 * Execute the processing that has been configured from the command line.
	 */
	protected void execute() throws Exception {

		if (starting) {
			start("start");
		} else if (stopping) {
			stop();

		} else if (upgrading) {
			doUpgrade();
		} else if (initing) {
			start("init");
		}
	}

	/**
	 * Start up server
	 * 
	 */
	protected void start(String operate) {
		Thread worker = new StartupThread(this, operate);

		// worker.setContextClassLoader(bootClassLoader(serverHome));
		ThreadGroup threads = new ThreadGroup("Server Group");
		new Thread(threads, worker, "main").start();

	}

	// Actually do startup
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doStart(String operate) throws Exception {
		//
		config.put("operate", operate);
		//
		ClassLoader clOld = Thread.currentThread().getContextClassLoader();
		try {
			// StartServer startServer;

			ClassLoader cl = new BootstrapClassLoader(serverHome, clOld);
			Thread.currentThread().setContextClassLoader(cl);
			Class clazz = cl.loadClass("com.mttk.orche.startup.StartServer");
			// System.out.println(clOld+"==>"+cl+"==>"+clazz.getClassLoader());
			Object loadServerInstance = clazz.getDeclaredConstructor().newInstance();
			String methodName;

			Method method;
			Class paramTypes[];
			Object paramValues[];
			methodName = "process";
			paramTypes = new Class[2];
			paramTypes[0] = String.class;
			// paramTypes[1] = Integer.class;
			paramTypes[1] = Map.class;
			paramValues = new Object[2];
			paramValues[0] = serverHome;
			paramValues[1] = config;
			method = loadServerInstance.getClass().getMethod(methodName, paramTypes);
			method.invoke(loadServerInstance, paramValues);

		} finally {
			Thread.currentThread().setContextClassLoader(clOld);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doUpgrade() throws Exception {
		ClassLoader clOld = Thread.currentThread().getContextClassLoader();
		try {
			// StartServer startServer;

			// ClassLoader cl = new LibClassLoader(
			// new String[]{serverHome + File.separator+ "lib"},true,clOld);
			ClassLoader cl = new UpgradeClassLoader(serverHome, clOld);
			Thread.currentThread().setContextClassLoader(cl);
			Class clazz = cl.loadClass("com.mttk.orche.upgrade.Upgrader");
			// System.out.println(clOld+"==>"+cl+"==>"+clazz.getClassLoader());
			Object loadServerInstance = clazz.getDeclaredConstructor().newInstance();
			String methodName;

			Method method;
			Class paramTypes[];
			Object paramValues[];
			methodName = "process";
			paramTypes = new Class[1];
			paramTypes[0] = String.class;
			// paramTypes[1] = String.class;
			paramValues = new Object[1];
			paramValues[0] = serverHome;
			// paramValues[1] = configFile;
			method = loadServerInstance.getClass().getMethod(methodName,
					paramTypes);
			method.invoke(loadServerInstance, paramValues);

		} finally {
			Thread.currentThread().setContextClassLoader(clOld);
		}
	}

	/**
	 * Stop server
	 * 
	 */
	protected void stop() {

	}

	class StartupThread extends Thread {
		private Bootstrap bootstrap;
		private String operate;

		StartupThread(Bootstrap bootstrap, String operate) {
			this.bootstrap = bootstrap;
			this.operate = operate;
		}

		public void run() {
			try {
				bootstrap.doStart(operate);
			} catch (Exception e) {
				System.err.println("Failed to boot server");
				e.printStackTrace();
			}
		}
	}
}
