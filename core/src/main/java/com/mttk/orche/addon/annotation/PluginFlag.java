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
public @interface PluginFlag {
	/**
	 * DEFAULT: plugin的缺省方式,不同plugin有不同的实现(也可能没有)
	 * NONE: plugin不需要数据维护
	 * CUSTOMIZE: plugin使用下面的dataClass给出数据维护的字段
	 */
	enum DATA_TYPE {
		DEFAULT, NONE, CUSTOMIZE
	}

	/**
	 * 参考 {@link com.mttk.api.addon.annotation.ActionFlag#key()}
	 * 
	 * @return
	 */
	String key() default "";

	/**
	 * 参考 {@link com.mttk.api.addon.annotation.ActionFlag#name()}
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
	 * 给出此plugin所属的服务class，必须提供
	 * 
	 * @return
	 */
	Class serviceClass();

	/**
	 * 定义plugin数据维护方式
	 * 
	 * @return
	 */
	DATA_TYPE dataType() default DATA_TYPE.DEFAULT;

	// 只有在type=CUSTOMIZE时才需要给出数据类的UI字段定义
	// 并非所有plugin都支持自定义数据,依赖于具体的plugin类型
	Class dataClass() default Object.class;
	/**
	 * plugin属性,暂未使用
	 * 
	 * @return
	 */
	// String[] props() default {};

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
