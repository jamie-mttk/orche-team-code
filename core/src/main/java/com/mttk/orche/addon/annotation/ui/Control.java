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
 * Control用于定义基础控件，如输入框、列表框等.
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Repeatable(Controls.class)
public @interface Control {
	/**
	 * 唯一标识，通过此参数从AdapterConfig中获取值<br>
	 * 如果注解是在类上或方法上,此参数必须填写<br>
	 * 如果注解是在类的成员变量上,此参数可以不填写，部署时自动使用成员变量的名称<br>
	 * 如果注解是在方法参数上，此参数可以不填写，部署时自动使用参数名称（注意：只有在Java1.8以及javac
	 * -parameters打开的时候才能获取参数,否则只能得到arg0,arg1）
	 * 
	 * @return
	 */
	String key() default "";

	/**
	 * 说明控件类型，包括<br>
	 * input 输入框 <br>
	 * select 下拉框<br>
	 * checkbox 单选框<br>
	 * popup 弹出选择框<br>
	 * button 按钮<br>
	 * dummy 不显示,用于表明有此数据但是不在编辑界面显示和编辑(但是可以在列表界面显示)
	 * 
	 * @return
	 */
	String mode() default "input";

	/**
	 * 标签，不同注解会显示标签在不同位置
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
	 * 缺省值，如果是布尔类型或数字类型，仍然输入字符型由系统转换<br>
	 * 布尔类型输入“true”或“false”
	 * 
	 * @return
	 */
	String defaultVal() default "";

	/**
	 * 尺寸，缺省值为2。<br>
	 * 1代表占满整行;2代表占满1/2行;3代表占1/3行以此类推.<br>
	 * 不建议设置大于4的值。,超过24会自动设置为24.
	 * 负数代表实际占用格数(一行为24格),小于-24会自动设置为24
	 * 
	 * @return
	 */
	int size() default 1;

	/**
	 * 用于控制控件的显示和禁用<br>
	 * 显示使用关键字“show”,禁用使用关键字“disabled”<br>
	 * 如bindings={"show:this.data.protocol=='HTTPS'"}代表数据的protocol值为HTTPS时显示，
	 * protocol的值可能绑定某个下拉框控件<br>
	 * 如bindings={"show:this.data.threadPoolCustomize"}代表数据的threadPoolCustomize为true时显示,
	 * threadPoolCustomize可能绑定到某个checkbox控件<br>
	 * 注意this.data不能省略
	 * 
	 * @return
	 */
	String[] bindings() default {};

	/**
	 * 是否必须输入，true/false
	 * 
	 * @return
	 */
	boolean mandatory() default false;

	/**
	 * 控件校验，参考本节后续描述<br>
	 * 如果是判断控件是否必须输入，强烈建议使用mandatory<br>
	 * 前端适用element-ui,验证使用其自带的async-validator (
	 * https://github.com/yiminghe/async-validator )<br>
	 * 每一条验证都是一个JSON(JSON的前后{}可以忽略,部署时自动补上)。“message”给出出错时显示的内容。<br>
	 * 如validates= {"min:3,max:5,message:'长度必须在3到5之间'"}
	 * 
	 * @return
	 */
	String[] validates() default {};

	/**
	 * 属性根据mode不同而不同,详细参考"开发者手册"
	 * 
	 * @return
	 */
	String[] props() default {};

	/**
	 * 此注解对应控件所属的容器(如panel,tab)<br>
	 * 如果没有设置则显示在根容器里<br>
	 * 还可以定义parent为“_parent”的@Tab,此容器下的控件会显示在和根容器并列的tab里。如HTTP入口的“连接”和“路径映射”。<br>
	 * 格式: 容器(panel或tab)key(可省略，说明放在根容器下):显示顺序(可省略)<br>
	 * 如 panel1:10代表此控件显示在panel1里,显示顺序为10<br>
	 * 如panel2代表此控件显示在panel2里,显示顺序为缺省(按照出现顺序显示)<br>
	 * 如:10代表此控件显示在根容器里,显示顺序为10
	 * 
	 * @return
	 */
	String parent() default "";

}
