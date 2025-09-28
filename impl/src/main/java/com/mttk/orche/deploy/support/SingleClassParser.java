package com.mttk.orche.deploy.support;

import java.util.Arrays;

import org.bson.Document;
import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.agentTemplate.DefaultCallDefine;
import com.mttk.orche.addon.annotation.AgentTemplateFlag;
import com.mttk.orche.addon.annotation.PluginFlag;
import com.mttk.orche.addon.annotation.PluginFlag.DATA_TYPE;
import com.mttk.orche.core.Service;
import com.mttk.orche.util.StringUtil;

public class SingleClassParser {
	// private static Logger logger =
	// LoggerFactory.getLogger(SingleClassParser.class);

	public static void analysis(DeployResult result, Class clazz) throws Exception {

		// 处理class级别的标记
		handleClass(clazz, result);

	}

	// 处理class级别的filter定义
	private static void handleClass(Class clazz, DeployResult result) throws Exception {

		// 查看是否有Service定义
		ServiceFlag serviceFlag = (ServiceFlag) clazz.getAnnotation(ServiceFlag.class);
		if (serviceFlag != null) {

			// 如果没有实现Service接口忽略
			if (!(Service.class.isAssignableFrom(clazz))) {
				return;
			}
			result.addService(buildServiceByClass(serviceFlag, clazz));
			return;
		}

		// 查看是否有AgentTemplate定义
		AgentTemplateFlag agentTemplateFlag = (AgentTemplateFlag) clazz.getAnnotation(AgentTemplateFlag.class);
		if (agentTemplateFlag != null) {
			result.addAgentTemplate(buildAgentTemplateByClass(agentTemplateFlag, clazz));
			return;
		}

		// 查看是否有Plugin定义
		PluginFlag pluginFlag = (PluginFlag) clazz.getAnnotation(PluginFlag.class);
		if (pluginFlag != null) {
			result.addPlugin(buildPluginByClass(pluginFlag, clazz));
			return;
		}
	}

	// 处理class级别的Servicey定义
	private static Document buildServiceByClass(ServiceFlag serviceFlag, Class clazz) throws Exception {
		//
		Document doc = new Document();
		//
		if (serviceFlag.type() == SERVICE_TYPE.SYS) {
			doc.append("category", Service.CATEGORY.SYS.name());
		} else {
			doc.append("category", Service.CATEGORY.USER.name());
		}
		// key
		String key = serviceFlag.key();
		if (StringUtil.isEmpty(key)) {
			key = buildKeyByClass(clazz);
		}
		doc.append("key", key);
		// name
		String name = serviceFlag.name();
		if (StringUtil.isEmpty(name)) {
			name = clazz.getSimpleName();
		}
		doc.append("name", name);
		doc.append("description", serviceFlag.description());
		//
		//

		doc.append("implClass", clazz.getCanonicalName());
		doc.append("autoStart", true);
		// type
		SERVICE_TYPE type = serviceFlag.type();
		if (type == SERVICE_TYPE.DETECT) {
			type = SERVICE_TYPE.USUAL;

		}
		doc.append("type", type.name());
		// depends
		if (serviceFlag.depends() != null && serviceFlag.depends().length > 0) {
			doc.append("depends", String.join(",", serviceFlag.depends()));
		}
		// props
		// if (serviceFlag.props() != null && serviceFlag.props().length > 0) {
		// Document d = new Document();
		// for (String prop : serviceFlag.props()) {
		// Split s = new Split(prop);
		// if (s.getPart1() != null) {
		// d.append(s.getPart1(), s.getPart2());
		// }
		// }
		// //
		// doc.append("props", d);
		// }
		// i18n
		// System.out.println("I18N:"+serviceFlag.i18n());
		doc.append("i18n", serviceFlag.i18n());
		//
		doc.put("ui", UIParser.parseByClass(clazz, name));
		//
		return doc;
	}

	// 处理class级别的AgentTemplate定义
	private static Document buildAgentTemplateByClass(AgentTemplateFlag agentTemplateFlag, Class clazz)
			throws Exception {
		//
		Document doc = new Document();
		//
		doc.append("type", "agentTemplate");
		// key
		String key = agentTemplateFlag.key();
		if (StringUtil.isEmpty(key)) {
			key = buildKeyByClass(clazz);
		}
		doc.append("key", key);
		// name
		String name = agentTemplateFlag.name();
		if (StringUtil.isEmpty(name)) {
			name = clazz.getSimpleName();
		}
		doc.append("name", name);
		doc.append("description", agentTemplateFlag.description());
		//
		// doc.append("icon", agentTemplateFlag.icon());
		// implClass
		doc.append("implClass", clazz.getCanonicalName());
		// 临时存放,持久化前前删除
		// doc.append("serviceClass", agentTemplateFlag.getClass().getCanonicalName());
		// doc.append("serviceClass", agentTemplateFlag.serviceClass());
		//
		// props
		if (agentTemplateFlag.props() != null && agentTemplateFlag.props().length > 0) {
			doc.append("props", (Arrays.asList(agentTemplateFlag.props())));
		}

		Class callDefineClass = agentTemplateFlag.callDefineClass();
		if (callDefineClass == null || callDefineClass.equals(Object.class)) {
			callDefineClass = DefaultCallDefine.class;

		}

		//
		doc.put("ui_call", UIParser.parseByClassBean(callDefineClass, "data"));
		// 解析工具参数
		doc.put("tool-call", ToolParaSupport.parse(callDefineClass));
		//
		doc.append("i18n", agentTemplateFlag.i18n());
		doc.put("ui", UIParser.parseByClass(clazz, name));
		//
		return doc;
	}

	// 处理class级别的Plugin定义
	private static Document buildPluginByClass(PluginFlag pluginFlag, Class clazz) throws Exception {
		//
		Document doc = new Document();
		//
		doc.append("type", "plugin");
		// key
		String key = pluginFlag.key();
		if (StringUtil.isEmpty(key)) {
			key = buildKeyByClass(clazz);
		}
		doc.append("key", key);
		// name
		String name = pluginFlag.name();
		if (StringUtil.isEmpty(name)) {
			name = clazz.getSimpleName();
		}
		doc.append("name", name);

		//
		// doc.append("icon", pluginFlag.icon());
		// implClass
		doc.append("implClass", clazz.getCanonicalName());
		// 临时存放,持久化前前删除
		// doc.append("serviceClass", pluginFlag.getClass().getCanonicalName());
		doc.append("serviceClass", pluginFlag.serviceClass());
		//
		doc.append("dataType", pluginFlag.dataType().name());

		if (pluginFlag.dataType() == DATA_TYPE.CUSTOMIZE && pluginFlag.dataClass() != null
				&& !Object.class.equals(pluginFlag.dataClass())) {
			//
			doc.put("ui_data", UIParser.parseByClassBean(pluginFlag.dataClass(), "data"));
		}
		//
		doc.append("i18n", pluginFlag.i18n());
		doc.put("ui", UIParser.parseByClass(clazz, name));
		//
		return doc;
	}

	// 从类名构建key,把点号换成下划线
	private static String buildKeyByClass(Class clazz) {
		return clazz.getCanonicalName().replace('.', '_');
	}
}
