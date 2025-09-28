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
 * 定义table
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Repeatable(Tables.class)
public @interface Table {
	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#key()}
	 * 
	 * @return
	 */
	String key() default "";

	/**
	 * 可选，如果设置了就显示，没设置就不显示<br>
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#key()}
	 * 
	 * @return
	 */
	String label() default "";

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
	 * 表格属性<br>
	 * 参考"开发者手册"
	 * 
	 * @return
	 */
	String[] props() default {};

	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#parent()}
	 * 
	 * @return
	 */
	String parent() default "";
}
