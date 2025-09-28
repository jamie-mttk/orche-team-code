package com.mttk.orche.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mttk.orche.core.PersistListener;
import com.mttk.orche.core.PersistService;
import com.mttk.orche.service.support.ThreadContext;
import com.mttk.orche.util.StringUtil;

/**
 * 基础的持久化实现
 *
 */
public abstract class AbstractPersistService extends AbstractService implements PersistService {
	private static Logger logger = LoggerFactory.getLogger(AbstractPersistService.class);
	private List<PersistListener> persistListenerList = new ArrayList<PersistListener>();
	private String collectionName = null;

	/**
	 * 继承此类的类返回其对应的collection name，用于增删改查
	 * 缺省的是去掉实现类后面可能有的Impl,Service,并把首字母修改为大写，并在前面加上sys
	 * 
	 * @return
	 */
	protected String getCollectionName() {
		String name = this.getClass().getSimpleName();
		if (name.endsWith("Impl")) {
			name = name.substring(0, name.length() - "Impl".length());
		}
		if (name.endsWith("Service")) {
			name = name.substring(0, name.length() - "Service".length());
		}
		Character ch = name.charAt(0);
		if (Character.isLowerCase(ch)) {
			name = Character.toUpperCase(ch) + name.substring(1);
		}
		//
		name = "sys" + name;
		//
		return name;
	}

	// 如果已经获取过collectionName，则直接返回;否则计算
	protected String getCollectionNameIfNecessary() {
		if (StringUtil.isEmpty(collectionName)) {
			collectionName = getCollectionName();
		}
		//
		return collectionName;
	}

	// 得到本服务对应的Mongo Collection
	public MongoCollection<Document> obtainCollection() {
		if (Thread.interrupted()) {
			logger.warn("Thread is interrupted. To avoid MongoDB error,clear interrupt flag.");
			// 由于清楚了interrupted标记,导致后续的流程无法知道当前thread其实已经被中断,因此
			ThreadContext.getCurrentContext().setInterrupted(true);
		}
		return server.obtainCollection(getCollectionNameIfNecessary());
	}

	//
	@Override
	public void addPersistListener(PersistListener persistentListener) {
		persistListenerList.add(persistentListener);
	}

	@Override
	public List<PersistListener> getPersistListeners() {
		return persistListenerList;
	}

	@Override
	public void removePersistListener(PersistListener persistentListener) {
		persistListenerList.remove(persistentListener);
	}
}
