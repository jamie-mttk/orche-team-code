package com.mttk.orche.core.impl;

import org.bson.Document;

import com.mttk.orche.service.ClusterEventService;
import com.mttk.orche.service.ClusterEventService.OPERATION;
import com.mttk.orche.support.MongoUtil;
import com.mttk.orche.support.ServerUtil;

/**
 * 当数据有变化时能够通知到Cluster的其他实例
 *
 */
public class AbstractClusterReadyService extends AbstractPersistWithListenerService {
	public void postInsert(Document document) throws Exception {
		notifyChange(document, OPERATION.INSERT);
		//
		super.postInsert(document);
	}

	@Override
	public void postUpdate(Document originalDocument, Document document, boolean replace) throws Exception {
		notifyChange(document, OPERATION.UPDATE);
		//
		super.postUpdate(originalDocument, document, replace);
	}

	@Override
	public void postDelete(Document document) throws Exception {
		notifyChange(document, OPERATION.DELETE);
		//
		super.postDelete(document);
	}

	// 通知Cluster事件引擎有新事件需要传播
	protected void notifyChange(Document document, OPERATION operation) throws Exception {
		// 如果不是在Cluster模式下没必要发出消息
		if (!ServerUtil.getClusterMode(server)) {
			return;
		}
		//
		server.getService(ClusterEventService.class).dataChanged(this, MongoUtil.getId(document), operation);
	}
	//

	// 当外部数据有变化时调用此方法通知数据发生变化
	// 继承程序可以覆盖此方法，实现如更新缓存
	public void dataChanged(String _id, OPERATION operation) throws Exception {
	}
}
