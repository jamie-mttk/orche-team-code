package com.mttk.orche.http.util;

import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.addon.ServiceContext;

import com.mttk.orche.http.handler.JettyHandler;
import com.mttk.orche.util.StringUtil;

public class JettyUtil {
	private static Logger logger = LoggerFactory.getLogger(JettyUtil.class);

	public static Server createHttpEntity(ServiceContext context, AdapterConfig config) throws Exception {

		System.setProperty("org.eclipse.jetty.util.log.class",
				org.eclipse.jetty.util.log.Slf4jLog.class.getCanonicalName());
		// 注意没有使用线程池时使用缺省设置
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setName("jetty_" + config.getString("name"));
		if (config.getBoolean("threadPoolCustomize")) {
			threadPool.setMaxThreads(config.getInteger("threadPoolMaxThreads", 20));
			threadPool.setMinThreads(config.getInteger("threadPoolMinThreads", 4));
			threadPool.setIdleTimeout(config.getInteger("threadPoolIdleTimeout", 60000));

			//
			logger.info("Jetty server is created with customized thread pool:" + threadPool);
		} else {
			threadPool.setMaxThreads(20);
			threadPool.setMinThreads(4);
			threadPool.setIdleTimeout(60000);
			//
			logger.info("Jetty server is created with default setting:" + threadPool);
		}
		//
		// Server
		final Server server = new Server(threadPool);
		// System.out.println(config);
		// Connectors
		server.setStopAtShutdown(true);

		if (config.containsKey("connectors")) {
			List<AdapterConfig> connectors = config.getBeanList("connectors");
			connectors.forEach((c) -> {
				handleConnector(server, c, config);
			});
		}
		// paths
		server.setHandler(new JettyHandler(context, config));
		// server.setErrorHandler(new MyErrorHandler());
		// server.setHandler(new DirectoryHandler(document));
		// 修改Jetty server能够支持的FORM参数最大值
		server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 1024 * 1024 * 1024);
		//
		List<AdapterConfig> attributes = config.getBeanList("attributes");
		if (attributes != null && attributes.size() > 0) {
			for (AdapterConfig attr : attributes) {
				server.setAttribute(attr.getString("name"), attr.get("value"));

				// System.out.println("######"+attr.getString("name")+"==>"+ attr.get("value"));
			}

		}
		//
		// server.start();
		//
		logger.info("Jetty server is started");
		//
		return server;
	}

	private static Connector handleConnector(Server server, AdapterConfig c, AdapterConfig config) {
		String protocol = c.getString("protocol");
		Connector connector = null;
		if ("HTTP".equalsIgnoreCase(protocol) || "HTTPS".equalsIgnoreCase(protocol)) {
			try {
				if (isActive(c)) {
					connector = handleConnectorHttp(server, protocol, c, config);
				} else {
					logger.info("Ignore deactive connector:" + c);
				}
			} catch (Exception e) {
				logger.error("Fail to start connector:" + c, e);
			}
		} else if ("HTTP2".equalsIgnoreCase(protocol)) {
			//
			throw new RuntimeException("HTTPS is not implemented yet");
		} else {
			throw new RuntimeException("Unsupported protocol:" + protocol);
		}
		//
		if (connector != null) {
			server.addConnector(connector);
		}
		//
		return connector;
	}

	private static Connector handleConnectorHttp(Server server, String protocol, AdapterConfig c, AdapterConfig config)
			throws Exception {
		//
		// connector
		SslContextFactory.Server sslContextFactory = null;
		if ("HTTPS".equalsIgnoreCase(protocol)) {

			// // sslContextFactory= new SslContextFactory();
			// // sslContextFactory=new SslContextFactory.Client();
			// sslContextFactory = new SslContextFactory.Server();

			// // 生成一个随机的密码
			// String password = StringUtil.getUUID();
			// CertService certService =
			// ServerLocator.getServer().getService(CertService.class);
			// KeyStore keyStore = KeyStoreSupport.create("JKS");
			// keyStore.load(null, password.toCharArray());
			// //
			// AdapterConfig configCert = c.getBean("cert");
			// if (configCert == null) {
			// throw new RuntimeException("No cert is configured for HTTPs");
			// }
			// List<String> aliasList = new ArrayList<>();
			// aliasList.add(configCert.getString("alias"));
			// // System.out.println("**********"+aliasList);
			// certService.exportKeyStore(configCert.getString("keyStore"), aliasList,
			// keyStore, password);
			// //
			// System.out.println("**********"+keyStore.getCertificate(aliasList.get(0)));
			// //
			// System.out.println("**********"+keyStore.getCertificate(aliasList.get(0).toLowerCase()));
			// // Added by Jamie,help Key manager to choose?
			// // Removed by Jamie @2023/04/03
			// // This will cause HTTPS failed if alias is upper case
			// // sslContextFactory.setCertAlias(configCert.getString("alias"));

			// //
			// sslContextFactory.setKeyStore(keyStore);
			// sslContextFactory.setKeyStorePassword(password);

			// //
			// // System.out.println("is client auth?"+c.getBoolean("clientAuth", false));

			// if ("YES".equals(c.getString("clientAuth", "NO"))) {
			// setClientAuth(sslContextFactory, c);
			// }
			// //
			// if (c.getInteger("sessionCacheSize") != null) {
			// sslContextFactory.setSslSessionCacheSize(c.getInteger("sessionCacheSize"));
			// }
			// if (StringUtil.notEmpty(c.getString("includeCipherSuites"))) {
			// sslContextFactory.setIncludeCipherSuites(c.getString("includeCipherSuites").split(","));
			// }

			// if (c.getBoolean("excludeUnsafeCipherSuites", true)) {
			// // 缺省的就不调用setExcludeCipherSuites
			// // 具体值查看 SslContextFactory的DEFAULT_EXCLUDED_CIPHER_SUITES
			// } else {
			// if (StringUtil.notEmpty(c.getString("excludeCipherSuites"))) {
			// sslContextFactory.setExcludeCipherSuites(c.getString("excludeCipherSuites").split(","));
			// } else {
			// sslContextFactory.setExcludeCipherSuites();
			// }
			// }
			// if (StringUtil.notEmpty(c.getString("includeProtocols"))) {
			// sslContextFactory.setIncludeProtocols(c.getString("includeProtocols").split(","));
			// }
			// if (c.getBoolean("excludeUnsafeProtocols", true)) {
			// //
			// } else {
			// if (StringUtil.notEmpty(c.getString("excludeProtocols"))) {
			// sslContextFactory.setExcludeProtocols(c.getString("excludeProtocols").split(","));
			// } else {
			// sslContextFactory.setExcludeProtocols();
			// }
			// }
		}
		//
		ServerConnector connector = null;
		if (sslContextFactory == null) {
			connector = new ServerConnector(server);
		} else {
			connector = new ServerConnector(server, sslContextFactory);
		}

		//
		connector.setName(c.getString("name"));
		//
		if (c.containsKey("host")) {
			String host = c.getString("host");
			if (StringUtil.isEmpty(host) || "*".equals(host)) {
				// bind all IP
			} else {
				connector.setHost(host);
			}
		}
		//
		connector.setPort(c.getInteger("port", 80));
		//
		connector.setIdleTimeout(c.getInteger("idleTimeout", 30) * 1000l);
		//
		logger.info("HTTP entry " + config.getString("name") + " is listenning at port " + connector.getPort()
				+ " with idle timeout(ms) :" + connector.getIdleTimeout());
		//
		logger.info("Succesfully init HTTP connector");
		//
		return connector;
	}

	// 设置客户端认证相关
	// private static void setClientAuth(SslContextFactory.Server sslContextFactory,
	// AdapterConfig c) throws Exception {
	// //
	// sslContextFactory.setNeedClientAuth(true);
	// sslContextFactory.setWantClientAuth(true);
	// // Trust store
	// String password = StringUtil.getUUID();
	// KeyStore keyStore = KeyStoreSupport.create("JKS");
	// keyStore.load(null, password.toCharArray());
	// //
	// List<AdapterConfig> certs = c.getBeanList("clientAuthCertsTable");
	// if (certs == null || certs.size() == 0) {
	// return;
	// }
	// //
	// CertService certService =
	// ServerLocator.getServer().getService(CertService.class);
	// String alias = null;
	// int i = 0;
	// for (AdapterConfig cert : certs) {
	// i++;
	// //
	// AdapterConfig certAdapterConfig = cert.getBean("cert");
	// if (certAdapterConfig == null) {
	// continue;
	// }
	// //
	// alias = certAdapterConfig.getString("alias");
	// // 加入i到alias是为了防止名字重复
	// keyStore.setCertificateEntry(alias + "_" + i,
	// certService.loadCertificate(certAdapterConfig.getString("keyStore"), alias));
	// }
	// //
	// sslContextFactory.setTrustStore(keyStore);
	// }

	// 判断给定的document的active标记
	public static boolean isActive(AdapterConfig config) {
		if (config.containsKey("active")) {
			return config.getBoolean("active", false);
		} else {
			return false;
		}
	}
}
