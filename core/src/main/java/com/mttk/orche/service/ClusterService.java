package com.mttk.orche.service;

import java.util.List;

import org.bson.Document;

import com.mttk.orche.core.PersistService;

public interface ClusterService extends PersistService {
	//列出集群所有的服务器,当前连接的服务器显示在第一位
	List<Document> listServers()throws Exception ;
	/**
	 * 判断当前服务器是否是给定类型的服务器、
	 * 此方法可以用于在一个集群环境下选举出一台服务器用于进行只允许一台服务器处理的操作
	 * @param type  0--id的哈希最小  1--id的哈希最大 不排除以后会支持其他值,当前如果给出其他值都认为是1
	 * @return true如果当前服务器是指定类型的服务器,如果当前服务器不支持集群也返回true
	 */
	//boolean isThisServer(int type);
}

