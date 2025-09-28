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
 * Panel是基础容器，有一个可选的标签label。<br>
 * Panel包含的控件从数据结构上不存在层级关系，也就是说控件A和Panel B都在根控件下，
 * Panel B包含控件C,则A和C从数据结构上在同一层。<br>
 * 主要用于更加清晰的组织控件，如一个panel包含的多个控件可以同时隐藏或显示。
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Repeatable(Panels.class)
public @interface Panel {
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
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#parent()}
	 * 
	 * @return
	 */
	String parent() default "";
}
