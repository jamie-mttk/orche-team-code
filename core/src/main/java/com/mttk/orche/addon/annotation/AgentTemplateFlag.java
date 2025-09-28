package com.mttk.orche.addon.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 定义一个plugin的注解
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE })
public @interface AgentTemplateFlag {
	/**
	 * DEFAULT: 缺省方式,有一个query参数,描述为用户任务,模拟用户输入提示词
	 * CUSTOMIZE: 使用下面callModeClass给出数据维护的字段
	 */
	// enum CALL_MODE {
	// DEFAULT, CUSTOMIZE
	// }

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

	// /**
	// *
	// *
	// * @return
	// */
	// CALL_MODE callMode() default CALL_MODE.DEFAULT;

	// 定义界面调用Agent的界面参数,同时也定义了此Agent作为工具调用时的Tool定义参数
	Class callDefineClass() default Object.class;

	/**
	 * 属性,暂未使用
	 * 
	 * SUPPRORT_MEMBER:支持合作智能体
	 * 
	 * @return
	 */
	String[] props() default {};

	// 分类的key
	// 如果没有设置,使用方法上定义的第一个@FolderFlag,如果不存在使用方法所在类上定义的第一个@FolderFlag,否则放到"entry"下
	// String folder() default "";
	// 如果为空，由前端决定显示图标
	// 支持两种设置方式:系统支持的ICON或用户自定义图片(存放在某个package下)
	// String icon() default "";
	/**
	 * 多语种使用的资源文件
	 * 空代表不支持多语种
	 * 
	 * @return
	 */
	String i18n() default "";
}
