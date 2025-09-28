package com.mttk.orche.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;

import com.mttk.orche.core.PersistSupport;
import com.mttk.orche.service.support.IHouseKeeping;
import com.mttk.orche.support.MongoUtil;
import com.mttk.orche.util.StringUtil;

public abstract class AbstractHouseKeepingReadyService extends AbstractPersistService implements IHouseKeeping {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 返回HouseKeeping前缀名
	// 实现可以覆盖此方法给出新名称
	public String getHistPrefix() {
		String str = this.getClass().getSimpleName();
		if (str.endsWith("ServiceImpl")) {
			str = str.substring(0, str.length() - "ServiceImpl".length());
		}
		//
		return "sys" + str + "_";
	}

	@Override
	public long houseKeeping(Bson filter, String histNameSuffix) {
		logger.info("Start house keeping with filter:" + filter + " and history name suffix:" + histNameSuffix);
		final MongoCollection<Document> targetCol;
		MongoCollection<Document> thisCol = obtainCollection();
		if (StringUtil.notEmpty(histNameSuffix)) {
			targetCol = server.obtainCollection(buildhistColName(histNameSuffix));
			// 创建_insertTime索引,这样查询能快点
			// 重复创建也只会产生一个索引
			// 注意这个索引不一定都有价值
			targetCol.createIndex(Indexes.descending("_insertTime"));
		} else {
			targetCol = null;
		}
		//
		if (targetCol != null) {
			Consumer<Document> consumer = d -> {
				try {
					try {
						// 为了提高效率不检查是否存在先插入
						targetCol.insertOne(d);
					} catch (com.mongodb.MongoWriteException e) {
						// 如果出错试图更新
						targetCol.replaceOne(Filters.eq("_id", d.getObjectId("_id")), d);
					}
				} catch (Throwable t) {
					logger.error("Error copy document:" + MongoUtil.getId(d), t);
				}

			};
			thisCol.find(filter).forEach(consumer);
		}
		// 注意删除和迁移是两个不同的动作,因此如果处理过程中数据有变化可能导致数据丢失问题
		DeleteResult result = thisCol.deleteMany(filter);
		//
		long count = result.getDeletedCount();
		logger.info("Finish house keeping with filter:" + filter + " and history name suffix:" + histNameSuffix
				+ " returns:" + count);
		return count;
	}

	@Override
	public boolean purgeHist(String name) {
		String colName = buildhistColName(name);
		server.obtainMongoDatabase().getCollection(colName).drop();
		return true;
	}

	@Override
	public List<Document> listHist() {
		List<Document> list = new ArrayList<>();
		Iterator<String> i = server.obtainMongoDatabase().listCollectionNames().iterator();
		String name = null;
		Document doc = null;
		MongoCollection<Document> col = null;
		while (i.hasNext()) {
			name = i.next();
			if (name.startsWith(getHistPrefix())) {
				doc = new Document();
				doc.put("name", name.substring(getHistPrefix().length()));
				col = server.obtainCollection(name);
				doc.put("count", col.estimatedDocumentCount());
				list.add(doc);
			}
		}
		return list;
	}

	@Override
	public List<Document> find(String histName, Bson filter, int skip, int limit, Bson sort) throws Exception {
		if (StringUtil.isEmpty(histName)) {
			return this.find(filter, skip, limit, sort);
		} else {
			return buildHist(histName).find(filter, skip, limit, sort);
		}
	}

	@Override
	public Optional<Document> load(String histName, String _id) throws Exception {
		if (StringUtil.isEmpty(histName)) {
			return this.load(_id);
		} else {
			return buildHist(histName).load(_id);
		}
	}

	@Override
	public long count(String histName, Bson filter) throws Exception {
		if (StringUtil.isEmpty(histName)) {
			return this.count(filter);
		} else {
			return buildHist(histName).count(filter);
		}
	}

	protected PersistSupport buildHist(String histName) {
		return new PersistSupport() {
			public MongoCollection<Document> obtainCollection() {
				return server.obtainCollection(buildhistColName(histName));
			}
		};
	}
	// *************************
	// * 获取到历史表的collection名
	// *************************

	private String buildhistColName(String histName) {
		return getHistPrefix() + histName;
	}
}
