package com.mttk.orche.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mttk.orche.util.StringUtil;

/**
 * 通过default实现针对数据的增删改查
 *
 */
public interface PersistSupport {
	/**
	 * 获得collection实现
	 * 
	 * @return
	 */
	public MongoCollection<Document> obtainCollection() throws Exception;

	/**
	 * 得到持久化监听器列表
	 * 
	 * @return
	 */
	public default List<PersistListener> getPersistListeners() throws Exception {
		return null;
	}

	/**
	 * 添加持久化事件监听器
	 * 
	 * @param persistListener
	 * @throws Exception
	 */
	public default void addPersistListener(PersistListener persistListener) throws Exception {

	}

	/**
	 * 移除持久化事件监听器
	 * 
	 * @param persistListener
	 * @throws Exception
	 */
	public default void removePersistListener(PersistListener persistListener) throws Exception {

	}

	/**
	 * 根据条件搜索<br>
	 * 建议适用 com.mongodb.client.model.Filters构建查询条件。
	 * 
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public default List<Document> find(Bson filter) throws Exception {
		return find(filter, 0, 0, null);
	}

	/**
	 * 条件搜索，支付分页和排序
	 * 
	 * @param filter
	 * @param skip
	 * @param limit
	 * @param sort   可以为空
	 * @return
	 * @throws Exception
	 */
	public default List<Document> find(Bson filter, int skip, int limit, Bson sort) throws Exception {
		if (skip < 0) {
			skip = 0;
		}
		if (limit <= 0) {
			limit = Integer.MAX_VALUE;
		}
		final List<Document> list = new ArrayList<Document>(100);
		MongoCollection<Document> collection = obtainCollection();
		FindIterable<Document> fi = null;
		if (filter == null) {
			fi = collection.find().skip(skip).limit(limit);
		} else {
			fi = collection.find(filter).skip(skip).limit(limit);
		}
		if (sort != null) {
			fi = fi.sort(sort);
		}

		fi.into(list);
		// fi.forEach((Document document)->{});
		//
		return list;
	}

	/**
	 * 根据某个字段查找
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public default List<Document> find(String key, Object value) throws Exception {
		return find(Filters.eq(key, value));
	}

	/**
	 * 统计满足条件的记录数量
	 * 
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public default long count(Bson filter) throws Exception {
		MongoCollection<Document> collection = obtainCollection();
		if (filter == null) {
			return collection.countDocuments();
		} else {
			return collection.countDocuments(filter);
		}
	}

	/**
	 * 根据编号得到一条数据
	 * 
	 * @param _id
	 * @return
	 * @throws Exception
	 */
	public default Optional<Document> load(String _id) throws Exception {
		return load(Filters.eq("_id", new ObjectId(_id)));
	}

	/**
	 * 根据某个字段得到一条数据
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public default Optional<Document> load(String key, Object value) throws Exception {
		return load(Filters.eq(key, value));
	}

	/**
	 * 根据查询条件得到要一条数据
	 * 
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public default Optional<Document> load(Bson filter) throws Exception {
		FindIterable<Document> fi = null;
		if (filter == null) {
			fi = obtainCollection().find();
		} else {
			fi = obtainCollection().find(filter);
		}
		//
		return Optional.ofNullable(fi.first());
	}

	/**
	 * 插入数据
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public default String insert(Document document) throws Exception {
		//
		if (document == null) {
			throw new RuntimeException("Document is null,insert failed");
		}
		//
		Date now = new Date();
		document.append("_insertTime", now);
		document.append("_updateTime", now);
		//
		firePreInsert(document);
		//
		obtainCollection().insertOne(document);
		//
		firePostInsert(document);
		//
		ObjectId objectId = document.get("_id", ObjectId.class);
		return objectId.toString();
	}

	/**
	 * 替换数据。<br>
	 * 替换数据使用参数中的document替换相同编号的数据
	 * 
	 * @param document
	 * @return 如果给定_id不存在，返回null
	 * @throws Exception 输入文档为空或数据库中不存在给定编号数据
	 */
	public default String replace(Document document) throws Exception {
		//
		if (document == null) {
			throw new RuntimeException("Document is null,replace failed");
		}
		//
		ObjectId objectId = document.get("_id", ObjectId.class);
		if (objectId == null) {
			throw new RuntimeException("No ObjectId is found in document,replace failed");
		}
		// Find original document
		FindIterable<Document> fi = obtainCollection().find(Filters.eq("_id", objectId));
		Document originalDocument = fi.first();
		if (originalDocument == null) {
			// Not found
			return null;
		}
		//
		Object _insertTime = originalDocument.get("_insertTime");
		if (_insertTime != null) {
			document.append("_insertTime", _insertTime);
		} else {
			document.append("_insertTime", new Date());
		}
		document.append("_updateTime", new Date());
		//
		firePreUpdate(originalDocument, document, true);
		//
		obtainCollection().replaceOne(Filters.eq("_id", objectId), document);
		//
		firePostUpdate(originalDocument, document, true);
		//
		return objectId.toString();
	}

	/**
	 * 更新数据。<br>
	 * 更新数据适用参数中的document更新相同编号的数据<br>
	 * document中存在的字段都会更新到数据库中的文档中，不存在的字段使用数据库中的值
	 * 
	 * @param document
	 * @return
	 * @throws Exception 输入文档为空或数据库中不存在给定编号数据
	 */
	public default String update(Document document) throws Exception {
		//
		if (document == null) {
			throw new RuntimeException("Document is null,upadte failed");
		}
		//
		Object _id = document.get("_id");
		if (_id == null) {
			throw new RuntimeException("No ObjectId is found in document,update failed");
		}
		//
		ObjectId objectId = null;
		if (_id instanceof ObjectId) {
			objectId = (ObjectId) _id;
		} else if (_id instanceof String) {
			objectId = new ObjectId((String) _id);
		} else {
			throw new RuntimeException("Invalid Object id type ,update failed:" + _id.getClass() + "[" + _id + "]");
		}
		// Find original document
		FindIterable<Document> fi = obtainCollection().find(Filters.eq("_id", objectId));
		Document originalDocument = fi.first();
		if (originalDocument == null) {
			// Not found
			return null;
		}
		//
		document.append("_updateTime", new Date());
		//
		firePreUpdate(originalDocument, document, false);
		// 把update文档转换成update请求的bson
		List<Bson> updates = new ArrayList<>();
		for (String key : document.keySet()) {
			if ("_id".equals(key)) {
				continue;
			}
			updates.add(Updates.set(key, document.get(key)));
		}
		//
		obtainCollection().updateOne(Filters.eq("_id", objectId), Updates.combine(updates));
		// 取得更新后的文档
		document = load("_id", objectId).orElse(document);
		//
		firePostUpdate(originalDocument, document, false);
		//
		return objectId.toString();
	}

	/**
	 * 保存文档<br>
	 * document中有_id调用replace不存在调用insert
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public default String save(Document document) throws Exception {

		if (StringUtil.notEmpty(document.get("_id"))) {
			return replace(document);
		} else {
			return insert(document);
		}
	}

	/**
	 * 删除给定编号的文档
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public default boolean delete(String id) throws Exception {
		//
		if (id == null) {
			throw new RuntimeException("Id is null,delete failed");
		}
		//
		ObjectId objectId = new ObjectId(id);
		FindIterable<Document> fi = obtainCollection().find(Filters.eq("_id", objectId));
		Document document = fi.first();
		if (document == null) {
			return false;
		}
		firePreDelete(document);
		obtainCollection().deleteOne(Filters.eq("_id", objectId));
		firePostDelete(document);
		return true;
	}

	public default void firePreDelete(final Document document) throws Exception {
		List<PersistListener> list = getPersistListeners();
		if (list == null || list.size() == 0) {
			return;
		}
		for (PersistListener e : list) {
			e.preDelete(document);
		}
	}

	public default void firePostDelete(final Document document) throws Exception {
		List<PersistListener> list = getPersistListeners();
		if (list == null || list.size() == 0) {
			return;
		}
		for (PersistListener e : list) {
			e.postDelete(document);
		}
	}

	//
	public default void firePreInsert(final Document document) throws Exception {
		List<PersistListener> list = getPersistListeners();
		if (list == null || list.size() == 0) {
			return;
		}
		for (PersistListener e : list) {
			e.preInsert(document);
		}
	}

	public default void firePostInsert(final Document document) throws Exception {
		List<PersistListener> list = getPersistListeners();
		if (list == null || list.size() == 0) {
			return;
		}
		for (PersistListener e : list) {
			e.postInsert(document);
		}
	}

	//
	public default void firePreUpdate(final Document originalDocument, final Document document, final boolean replace)
			throws Exception {
		List<PersistListener> list = getPersistListeners();
		if (list == null || list.size() == 0) {
			return;
		}
		for (PersistListener e : list) {
			e.preUpdate(originalDocument, document, replace);
		}
	}

	public default void firePostUpdate(final Document originalDocument, final Document document, final boolean replace)
			throws Exception {

		List<PersistListener> list = getPersistListeners();
		if (list == null || list.size() == 0) {
			return;
		}

		for (PersistListener e : list) {
			e.postUpdate(originalDocument, document, replace);
		}
	}
}
