package com.mttk.orche.addon;

import java.util.Map;

/**
 * 服务上下文
 * 
 * @author jamie
 *
 */
public interface ServiceContext extends Context {
	// /**
	// * 通知事件<br>
	// * 实现调用了Context.createEvent和informEvent()<br>
	// * 对于入口编写，此方法更加快捷
	// * @param config 入口/服务配置
	// * @param content 错误描述
	// * @param t 异常
	// * @param infos 辅助参数,显示在界面用于排错
	// * @param payloads 辅助报文,显示在界面用于排错
	// */
	// public void entryEvent( AdapterConfig config, String content, Throwable t,
	// Map<String,Object> infos,
	// String[] payloads);
	// //不推荐外部用户使用此方法,不排除以后删除此方法
	// //设置一个threadLocal的变量为当前时间,以后调用executeAsync时可以根据此时间计算生成pipeline发的时间
	// //一般用于在轮询时统计轮询花了多久
	// //clear-false,设置
	// //clear=true，删除设置
	// public void tickNow(boolean clear);

}
