package com.mttk.orche.service;

import java.io.File;
import org.bson.Document;
import com.mttk.orche.core.PersistService;
import com.mttk.orche.service.support.DeployStrategy;

//added by Jamie @ verison 5.5
//deploy表里记录所有的部署
//包括如下字段
// _id,系统生成的唯一编号
// type - 组件类型，枚举型，字符串
// pk		- 组件原始的_id,对应组件实际表的id
//key -  组件key
//name - 组件name
//description - 组件description
//suppress   - true代表组件被禁用，再次部署也不会真正被部署
public interface DeployService extends PersistService {
	// 部署组件类型
	public enum TYPE {
		SERVICE, AGENT_TEMPALTE
	};

	/**
	 * 部署jar包
	 * 
	 * @param file     jar包文件的路径
	 * @param name     上传的文件名
	 * @param pattern  Regex通配符,可以为null代表所有
	 * @param strategy 参数类解释
	 * @return 返回包括 folders,actions,entryes,resources,throwables的列表
	 *         除了throwables为字符串列表外，其他为Document列表
	 *         如果保存了,Document包含_id等原始文件解析出来没有的数据
	 * @throws 异常
	 */
	public Document deploy(File file, String name, String pattern, DeployStrategy strategy) throws Exception;

	// 设置disable状态为true,注意此id为部署表_id,而不是组件表的_id
	// suppress首先会删除原始组件表的值，然后设置deploy表中的disable=true
	public boolean suppress(String id) throws Exception;

	public boolean unsuppress(String id) throws Exception;
}
