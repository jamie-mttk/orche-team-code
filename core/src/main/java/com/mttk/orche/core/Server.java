package com.mttk.orche.core;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * 服务器对象
 *
 */
public interface Server extends PersistService {
	// 服务器运行模式
	public enum RUNNING_MODE {
		NORMAL, // 通常模式
		MAINTANCE // 维护模式, 此模式下部分服务，如调度、定时器服务不启动,譬如用户在初始化的时候
	}

	/**
	 * 系统启动时间,java.util.Date类型
	 */
	public static final String TIME_START = "time.start";// java.util.Date
	/**
	 * 系统安装根目录(结尾不包含路径分隔符)
	 */
	public static final String PATH_HOME = "path.home";
	/**
	 * 系统的临时目录(结尾不包含路径分隔符)
	 */
	public static final String PATH_TEMP = "path.temp";
	/**
	 * 系统的配置目录(结尾不包含路径分隔符)
	 */
	public static final String PATH_CONF = "path.conf";
	/**
	 * 版本
	 */
	public static final String VERSION = "version";

	/**
	 * 是否运行在集群模式下,类型为Boolean
	 */
	public static final String CLUSTER_MODE = "cluster.mode";
	/**
	 * 系统唯一编号,集群模式下每个实例有唯一编号
	 */
	public static final String INSTANCE_ID = "instance.id";
	// 上一次启动的instance if
	public static final String INSTANCE_ID_LAST = "instance.id.last";
	/**
	 * 实例名称，为服务器名称
	 */
	public static final String INSTANCE_NAME = "instance.name";
	/**
	 * 集群模式下实例的IP
	 * 每个实例可能有多个IP,本IP指能够被其他实例访问的IP,往往为内部IP
	 */
	public static final String INSTANCE_IP = "instance.ip";
	/**
	 * 集群模式下的实例端口号
	 * 一般来说集群服务器访问端口号相同，但是可能做了端口映射时(如Docker里)就不同
	 */
	public static final String INSTANCE_PORT = "instance.port";

	/**
	 * 得到服务设置信息，具体参考后续表格
	 * 
	 * @param <T>
	 * @param key      参考Server的静态变量定义
	 * @param retClass
	 * @return
	 */
	public <T> T getSetting(String key, Class<T> retClass);

	/**
	 * 得到所有服务器支持的设置值键
	 * 
	 * @return
	 */
	public String[] settingKeys();

	//
	/*******************************************************************
	 * 这部分是与运行实例相关部分
	 *****************************************************************/
	/**
	 * 根据服务编号得到服务实例.如果不存在返回null
	 * 
	 * @param serviceId
	 * @return
	 */
	Service getService(String serviceId);

	/**
	 * 根据服务类得到服务实例.如果不存在返回null
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @return
	 */
	public <T extends Service> T getService(Class<T> serviceClass);

	/**
	 * 启动服务,不允许外部调用
	 * 
	 * @param serviceId
	 */
	public void startService(String serviceId);

	/**
	 * 停止服务,不允许外部调用
	 * 
	 * @param serviceId
	 */
	public void stopService(String serviceId);

	/**
	 * 列出所有服务
	 * 
	 * @param category
	 * @return
	 */
	public List<Service> listServices(CATEGORY category);

	/**
	 * 得到服务器当前运行模式
	 * 
	 * @return
	 */
	public RUNNING_MODE getRunningMode();

	/*******************************************************************
	 * 这部分是与基础存储(MongoDB)相关部分
	 *****************************************************************/
	/**
	 * 得到一个MongoClient对象<br>
	 * 此对象为全局共享,调用者不允许关闭此对象.<br>
	 * 建除非特殊情形，一般不建议调用此方法
	 * 
	 * @return
	 */
	MongoClient obtainMongoClient();

	/**
	 * 得到系统配置的mongo数据库
	 * 
	 * @return
	 */
	MongoDatabase obtainMongoDatabase();

	/**
	 * 得到指定名称的Mongodb Collection
	 * 
	 * @param collectionName
	 * @return
	 */
	MongoCollection<Document> obtainCollection(String collectionName);

	/*************************************************************************************
	 * 与Addon classloader相关
	 **************************************************************************************/
	/**
	 * 根据addon名得到class loader
	 * 如果addon有私有的lib则返回私有的，否则返回通用的
	 * 
	 * @param addonName
	 * @return
	 */
	ClassLoader obtainClassLoader(String addonName);
}
