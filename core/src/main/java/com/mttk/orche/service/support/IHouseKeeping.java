package com.mttk.orche.service.support;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;

//实现此接口用于支持数据清理
public interface IHouseKeeping {
	/**
	 * 把给定条件的数据保存到target collection后删除掉
	 * target为null代表直接删除
	 * 
	 * @param filter
	 * @param histNameSuffix 无需事先创建
	 * @return 删除的条数
	 */
	long houseKeeping(Bson filter, String histNameSuffix) throws Exception;

	/**
	 * 删除给定的历史表存储
	 * 
	 * @param name
	 * @return
	 */
	boolean purgeHist(String name);

	/**
	 * 列出所有历史表，包括 name和count(记录数)
	 * 
	 * @return
	 */
	List<Document> listHist() throws Exception;

	/**
	 * 从给定历史表中查询
	 * 
	 * @param histName
	 * @param filter
	 * @param skip
	 * @param limit
	 * @return
	 */
	List<Document> find(String histName, Bson filter, int skip, int limit, Bson sort) throws Exception;

	/**
	 * 从给定历史表中获取一条数据
	 * 
	 * @param histName
	 * @param _id
	 * @return
	 */
	Optional<Document> load(String histName, String _id) throws Exception;

	long count(String histName, Bson filter) throws Exception;

}
