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
 * 定义一个Tab容器，里面包含多个Tab
 * 可以直接在TabFolder下定义Tab,也可以单独定义Tab并通过Tab的parent与TabFolder关联
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Repeatable(TabFolders.class)
public @interface TabFolder {
	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#key()}
	 * 
	 * @return
	 */
	String key() default "";

	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#bindings()}
	 * 
	 * @return
	 */
	String[] bindings() default {};

	/**
	 * 可以包含多个Tab定义，和Tab定义时设置parent为TabFolder作用相同<br>
	 * 如下面的mainTab包含三个标签页<br>
	 * @TabFolder(key="mainTab",tabs= {<br>
	 * @Tab(key="mainTabBasic",label="基本"),<br>
	 * @Tab(key="mainTabProd",label="正式",bindings= {"show:env='PROD'"}),<br>
	 * @Tab(key="mainTabTest",label="测试",bindings= {"show:env='TEST'"})})
	 * 
	 * @return
	 */
	Tab[] tabs() default {};

	/**
	 * 参考 {@link com.mttk.orche.addon.annotation.ui.Control#parent()}
	 * 
	 * @return
	 */
	String parent() default "";

}
