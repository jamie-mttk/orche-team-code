package com.mttk.orche.i18n;

import org.bson.Document;

import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.core.ServerLocator;
import com.mttk.orche.core.impl.AbstractService;
import com.mttk.orche.service.I18nService;
import com.mttk.orche.util.StringUtil;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.LinkedHashMap;

@ServiceFlag(key = "i18nService", name = "多语种支持", description = "", type = SERVICE_TYPE.SYS, depends = "", i18n = "/com/mttk/api/impl/i18n")

public class I18nServiceImpl extends AbstractService implements I18nService {

	/**
	 * 线程安全的LRU缓存实现
	 */
	private static class ThreadSafeLRUCache<K, V> {
		private final Map<K, V> cache;
		private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

		public ThreadSafeLRUCache(int maxSize) {
			this.cache = new LinkedHashMap<K, V>(16, 0.75f, true) {
				@Override
				protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
					return size() > maxSize;
				}
			};
		}

		public V get(K key) {
			lock.readLock().lock();
			try {
				return cache.get(key);
			} finally {
				lock.readLock().unlock();
			}
		}

		public void put(K key, V value) {
			lock.writeLock().lock();
			try {
				cache.put(key, value);
			} finally {
				lock.writeLock().unlock();
			}
		}

		public void clear() {
			lock.writeLock().lock();
			try {
				cache.clear();
			} finally {
				lock.writeLock().unlock();
			}
		}
	}

	@Control(label = "资源文件缓存大小", mandatory = true, size = 1, defaultVal = "100")
	private int resourceCacheSize = 100;
	@Control(label = "转换过的配置缓存大小", mandatory = true, size = 1, defaultVal = "200")
	private int convertedCacheSize = 200;

	// 缓存资源以及加载后的Document
	private ThreadSafeLRUCache<String, Document> resourceCache;
	// 记录多语种处理完成后的数据
	private ThreadSafeLRUCache<String, Document> convertedCache;

	@Override
	public void doStart() throws Exception {
		super.doStart();
		// 初始化缓存
		resourceCache = new ThreadSafeLRUCache<>(resourceCacheSize);
		convertedCache = new ThreadSafeLRUCache<>(convertedCacheSize);
	}

	@Override
	public void doStop() throws Exception {
		super.doStop();
		//
		this.reset();
	}

	@Override
	public boolean isSupport() {

		// Document config = obtainConfig();

		// if (config == null) {
		// return false;
		// }
		// //
		// return config.getBoolean("i18nEnabled", false);
		return false;
	}

	@Override
	public String defaultLang() {
		// Document config = obtainConfig();
		// if (config == null) {
		// return null;
		// }
		// //
		// return config.getString("i18nDefault");
		return null;
	}

	// private void test(Object o) {
	// System.out.println("~~~"+o);
	// }
	@Override
	public Document convert(Document doc, String locale) throws Exception {
		if (doc == null) {
			return null;
		}
		if (StringUtil.isEmpty(locale)) {
			locale = defaultLang();
		}
		if (StringUtil.isEmpty(locale)) {
			return null;
		}
		//
		String key = doc.getString("key");
		// test("("+key+")"+"==>"+locale+":start");

		String cacheKey = key + "." + locale;
		// 试图从缓存获取
		Document d = convertedCache.get(cacheKey);
		if (d != null) {
			// test("("+key+")"+"==>"+locale+":from cache");
			return d;
		}
		// 不能修改doc,所以拷贝一份
		d = I18nUtil.copy(doc);
		if (convertInternal(d, locale)) {
			// test("("+key+")"+"==>"+locale+":created");
			// 写入缓存
			convertedCache.put(cacheKey, d);
			//
			return d;
		} else {
			// test("("+key+")"+"==>"+locale+"Not convert");
			return null;
		}

	}

	// 用指定的locale转换输入的document
	private boolean convertInternal(Document doc, String locale) throws Exception {
		// 从文档得到i18n设置
		String i18n = doc.getString("i18n");

		if (StringUtil.isEmpty(i18n)) {
			// 没有设置i18n
			return false;
		}
		// 拼接locale得到真正的i18n资源文件
		i18n = i18n + "_" + locale + ".json";
		// 读取资源文件
		Document resource = loadResource(ServerLocator.getServer().obtainClassLoader(doc.getString("_package_name")),
				i18n);

		// System.out.println("i18n:"+i18n+"
		// _package_name:"+doc.getString("_package_name")
		// +"~~~~"+ServerLocator.getServer().obtainClassLoader(doc.getString("_package_name")));
		// if (resource==null) {
		// System.out.println("resource is null");
		// }else {
		// System.out.println("resource:"+resource.toJson());
		// }

		if (resource == null) {
			return false;
		}
		// 得到当前对应对应的资源
		// 得到对应的Document
		Document resourceSingle = (Document) resource.get(doc.getString("key"));
		if (resourceSingle == null) {
			return false;
		}
		// APPLY i18n
		I18nUtil.convert(doc, resourceSingle);
		//
		return true;
	}

	// 得到resource
	private Document loadResource(ClassLoader cl, String i18n) {
		// 试图从cache获取
		Document d = resourceCache.get(i18n);
		if (d != null) {
			return d;
		}
		//
		d = I18nUtil.loadI18n(cl, i18n);
		if (d != null) {
			resourceCache.put(i18n, d);
		}
		return d;
	}

	// // 得到authSolution的配置,没有返回null
	// private Document obtainConfig() {
	// try {
	// SolutionAuthService s =
	// context.getServer().getService(SolutionAuthService.class);
	// if (s == null) {
	// return null;
	// }
	// List<Document> list = s.find(null);
	// if (list != null && list.size() > 0) {
	// return list.get(0);
	// } else {
	// return null;
	// }
	// } catch (Exception e) {
	// // 正常不会到这里
	// e.printStackTrace();
	// return null;
	// }
	// }

	// clear cache
	private void reset() {
		if (resourceCache != null) {
			resourceCache.clear();
		}
		if (convertedCache != null) {
			convertedCache.clear();
		}
	}
}
