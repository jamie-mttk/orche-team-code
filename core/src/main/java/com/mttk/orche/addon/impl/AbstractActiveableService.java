package com.mttk.orche.addon.impl;

import java.util.Optional;

import org.bson.Document;

import com.mttk.orche.addon.Activeable;
import com.mttk.orche.core.impl.AbstractCacheBeanService;

/**
 * 支持激活和禁用的服务实现
 *
 * @param <T>
 */
public class AbstractActiveableService<T> extends AbstractCacheBeanService<T> implements Activeable {
	/**
	 * 继承者可以在此编写具体激活实现
	 * 
	 * @param doc
	 * @throws Exception
	 */
	public void doActive(Document doc) throws Exception {
	}

	/**
	 * 继承者可以在此编写具体禁用实现
	 * 
	 * @param doc
	 * @throws Exception
	 */
	public void doDeactive(Document doc) throws Exception {

	}

	@Override
	public void doStart() throws Exception {
		getStrategy().setActiveField("active");
		//
		super.doStart();
	}

	@Override
	public void active(String instance) throws Exception {
		Optional<Document> o = load(instance);
		if (o.isPresent()) {
			//
			// doActive(o.get());
			// 设置Active标记
			o.get().put(getStrategy().getActiveField(), true);
			replace(o.get());
		}
	}

	@Override
	public void deactive(String instance) throws Exception {
		Optional<Document> o = load(instance);
		if (o.isPresent()) {
			//
			// doDeactive(o.get());
			// 设置Active标记
			o.get().put(getStrategy().getActiveField(), false);
			replace(o.get());
		}
	}

	// 增加修改删除时自动active/deactive
	@Override
	public void postInsert(Document document) throws Exception {
		tryActive(document);
		//
		super.postInsert(document);
	}

	@Override
	public void postUpdate(Document originalDocument, Document document, boolean replace) throws Exception {
		tryDeactive(originalDocument);
		tryActive(document);
		//
		super.postUpdate(originalDocument, document, replace);
	}

	@Override
	public void preDelete(Document document) throws Exception {
		tryDeactive(document);
		//
		super.postDelete(document);
	}

	// 试图停止
	private void tryDeactive(Document document) {
		// System.out.println("###1:"+document);
		// System.out.println("###2:"+getStrategy());
		// System.out.println("###3:"+getStrategy().getActiveField());
		if (document.getBoolean(getStrategy().getActiveField(), false)) {
			try {
				doDeactive(document);
			} catch (Exception e) {
				// 忽略错误
			}
		}
	}

	private void tryActive(Document document) throws Exception {
		if (document.getBoolean(getStrategy().getActiveField(), false)) {
			doActive(document);
		}
	}
}
