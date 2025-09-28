package com.mttk.orche.deploy.support;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.addon.annotation.ui.Controls;
import com.mttk.orche.addon.annotation.ui.Panel;
import com.mttk.orche.addon.annotation.ui.Panels;
import com.mttk.orche.addon.annotation.ui.Tab;
import com.mttk.orche.addon.annotation.ui.TabFolder;
import com.mttk.orche.addon.annotation.ui.TabFolders;
import com.mttk.orche.addon.annotation.ui.Table;
import com.mttk.orche.addon.annotation.ui.Tables;
import com.mttk.orche.addon.annotation.ui.Tabs;
import com.mttk.orche.util.StringUtil;

public class UISupport {
	// 生成控件生成UI
	static Document genUI(List<Object> as, String label) throws Exception {

		Document ui = toUI(parseRawList(as, null), label);
		return ui;
	}

	// ***************************************
	// * 把List<Document>处理成ui
	// ***************************************
	// defaultParent可以为null,不为null时在控件没有设置parent时使用他
	private static List<Document> parseRawList(List<Object> as, String defaultParent) {

		List<Document> list = new ArrayList<>(as.size());
		//
		for (Object a : as) {
			if (a instanceof Controls) {
				Controls cs = (Controls) a;
				for (Control c : cs.value()) {
					list.add(parseControl(c, null, defaultParent));
				}
			} else if (a instanceof Control) {
				list.add(parseControl((Control) a, null, defaultParent));
			} else if (a instanceof TabFolders) {
				TabFolders ts = (TabFolders) a;
				for (TabFolder t : ts.value()) {
					list.addAll(parseTabFolder(t, defaultParent));
				}
			} else if (a instanceof TabFolder) {
				list.addAll(parseTabFolder((TabFolder) a, defaultParent));
			} else if (a instanceof Tabs) {
				Tabs ts = (Tabs) a;
				for (Tab t : ts.value()) {
					list.add(parseTab(t, defaultParent));
				}
			} else if (a instanceof Tab) {
				list.add(parseTab((Tab) a, defaultParent));
			} else if (a instanceof Panels) {
				Panels ps = (Panels) a;
				for (Panel p : ps.value()) {
					list.add(parsePanel(p, defaultParent));
				}
			} else if (a instanceof Panel) {
				list.add(parsePanel((Panel) a, defaultParent));
			} else if (a instanceof Tables) {
				Tables ts = (Tables) a;
				for (Table t : ts.value()) {
					list.add(parseTable(t, defaultParent));
				}
			} else if (a instanceof Table) {
				list.add(parseTable((Table) a, defaultParent));
			} else if (a instanceof AbstractWrap) {
				//
				Document doc = parseAbstractWrap((AbstractWrap) a, defaultParent);
				if (doc != null) {
					list.add(doc);
				}
			} else {
				// 其他的都是我们不需要处理的,如ActionFlag之类的
				// System.out.println(a);
			}
		}

		return list;
	}

	// 把list变成有层级关系的
	private static List<Document> hierachy(List<Document> fullList, String root) {
		// 以null为父节点的,放到根下面
		List<Document> list = handle(fullList, root);
		// 处理以_parent的tabs
		List<Document> list1 = handle(fullList, "_parent");
		for (Document doc : list1) {
			if ("tab".equals(doc.getString("mode"))) {
				list.add(doc);
			}
		}
		return list;
	}

	private static Document toUI(List<Document> fullList, String label) {
		Document ui = new Document();
		ui.put("label", label);
		ui.put("mode", "config");
		//
		ui.put("children", hierachy(fullList, null));
		return ui;
	}

	private static List<Document> handle(List<Document> fullList, String parent) {
		List<Document> list = fullList.stream().filter(a -> isParentMatch(a, parent))
				.sorted(Comparator.comparing(c -> getDocSequence(c))).collect(Collectors.toList());
		for (Document doc : list) {
			if (StringUtil.notEmpty(doc.getString("key"))) {
				List<Document> l = handle(fullList, doc.getString("key"));
				if (l != null && l.size() > 0) {
					doc.put("children", l);
				}
			}
		}
		return list;
	}

	// 判断给定的doc是否是属于父亲parent
	private static boolean isParentMatch(Document doc, String parent) {
		if (StringUtil.isEmpty(parent)) {
			if (!doc.containsKey("parent")) {
				return true;
			} else {
				return StringUtil.isEmpty(doc.getString("parent"));
			}
		} else {
			if (!doc.containsKey("parent")) {
				return false;
			} else {
				// System.out.println("##"+(parent.equalsIgnoreCase(doc.getString("parent"))+"==>"+doc));
				return parent.equalsIgnoreCase(doc.getString("parent"));
			}
		}
	}

	// 获得doc的序号，没有返回0
	private static int getDocSequence(Document doc) {
		return doc.getInteger("sequence", 0);
	}

	//
	// ***************************************
	// * 处理控件生成Document
	// ***************************************
	private static Document parseControl(Control a, AbstractWrap p, String defaultParent) {
		Document doc = new Document();
		//
		// key
		String key = a.key();
		if (StringUtil.isEmpty(key)) {
			if (p != null) {
				key = p.getKey();
			}
		}
		if (key == null) {
			throw new RuntimeException("No key is set for :" + a);
		}

		doc.append("key", key);
		//
		String mode = a.mode();

		doc.append("mode", mode);
		doc.append("label", StringUtil.isEmpty(a.label()) ? key : a.label());
		if (StringUtil.notEmpty(a.description())) {
			doc.append("description", a.description());
		}
		if (StringUtil.notEmpty(a.defaultVal())) {
			doc.append("defaultVal", a.defaultVal());
		}
		// size
		doc.append("size", a.size());
		// mandatory
		doc.append("mandatory", a.mandatory());
		//
		handleParent(doc, a.parent(), defaultParent);
		handleBindings(doc, a.bindings());
		handleValidates(doc, a.validates());
		handleProps(doc, a.props());
		// 特别处理数据类型为数字时加入特殊props
		if (p != null && isNumeric(p.getType())) {
			Document props = (Document) doc.get("props");
			if (props == null) {
				props = new Document();
				doc.put("props", props);
			}
			props.put("dataType", "number");
		}

		//
		return doc;
	}

	private static boolean isNumeric(Class type) {
		return type.equals(Integer.class) || type.equals(Byte.class) || type.equals(Short.class)
				|| type.equals(Long.class) || type.equals(Double.class) || type.equals(Float.class)
				|| type.equals(BigDecimal.class) || type.equals(int.class) || type.equals(byte.class)
				|| type.equals(short.class) || type.equals(long.class) || type.equals(double.class)
				|| type.equals(float.class);

	}

	private static List<Document> parseTabFolder(TabFolder a, String defaultParent) {
		List<Document> list = new ArrayList<>();
		Document doc = new Document();
		//
		// key
		doc.append("key", a.key());
		//
		doc.append("mode", "tabFolder");

		handleParent(doc, a.parent(), defaultParent);
		handleBindings(doc, a.bindings());
		//
		list.add(doc);
		// 检查是否有定义的tabs
		if (a.tabs() != null && a.tabs().length > 0) {
			for (Tab t : a.tabs()) {
				Document d = parseTab(t, defaultParent);
				if (StringUtil.isEmpty(d.get("parent", ""))) {
					d.put("parent", doc.getString("key"));
				}
				list.add(d);
			}
		}
		//
		return list;
	}

	private static Document parseTab(Tab a, String defaultParent) {
		Document doc = new Document();
		//
		// key
		doc.append("key", a.key());
		if (StringUtil.isEmpty(a.label())) {
			doc.append("label", a.key());
		} else {
			doc.append("label", a.label());
		}
		if (StringUtil.notEmpty(a.description())) {
			doc.append("description", a.description());
		}
		//
		doc.append("mode", "tab");

		handleParent(doc, a.parent(), defaultParent);
		handleBindings(doc, a.bindings());
		//
		return doc;
	}

	private static Document parsePanel(Panel a, String defaultParent) {
		Document doc = new Document();
		//
		// key
		doc.append("key", a.key());
		doc.append("label", a.label());
		if (StringUtil.notEmpty(a.description())) {
			doc.append("description", a.description());
		}
		//
		doc.append("mode", "panel");

		handleParent(doc, a.parent(), defaultParent);
		handleBindings(doc, a.bindings());
		//
		return doc;
	}

	private static Document parseTable(Table a, String defaultParent) {
		Document doc = new Document();
		//
		// key
		doc.append("key", a.key());
		if (StringUtil.notEmpty(a.label())) {
			doc.append("label", a.label());
		}
		if (StringUtil.notEmpty(a.description())) {
			doc.append("description", a.description());
		}
		//
		doc.append("mode", "table");

		handleProps(doc, a.props());
		// 处理operates
		handleTableOperates(doc);
		//
		handleParent(doc, a.parent(), defaultParent);
		handleBindings(doc, a.bindings());
		//
		return doc;
	}

	// 处理表格的operates属性
	private static void handleTableOperates(Document doc) {
		Document props = (Document) doc.get("props");
		if (props == null) {
			return;
		}
		//
		String operates = props.getString("operates");
		if (StringUtil.isEmpty(operates)) {
			return;
		}
		//
		String[] ops = operates.split(",");
		List<String> buttonsConfig = new ArrayList<>(ops.length);
		List<String> emptyButtonsConfig = new ArrayList<>(1);
		//
		for (String op : ops) {
			buttonsConfig.add(op);
			if ("_add".equals(op)) {
				emptyButtonsConfig.add(op);
			}
		}

		//
		props.append("buttonsConfig", new Document().append("max", buttonsConfig.size()).append("list", buttonsConfig))
				.append("emptyButtonsConfig",
						new Document().append("list", emptyButtonsConfig));
	}

	private static Document parseAbstractWrap(AbstractWrap p, String defaultParent) {

		// if
		// (p.getAnnotatedElement().isAnnotationPresent(Bean.class)||p.getType().isAnnotationPresent(Bean.class))
		// {
		// return parseWrapBean(p,defaultParent);
		// }else
		if (p.getAnnotatedElement().isAnnotationPresent(Table.class)) {
			//
			return parseWrapTable(p, defaultParent);
		} else if (p.getAnnotatedElement().isAnnotationPresent(Control.class)) {
			//
			return parseControl(p.getAnnotatedElement().getAnnotation(Control.class), p, defaultParent);
		} else {
			return null;
		}
	}

	// private static Document parseWrapBean(AbstractWrap p,String defaultParent) {
	// Bean bean=p.getAnnotatedElement().getDeclaredAnnotation(Bean.class);
	// if (bean==null) {
	// bean=(Bean)p.getType().getDeclaredAnnotation(Bean.class);
	// }
	// //如果有，则认为是一个bean
	// Document doc = new Document();
	// //
	// // key
	// doc.append("key", p.getKey());
	// doc.append("label", bean.label());
	// //
	// doc.append("mode", "panel");
	//
	// handleParent(doc, bean.parent(), defaultParent);
	// handleBindings(doc, bean.bindings());
	// //解析此bean的所有字段
	// Annotation[] as=p.getType().getAnnotations();
	// List<Object> list=new ArrayList<>(as.length+1);
	// //
	// for (int i=0;i<as.length;i++) {
	// list.add(as[i]);
	// }
	// Field[] fields=DynaApplySupport.getAllFields(p.getType());
	// for (int i=0;i<fields.length;i++) {
	// list.add(new FieldWrap(p.getType(),fields[i]));
	// }
	// doc.append("children", hierachy(parseRawList(list,p.getKey()),p.getKey()) );
	// //
	// return doc;
	// }
	private static Document parseWrapTable(AbstractWrap p, String defaultParent) {
		Table table = p.getAnnotatedElement().getDeclaredAnnotation(Table.class);
		Document doc = new Document();
		//
		// key
		doc.append("key", p.getKey());
		doc.append("label", table.label());
		//
		doc.append("mode", "table");

		//
		// 解析此table的所有字段
		Annotation[] anns = p.getAnnotatedElement().getAnnotations();
		List<Object> list = new ArrayList<>(anns.length);
		for (int i = 0; i < anns.length; i++) {
			if (anns[i].annotationType() != Table.class) {
				list.add(anns[i]);
			}
		}
		doc.append("children", hierachy(parseRawList(list, p.getKey()), p.getKey()));
		//
		handleProps(doc, table.props());
		handleParent(doc, table.parent(), defaultParent);
		handleBindings(doc, table.bindings());
		//
		return doc;
	}

	// private static Document parseSysHolder(SysHolder s,DEPLOY_TYPE deployType) {
	// Document doc = new Document();
	// //
	// // key
	// //
	// doc.append("key", "sysHolder");
	// doc.append("mode", "sysHolder");
	// handleParent(doc,s==null?"":s.parent());
	// //
	// Document props = new Document();
	// props.append("info",
	// s==null?true:s.info()).append("deployType",s==null||s.related()?deployType.name():null);
	// doc.append("props", props);
	// //
	// return doc;
	// }
	// ***************************************
	// * 一些基础的处理方法
	// ***************************************
	private static void handleParent(Document doc, String parent, String defaultParent) {
		Split s = null;
		if (StringUtil.notEmpty(parent)) {
			s = new Split(parent);
		} else {
			s = new Split(defaultParent);
		}
		if (s.getPart1() != null) {
			doc.append("parent", s.getPart1());
		}
		if (s.getPart2() != null) {
			doc.append("sequence", Integer.parseInt(s.getPart2()));
		}
	}

	private static void handleBindings(Document doc, String[] bindings) {
		Document d = new Document();
		for (String binding : bindings) {
			Split s = new Split(binding);
			if (s.getPart1() != null) {
				d.append(s.getPart1(), s.getPart2());
			}
		}
		//
		if (!d.isEmpty()) {
			doc.append("bindings", d);
		}
	}

	private static void handleValidates(Document doc, String[] validates) {
		List<Document> list = new ArrayList<>(validates.length);
		for (String validate : validates) {
			if (StringUtil.isEmpty(validate)) {
				continue;
			}
			// 检查是否带了前后的{}
			if (!validate.startsWith("{")) {
				validate = "{" + validate;
			}
			if (!validate.endsWith("}")) {
				validate = validate + "}";
			}
			//
			list.add(Document.parse(validate));
		}
		//
		if (list.size() > 0) {
			doc.append("validates", list);
		}
	}
	// private static void handleValidates(Document doc, String[] validates) {
	// Document d = new Document();
	// for (String validate : validates) {
	// if (StringUtil.isEmpty(validate)) {
	// continue;
	// }
	// String[] ss = validate.split(";");
	// // 处理rule部分
	// Split s = new Split(ss[0]);
	// if (s.getPart1() != null) {
	// d.append(s.getPart1(), s.getPart2());
	// }
	// // message部分
	// if (ss.length >= 2) {
	// s = new Split(ss[1]);
	// if (s.getPart1() != null) {
	// d.append(s.getPart1(), s.getPart2());
	// }
	// }
	// // trigger部分
	// if (ss.length >= 3) {
	// s = new Split(ss[2]);
	// if (s.getPart1() != null) {
	// d.append(s.getPart1(), s.getPart2());
	// }
	// }
	// }
	// //
	// if (!d.isEmpty()) {
	// doc.append("validates", d);
	// }
	// }

	private static void handleProps(Document doc, String[] props) {
		Document d = new Document();
		for (String prop : props) {
			Split s = new Split(prop);
			if (s.getPart1() != null) {
				d.append(s.getPart1(), s.getPart2());
			}
		}
		//
		if (!d.isEmpty()) {
			doc.append("props", d);
		}
	}

}
