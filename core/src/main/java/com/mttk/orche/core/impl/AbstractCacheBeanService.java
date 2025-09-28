package com.mttk.orche.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.Filters;
import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.core.ObtainBeanable;
import com.mttk.orche.service.ClusterEventService.OPERATION;
import com.mttk.orche.support.MongoUtil;
import com.mttk.orche.util.StringUtil;

/**
 * 实现了带有对象缓存的实现 某些服务的对象，如httpEntry的JettyServer需要在运行过程中被创建/摧毁和缓存
 * T是缓存的对象的类型,可以为CacheWrap的子类或Document类型,也可以是其他自定义类型
 * 
 * @param <T>
 */
public abstract class AbstractCacheBeanService<T> extends AbstractClusterReadyService
		implements ObtainBeanable<T> {
	private Logger logger = LoggerFactory.getLogger(AbstractCacheBeanService.class);
	// Cache Map - 缓存了key-bean Map
	protected Map<String, T> cacheMap = new ConcurrentHashMap<>();
	// Cache List - Bean列表，只有在需要的时候才生成
	protected List<T> cacheList = null;
	//
	private CacheBeanStrategy strategy = new CacheBeanStrategy();

	// *********************************************************
	// * 以下方法可以被覆盖
	// *********************************************************
	/**
	 * 根据adapterConfig创建对象,返回null说明直接缓存AdapterConfig
	 * 
	 * @param adapterConfig
	 * @return
	 * @throws Exception
	 */
	protected T createObject(AdapterConfig adapterConfig) throws Exception {
		return null;
	}

	/**
	 * 此方法在对象创建并加入map后调用
	 * 
	 * @param t
	 */
	protected void objectCreated(T t) {
		resetList();
	}

	/**
	 * 此方法在对象从map里删除后调用
	 * 
	 * @param t
	 */
	protected void objectDestroyed(T t) {
		resetList();
	}

	// *********************************************************
	// * 提供给外部读取的方法
	// *********************************************************
	// 得到Strategy对象并赋值,赋值只能在doStart中完成;运行中修改可能导致不可预知的风险
	public CacheBeanStrategy getStrategy() {
		return strategy;
	}

	// 根据key查找,没有查找到返回null
	// 如果bean创建则自动创建
	@Override
	public T obtainBean(String key) throws Exception {
		if (strategy.isSuppressCache()) {
			throw new UnsupportedOperationException("Cache  is suppressed");
		}
		//
		T t = cacheMap.get(key);
		if (t != null) {
			return t;
		}
		// changed by jamie @2020/02/14
		// 定时入口的运行一次需要获取,所以禁用掉下面的代码
		// if (strategy.isAutoStart()) {
		// //自动启动说明激活的都启动了,所以直接返回null说明没有查找到
		// //active/deactive之类的操作只能调用load而不能调用obtainBean
		// return null;
		// }
		//
		Document doc = findDocByKey(key);
		if (doc != null) {
			return createInternalWrap(doc, true);
		} else {
			return null;
		}
	}

	// 获取所有的缓存的对象列表 -注意没有缓存的拿不到
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> obtainBeanList() {
		if (strategy.isSuppressCache()) {
			throw new UnsupportedOperationException("Cache  is suppressed");
		}
		List<T> list = cacheList;
		if (list == null) {
			// 如果没有数据则试图从cacheMap重建
			list = new ArrayList<>(cacheMap.size());
			list.addAll(cacheMap.values());
			// 试图排序
			if (list.size() > 0) {
				if (list.get(0) instanceof Comparable) {
					Collections.sort((List<Comparable>) list);
				}
			}
			// 没有考虑并发可能导致多次设置cacheList，但是从性能上可以接受
			// 正常的话CacheList调用都是针对变化不频繁的数据
			cacheList = list;
		}
		//
		return list;
	}

	/**
	 * 去掉某个key对应的缓存,并可能重新创建(auto且active时)
	 */
	public void evictCache(String key) throws Exception {
		destroyInternalWrap(key, false);
		Document doc = findDocByKey(key);
		if (isAutoCreate(doc)) {
			createInternalWrap(doc, false);
		}
	}

	// public void debugMe(String pos) {
	// System.out.println(pos+"==>"+cacheMap);
	// }
	// //调用此方法用于激活此bean
	// //注意必须在此之前修改document状态为active
	// public void activeBean(Document doc) {
	// if (isAutoStart()) {
	// createInternalWrap(doc, true);
	// }
	// //update时已经调用
	// //notifyChange(doc,OPERATION.UPDATE);
	// }
	// //调用此方法用于禁用此bean
	// public void deactiveBean(Document doc) {
	// //
	// destroyInternalWrap(obtainIdentify(doc), true);
	// //update时已经调用
	// //notifyChange(doc,OPERATION.UPDATE);
	// }
	// *********************************************************
	// * 继承一些方法实现对象的管理
	// *********************************************************
	@Override
	public void postInsert(Document document) throws Exception {
		if (isAutoCreate(document)) {
			createInternalWrap(document, true);
		}
		//
		super.postInsert(document);
	}

	@Override
	public void postUpdate(Document originalDocument, Document document, boolean replace) throws Exception {
		destroyInternalWrap(obtainIdentify(document), true);
		//
		if (isAutoCreate(document)) {
			createInternalWrap(document, true);
		}
		//
		super.postUpdate(originalDocument, document, replace);
	}

	@Override
	public void postDelete(Document document) throws Exception {
		destroyInternalWrap(obtainIdentify(document), true);
		//
		super.postDelete(document);
	}

	@Override
	public void doStart() throws Exception {
		super.doStart();
		//
		resetCache(true);
	}

	@Override
	public void doStop() throws Exception {
		super.doStop();
		//
		resetCache(false);
	}

	// *********************************************************
	// * 私有方法
	// *********************************************************
	private String obtainIdentify(Document doc) {
		String identifyField = strategy.getIdentifyField();
		String identify = null;
		if (StringUtil.isEmpty(identifyField)) {
			identify = MongoUtil.getId(doc);
		} else {
			identify = doc.getString(identifyField);
		}
		if (StringUtil.isEmpty(identify)) {
			throw new RuntimeException("No identify is found by [" + identifyField + "] from :" + doc);
		}
		return identify;
	}

	private String obtainIdentify(AdapterConfig config) {
		String identifyField = strategy.getIdentifyField();
		String identify = null;
		if (StringUtil.isEmpty(identifyField)) {
			identify = config.getId();
		} else {
			identify = config.getString(identifyField);
		}
		if (StringUtil.isEmpty(identify)) {
			throw new RuntimeException("No identify is found by [" + identifyField + "] from :" + config);
		}
		return identify;
	}

	// 创建并加入到Map或List中
	// 返回创建的对象
	// synchronized防止并发访问导致重复创建
	@SuppressWarnings("unchecked")
	private synchronized T createInternal(Document doc) throws Exception {
		// 得到编号
		String identify = obtainIdentify(doc);
		// 判断是否存在
		T t = cacheMap.get(identify);
		if (t != null) {
			return t;
		}
		// 创建AdapterConfig
		AdapterConfig adapterConfig =  context.createAdapterConfig(doc);

		// 创建
		t = createObject(adapterConfig);
		if (t == null) {
			// 返回null说明不是bean而是直接缓存A
			// 这里做强制类型转换,如果createObject返回null但是T不是Document会出错
			t = (T) adapterConfig;
		}
		if (t instanceof CacheWrap) {
			CacheWrap cacheWrap = (CacheWrap) t;
			cacheWrap.setId(MongoUtil.getId(doc));
			cacheWrap.setKey(identify);
			// 启动
			cacheWrap.start();
		}
		// 加入map
		cacheMap.put(identify, t);
		//
		objectCreated(t);
		//
		return t;
	}

	// 销毁并从Map/List中删除
	// 返回null代表没有找到无法删除
	private T destroyInternal(String identify) throws Exception {
		T t = cacheMap.get(identify);
		if (t != null) {
			if (t instanceof CacheWrap) {
				CacheWrap cacheWrap = (CacheWrap) t;
				cacheWrap.stop();
			}
			//
			cacheMap.remove(identify);
			//
			objectDestroyed(t);
			//
			return t;
		} else {
			return null;
		}
	}

	// // 判断给定的文档是否是激活状态
	private boolean isActive(Document doc) {
		String activeField = strategy.getActiveField();
		// 没有设置active field都认为是active的
		if (StringUtil.isEmpty(activeField)) {
			return true;
		}
		//
		return doc.getBoolean(activeField, true);
	}

	// Added by Jamie @2020/3/4
	// 不做此判断时禁用掉strategy.autostart=true的对象(如入口)时,禁用后会自动创建
	// 判断给定doc是否需要创建-如增加或更新以后
	private boolean isAutoCreate(Document doc) {
		return strategy.isAutoStart() && isActive(doc);
	}

	// 插入后并处理报错
	private T createInternalWrap(Document document, boolean raiseException) {
		// 如果不加入此判断可能导致创建了deactive的对象
		// 如果加入判断会导致无法得到deactive的对象,如我们想active对象的时候就会报错
		// changed by jamie @2020/02/14
		// 定时入口的运行一次需要获取,所以禁用掉下面的代码
		// if (!isActive(document)) {
		// return null;
		// }
		//
		try {
			return createInternal(document);
		} catch (Exception e) {
			logger.error("Create or start entry failed:" + document, e);
			//
			if (raiseException) {
				throw new RuntimeException(
						"Create or start entry failed because of " + e.getMessage() + ",entity:" + document, e);
			} else {
				//
				return null;
			}
		}

	}

	// 删除后并处理报错
	private T destroyInternalWrap(String identify, boolean raiseException) {
		try {
			return destroyInternal(identify);
		} catch (Exception e) {
			if (raiseException) {
				throw new RuntimeException("Stop or destroy entry failed", e);
			} else {
				logger.error("Stop or destroy entry failed:" + identify, e);
				return null;
			}
		}
	}

	// 清除列表缓存
	private void resetList() {
		cacheList = null;
	}

	// 根据key查找到Document,没有找到返回null
	private Document findDocByKey(String key) throws Exception {
		Optional<Document> o = null;
		if (StringUtil.isEmpty(strategy.getIdentifyField())) {
			o = load(key);
		} else {
			o = load(strategy.getIdentifyField(), key);
		}
		if (o.isPresent()) {
			return o.get();
		} else {
			return null;
		}
	}

	//
	private void resetCache(boolean createIfNeed) throws Exception {
		// clear all -stop and destroy
		for (String key : cacheMap.keySet()) {
			destroyInternalWrap(key, false);
		}
		//
		if (!strategy.isAutoStart() || !createIfNeed) {
			return;
		}
		//
		List<Document> list = null;
		if (StringUtil.isEmpty(strategy.getActiveField())) {
			if (strategy.getFilter() != null) {
				list = find(strategy.getFilter());
			} else {
				list = find(null);
			}
		} else {
			if (strategy.getFilter() != null) {
				list = find(Filters.and(strategy.getFilter(), Filters.eq(strategy.getActiveField(), true)));
			} else {
				list = find(strategy.getActiveField(), true);
			}
		}
		list.forEach((doc) -> {
			createInternalWrap(doc, false);
		});
	}

	//
	// 覆盖此方法用来更新缓存
	@Override
	public void dataChanged(String _id, OPERATION operation) throws Exception {
		super.dataChanged(_id, operation);
		//
		if (StringUtil.isEmpty(_id)) {
			resetCache(true);
			return;
		}
		switch (operation) {
			case INSERT:
				Optional<Document> o1 = load(_id);

				if (o1.isPresent() && isAutoCreate(o1.get())) {
					createInternalWrap(o1.get(), false);
				}
				break;
			case UPDATE:
				Optional<Document> o2 = load(_id);
				if (o2.isPresent()) {
					destroyInternalWrap(obtainIdentify(o2.get()), false);
					if (isAutoCreate(o2.get())) {
						createInternalWrap(o2.get(), false);
					}
				}
				break;
			case DELETE:
				// 由于数据库中已经没有此记录了,只能从Map中查找
				T t;
				// 试图从cache中删除
				for (String key : cacheMap.keySet()) {
					t = cacheMap.get(key);
					if (t instanceof CacheWrap) {
						//
						CacheWrap cacheWrap = (CacheWrap) t;
						if (_id.equals(cacheWrap.getId())) {
							// Found,la
							destroyInternalWrap(cacheWrap.getKey(), false);
							//
							break;
						}
					} else if (t instanceof AdapterConfig) {
						AdapterConfig d = (AdapterConfig) t;
						String identify = obtainIdentify(d);
						if (_id.equals(identify)) {
							destroyInternalWrap(identify, false);
							break;
						}

					} else {
						// 尝试是用id作为key
						if (_id.equals(key)) {
							destroyInternalWrap(_id, false);
							break;
						}
					}
				}
		}
	}
}
