package com.mttk.orche.addon.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ TYPE })
public @interface ServiceFlag {
	/**
	 * DETECT:根据类型自动判断
	 * ENTRY:入口服务,不提供Scheduler
	 * ENTRY_SCHEDULER:带定时器的入口
	 * ENTRY_NONE:入口,不带调用流程相关(流程、通道和执行策略)
	 * ENTRY_SYNC:入口，同步流程，带流程但是不带通道和执行策略
	 * USUAL:普通的服务,不提供界面编辑
	 * POOL:池管理服务
	 * SYS:系统服务,一般开发者不建议编写
	 */
	enum SERVICE_TYPE {
		DETECT, ENTRY, ENTRY_SCHEDULED, ENTRY_SYNC, ENTRY_NONE, USUAL, POOL, SYS
	}

	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ActionFlag#key()}
	 * 
	 * @return
	 */
	String key() default "";

	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ActionFlag#name()}
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * 描述
	 * 
	 * @return
	 */
	String description() default "";

	/**
	 * 类型，不同类型决定了维护界面的基本字段<br>
	 * 如果类型为DETECT,实现了PollingEntryService认为是ENTRY_SCHEDULED;
	 * 如果实现了EntryService则认为是ENTRY，否则认为是SERVICE;
	 * 如果用户指定了，则以用户指定的为准
	 * 
	 * @return
	 */
	SERVICE_TYPE type() default SERVICE_TYPE.DETECT;

	//
	// 分类的key
	// 如果没有设置,使用方法上定义的第一个@FolderFlag,如果不存在使用方法所在类上定义的第一个@FolderFlag,否则放到"entry"下
	// String folder() default "";
	// 如果为空，由前端决定显示图标
	// 支持两种设置方式:系统支持的ICON或用户自定义图片(存放在某个package下)
	// String icon() default "";
	/**
	 * 依赖的服务列表
	 * 
	 * @return
	 */
	String[] depends() default {};

	/**
	 * 服务属性,暂未使用
	 * 
	 * @return
	 */
	// String[] props() default {};
	/**
	 * 多语种使用的资源文件
	 * 空代表不支持多语种
	 * 
	 * @return
	 */
	String i18n() default "";
}
