package com.mttk.orche.core.impl;

import org.bson.Document;

import com.mttk.orche.core.PersistListener;

//此类实现了基本的存储事件监听
public abstract class AbstractPersistWithListenerService extends AbstractPersistService implements PersistListener {
	@Override
	protected void doStart() throws Exception {
		super.doStart();
		//
		addPersistListener(this);
	}

	@Override
	protected void doStop() throws Exception {
		removePersistListener(this);
		//
		super.doStart();
	}

	// 下面是缺省实现,
	@Override
	public void preInsert(Document document) throws Exception {
		validate(document, null);
	}

	@Override
	public void postInsert(Document document) throws Exception {

	}

	@Override
	public void preUpdate(Document originalDocument, Document document, boolean replace) throws Exception {
		validate(document, originalDocument);
	}

	@Override
	public void postUpdate(Document originalDocument, Document document, boolean replace) throws Exception {
		// System.out.println("@@@@@@@@@@@@@@@@");
	}

	@Override
	public void preDelete(Document document) throws Exception {

	}

	@Override
	public void postDelete(Document document) throws Exception {

	}

	// 验证输入document是否合法
	// 如果是update给出originalDocument,否则为null
	protected void validate(Document document, Document originalDocument) {

	}
}
