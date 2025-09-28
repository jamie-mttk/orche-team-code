package com.mttk.orche.deploy.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

public class ToolParaSupport {
    public static Document parse(Class clazz) throws Exception {
        List<Object> as = UIParser.gatherAllByClass(clazz);
        //
        Document result = initObjectNode();
        //
        parseInternal(as, result, null);
        //
        return result;
    }

    // 初始化一个object类型的节点
    private static Document initObjectNode() {
        //
        Document result = new Document();
        result.append("type", "object");
        result.append("properties", new Document());
        result.append("required", new ArrayList<String>());
        return result;
    }

    private static void parseInternal(List<Object> as, Document result, String parent) throws Exception {
        //
        for (Object a : as) {

            //
            if (a instanceof Controls) {
                Controls cs = (Controls) a;
                for (Control c : cs.value()) {
                    parseControl(result, parent, c);
                }
            } else if (a instanceof Control) {
                parseControl(result, parent, (Control) a);
            } else if (a instanceof TabFolders) {
                TabFolders ts = (TabFolders) a;
                for (TabFolder t : ts.value()) {
                    parseHolder(as, result, parent, t.key(), t.parent(), false);
                }
            } else if (a instanceof TabFolder) {
                TabFolder t = (TabFolder) a;
                parseHolder(as, result, parent, t.key(), t.parent(), false);
            } else if (a instanceof Tabs) {
                Tabs ts = (Tabs) a;
                for (Tab t : ts.value()) {
                    parseHolder(as, result, parent, t.key(), t.parent(), true);
                }
            } else if (a instanceof Tab) {
                Tab t = (Tab) a;
                parseHolder(as, result, parent, t.key(), t.parent(), true);
            } else if (a instanceof Panels) {
                Panels ps = (Panels) a;
                for (Panel t : ps.value()) {
                    parseHolder(as, result, parent, t.key(), t.parent(), false);
                }
            } else if (a instanceof Panel) {
                Panel t = (Panel) a;
                parseHolder(as, result, parent, t.key(), t.parent(), false);
            } else if (a instanceof Tables) {
                Tables ts = (Tables) a;
                for (Table t : ts.value()) {
                    parseTable(as, result, parent, t);
                }
            } else if (a instanceof Table) {
                Table t = (Table) a;
                parseTable(as, result, parent, t);
            } else if (a instanceof AbstractWrap) {
                //
                parseAbstractWrap((AbstractWrap) a, as, result, parent);
            } else {
                // 其他的都是我们不需要处理的,如ActionFlag之类的
                // System.out.println(a);
            }
        }

    }

    private static void parseAbstractWrap(AbstractWrap p, List<Object> as, Document result, String parent)
            throws Exception {

        // if
        // (p.getAnnotatedElement().isAnnotationPresent(Bean.class)||p.getType().isAnnotationPresent(Bean.class))
        // {
        // return parseWrapBean(p,defaultParent);
        // }else
        if (p.getAnnotatedElement().isAnnotationPresent(Table.class)) {
            // p.getAnnotatedElement().getDeclaredAnnotation(Table.class);
            parseTable(as, result, parent, p.getAnnotatedElement().getDeclaredAnnotation(Table.class));
        } else if (p.getAnnotatedElement().isAnnotationPresent(Control.class)) {
            //
            parseControl(result, parent, p.getAnnotatedElement().getDeclaredAnnotation(Control.class));
        } else {

        }
    }

    private static void parseControl(Document result, String parent, Control c) throws Exception {
        if (!isParentMatch(parent, c.parent(), false)) {
            return;
        }
        //
        Document doc = new Document();
        //
        doc.append("description", StringUtil.isEmpty(c.description()) ? c.label() : c.description());
        doc.append("type", parseType(c));
        //
        result.get("properties", Document.class).append(c.key(), doc);
        if (c.mandatory()) {
            result.get("required", List.class).add(c.key());
        }

    }

    private static void parseHolder(List<Object> as, Document result, String parent, String key,
            String parentInAnnotation, boolean isTab)
            throws Exception {
        if (!isParentMatch(parent, parentInAnnotation, isTab)) {
            return;
        }
        // 把tabFolder所有值放到当前result下
        parseInternal(as, result, key);
    }

    private static void parseTable(List<Object> as, Document result, String parent, Table t)
            throws Exception {
        if (!isParentMatch(parent, t.parent(), false)) {
            return;
        }
        //
        Document doc = new Document();
        doc.append("type", "array");
        doc.append("description", StringUtil.isEmpty(t.description()) ? t.label() : t.description());
        //
        Document items = initObjectNode();
        doc.append("items", items);
        //
        result.get("properties", Document.class).append(t.key(), doc);
        // 处理Table下面的元素
        parseInternal(as, items, t.key());

    }

    // 解析control的数据类型
    private static String parseType(Control c) {

        if ("checkbox".equals(c.mode())) {
            return "boolean";
        }
        String[] props = c.props();
        if (props == null || props.length == 0) {
            return "string";
        }
        for (String prop : props) {
            if (prop.startsWith("dataType:")) {
                return prop.substring(9);
            }
        }
        return "string";
    }

    // 判断当前annotation中的parent是否匹配当前正在处理的parent,tab会有一些特殊逻辑

    private static boolean isParentMatch(String parent, String parentInAnnotation, boolean isTab) {
        if (StringUtil.isEmpty(parent)) {
            if ("_parent".equalsIgnoreCase(parentInAnnotation)) {
                // tab控件的_parent表示新的tab显示,所以也属于数据在根下
                return true;
            }
            return StringUtil.isEmpty(parentInAnnotation);
        } else {
            return parent.equalsIgnoreCase(parentInAnnotation);
        }
    }
}
