package com.mttk.orche.i18n;

import java.io.InputStream;
import java.util.List;

import org.bson.Document;

import com.mttk.orche.deploy.support.Split;

import com.mttk.orche.util.IOUtil;
import com.mttk.orche.util.StringUtil;

public class I18nUtil {
	//如果是使用classloader加载资源,不能带 / (使用Class的getResourceAsStream的可以带/)
	private  static String resolveName(String name) {
        if (name == null) {
            return name;
        }
        if (name.startsWith("/")) {
             name = name.substring(1);
        }
        return name;
    }
	
	
	// 读取资源文件
	public static Document loadI18n(ClassLoader cl, String i18n) {
		try {
			i18n=resolveName(i18n);
			try (InputStream is = cl.getResourceAsStream(i18n)) {
				if (is == null) {
					return null;
				}
				byte[] data = IOUtil.toArray(is);
				return Document.parse(new String(data, "utf-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 深度拷贝document
	public static Document copy(Document doc) {
		return Document.parse(doc.toJson());
	}

	// 执行转换逻辑
	public static void convert(Document doc, Document resource) {
		// name
		String name = resource.getString("name");
		if (StringUtil.notEmpty(name)) {
			doc.append("name", name);
		}
		// description
		String description = resource.getString("description");
		if (StringUtil.notEmpty(description)) {
			doc.append("description", description);
		}
		//convert ui
		convertUI(doc,resource,name);
			//convert ui_data仅仅针对plugin
		convertUI_data(doc,resource);
	}
	private static void convertUI(Document doc, Document resource,String name) {
		// 得到没转换文档
		Document uiDoc = (Document) doc.get("ui");
		if (uiDoc == null) {
			return;
		}
		if (StringUtil.notEmpty(name)) {
			// 转换ui的label
			uiDoc.append("label", name);
		}
		// 得到没转换文档的ui/children
		List<Document> uiChildren = (List<Document>) uiDoc.get("children");
		if (uiChildren == null || uiChildren.size() == 0) {
			return;
		}
		// 得到资源文件的ui节点
		Document uiResource = (Document) resource.get("ui");
		if (uiResource == null) {
			return;
		}
		//
		convertUI(uiChildren, uiResource);
	}
	
	private static void convertUI_data(Document doc, Document resource) {
		
		// 得到没转换文档
		Document uiDoc = (Document) doc.get("ui_data");
		if (uiDoc == null) {
			return;
		}

		// 得到没转换文档的ui/children
		List<Document> uiChildren = (List<Document>) uiDoc.get("children");
		if (uiChildren == null || uiChildren.size() == 0) {
			return;
		}
		// 得到资源文件的ui节点
		Document uiResource = (Document) resource.get("ui_data");
		if (uiResource == null) {
			return;
		}
		//
		convertUI(uiChildren, uiResource);
	}
	
	// 把给定的ui更新到uiChildren里
	private static void convertUI(List<Document> uiChildren, Document uiResource) {
		for (String key : uiResource.keySet()) {
			Object single = uiResource.get(key);
			//
			Document d = findMatchdingByKey(uiChildren, key);
			if (d == null) {
				// 隐含错误
				continue;
			}
			if (single instanceof String) {
				// String认为是对应key的label
				d.append("label", (String) single);

			} else if (single instanceof Document) {
				// 认为是包含了多个描述
				Document s = (Document) single;
				// label
				String label = s.getString("label");
				if (StringUtil.notEmpty(label)) {
					d.append("label", label);
				}
				// description
				String description = s.getString("description");
				if (StringUtil.notEmpty(description)) {
					d.append("description", description);
				}
				// props
				handleProps(d, s);
				// 看看是否有children
				//
				Document children = (Document) s.get("children");
				if (children == null) {
					continue;
				}
				// 原始ui的children
				Object childrenRaw = d.get("children");
				if (childrenRaw == null || !(childrenRaw instanceof List)) {
					// 必须是Document类型的List
					continue;
				}
				// i18n的children
				Object childrenNew = s.get("children");
				if (childrenNew == null || !(childrenNew instanceof Document)) {
					//
					continue;
				}
				//
				convertUI((List<Document>) childrenRaw, (Document) childrenNew);
			} else {

				// 隐含错误
			}
		}
	}

	private static void handleProps(Document d, Document s) {
		Document propsRaw = (Document) d.get("props");
		if (propsRaw == null) {
			return;
		}
		Object propsNew = s.get("props");
		if (propsNew == null) {
			return;
		}
		if (propsNew instanceof String) {
			handlePropsSingle(propsRaw, (String) propsNew);
		} else if (propsNew instanceof List) {
			for (String pn : (List<String>) propsNew) {
				handlePropsSingle(propsRaw, pn);
			}
		}
	}

	private static void handlePropsSingle(Document propsRaw, String propsNew) {
		// 适合于只有一个props的情形
		Split split = new Split((String) propsNew);
		if (split.getPart1() != null && propsRaw.containsKey(split.getPart1())) {
			propsRaw.append(split.getPart1(), split.getPart2());
		}
	}

	private static Document findMatchdingByKey(List<Document> uiChildren, String key) {
		for (Document c : uiChildren) {
			if (key.equals(c.getString("key"))) {
				return c;
			}
		}
		return null;
	}
}
