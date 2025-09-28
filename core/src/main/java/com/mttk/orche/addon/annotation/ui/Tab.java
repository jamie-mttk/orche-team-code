package com.mttk.orche.addon.annotation.ui;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Tab定义
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Repeatable(Tabs.class)
public @interface Tab {
	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#key()}
	 * 
	 * @return
	 */
	String key() default "";

	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#key()}
	 * 
	 * @return
	 */
	String label();

	/**
	 * 描述
	 * 一般会在控件的标题后面提供按钮弹出显示
	 * 
	 * @return
	 */
	String description() default "";

	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#bindings()}
	 * 
	 * @return
	 */
	String[] bindings() default {};

	/**
	 * 只能是TabFolder <br>
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#parent()}
	 * 
	 * @return
	 */
	String parent() default "";

}