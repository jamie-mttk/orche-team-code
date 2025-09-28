package com.mttk.orche.addon;

import com.mttk.orche.core.PersistService;

/**
 * Plugin服务是一种特殊的服务,其一种服务能够有多重方式实现。<br>
 * 如认证服务，可以由数据库认证、AD认证等。<br>
 * 如缓存服务,可以有Ehcache、Redis等<br>
 * 如Lookup服务可以有内置的MongoDB，也可以有文件或外部数据库<br>
 * MongoDB里至少存放了两类数据:<br>
 * PLUGIN类型,给出Lookup数据支持的plugin
 * 类型，如mongodb,file等，type字段为TYPE_PLUGIN;每个plugin一个文档
 * 包括字段id,type,key(唯一标识一个plugin),name,description，implClass(PLugin实现类)<br>
 * plugin定义: 给出了一个具体的plugin定义,type=TYPE_DEFINE<br>
 * 包括字段id,type,name,description,plugin(向PLUGIN类型的key)<br>
 * 泛型P是能够得到的Pluginable类型<br>
 */
public interface PluginService<P extends Pluginable> extends PersistService {
	public static final String TYPE_PLUGIN = "plugin";
	public static final String TYPE_DEFINE = "define";

	//
	// void setContext(Context context);
	// /**
	// * 根据Plugin 定义的ID得到plugin对象
	// * @param id
	// * @return
	// */
	public P obtainPlugin(String id) throws Exception;
}
