package com.mttk.orche.addon.annotation.ui;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于支持Table重复出现
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
public @interface Tables {
	Table[] value() default {};
}
