package com.mttk.orche.admin.util;

import java.util.List;

import org.bson.Document;

import com.mttk.orche.util.StringUtil;
//生成FORM表单代码 --- 没有使用
public class GenFormUtil {
	public static String generate( Document ui) throws Exception {
		StringBuffer sb=new StringBuffer(2048);
		// 生成FORM开�?
		sb.append("<el-form label-position=\"right\" label-width=\"20%\"  :model=\"data\"  ref=\"dataEditForm\">\n ");
		// 处理容器
		List<Document> components = (List<Document>) ui.get("components");
		handleContainer(sb, components);
		// 生成FORM结尾
		sb.append("</el-form>");
		//
		return sb.toString();
	}

	private static void handleContainer(StringBuffer sb, List<Document> components) throws Exception {
		for (Document c : components) {
			//
			String mode = c.getString("mode");
			if ("input".equals(mode)) {
				handleInput(sb, c);
			} else if ("select".equals(mode)) {
				handleSelect(sb, c);
			} else if ("checkbox".equals(mode)) {
				handleCheckbox(sb, c);
			} else if ("pop".equals(mode)) {
				handlePop(sb, c);
			} else if ("tabFolder".equals(mode)) {
				handleTabFolder(sb, c);
			} else if ("panel".equals(mode)) {
				handlePanel(sb, c);
			} else {
				// Not support
				handleNotSupport(sb, c);
			}
		}
	}

	private static void handleInput(StringBuffer sb, Document c) throws Exception {
		String key = c.getString("key");
		Document props=c.get("props", Document.class);
		String type = "text";
		
		if (props!=null) {
			type=props.get("type", "text");
		}
		//rows用于多行编辑�?说明显示行数
		String rows ="";
		if (props!=null) {
			rows=props.getString("rows");
		}
		if ("multiple".equals(type)) {
			type="textarea";
			if (StringUtil.isEmpty(rows)) {
				rows = "";
			} else {
				rows = " :rows=\"" + rows + "\"";
			}
		} else {
			rows = "";
		}
		//dataType用于在v-model后给出数据类型
		String dataType="";
		if (props!=null) {
			dataType=props.get("dataType","");
			if ("number".equals(dataType)) {
				dataType=".number";
			}else {
				dataType="";
			}
		}
		//		
		sb.append("<el-form-item  label=\"" + c.getString("label") + "\" prop=\"" + key + "\" "+calBindingShow(c)+">\n")
				.append("<el-input type=\"" + type + "\" v-model"+dataType+"=\"data." + key + "\"").append(rows)
				.append(" clearable "+calBindingDisabled(c)+"></el-input>\n").append("</el-form-item>\n");
	}

	private static void handleSelect(StringBuffer sb, Document c) throws Exception {
		String key = c.getString("key");
		Document props=c.get("props", Document.class);
		sb.append("<el-form-item  label=\"" + c.getString("label") + "\" prop=\"" + key + "\" "+calBindingShow(c)+">\n")
				.append("<el-select  v-model=\"data." + key + "\" clearable "+calBindingDisabled(c)+">\n");
		//options
		if(props!=null) {
			String options=props.getString("options");
			if (StringUtil.notEmpty(options)){
				String[] items=options.split(",");
				for (String item:items) {
					String[] os=item.split(":");
					String label=os.length==1?os[0]:os[1];
					sb.append("<el-option label=\""+label+"\" value=\""+os[0]+"\"></el-option>\n");
				}
			}
		}
		//
		sb.append("</el-select>\n").append("</el-form-item>\n");
	}

	private static void handleCheckbox(StringBuffer sb, Document c) throws Exception {
		String key = c.getString("key");
		sb.append("<el-checkbox v-model=\"data."+key+"\" "+calBindingShow(c)+calBindingDisabled(c)+">"+ c.getString("label") +"</el-checkbox>");
	}

	private static void handlePop(StringBuffer sb, Document c) throws Exception {
		//临时
		handleInput(sb,c);
	}

	private static void handleTabFolder(StringBuffer sb, Document c) throws Exception {
		List<Document> children=(List<Document>)c.get("children");
		//
		if (children==null||children.size()==0) {
			return;
		}
		sb.append("<el-tabs  type=\"border-card\" "+calBindingShow(c)+calBindingDisabled(c)+">");
		//处理children,只能是tab
		for (Document child:children) {
			String mode=child.getString("mode");
			if (!"tab".equals(mode)) {
				throw new Exception("Only tab is allowed under tabFolder:"+c);
			}
			sb.append("<el-tab-pane label=\""+child.getString("label")+"\" "+calBindingDisabled(child)+">");
			handleContainer(sb,(List<Document>)child.get("children"));
			sb.append("</el-tab-pane>");
		}
		//
		sb.append("</el-tabs>");
	}

	private static void handlePanel(StringBuffer sb, Document c) throws Exception {
		List<Document> children=(List<Document>)c.get("children");
		//
		if (children==null||children.size()==0) {
			return;
		}
		sb.append("<el-card class=\"box-card\"  shadow=\"hover\" header=\""+c.getString("label")+"\" "+calBindingShow(c)+calBindingDisabled(c)+">");
		handleContainer(sb,children);
		sb.append("</el-card>");
	}
	//如果给出了binding show则返回v-show字符串否则返�?"
	private static String calBindingShow(Document c) {
		Document doc=c.get("bindings",Document.class);
		if (doc==null) {
			return "";
		}
		String show=doc.getString("show");
		if (StringUtil.isEmpty(show)) {
			return "";
		}
		return " v-show=\""+show+"\" ";
	}
	//如果给出了binding disabled则返回v-show字符串否则返�?"
		private static String calBindingDisabled(Document c) {
			Document doc=c.get("bindings",Document.class);
			if (doc==null) {
				return "";
			}
			String disabled=doc.getString("disabled");
			if (StringUtil.isEmpty(disabled)) {
				return "";
			}
			return " :disabled=\""+disabled+"\" ";
		}
	private static void handleNotSupport(StringBuffer sb, Document c) throws Exception {
		throw new Exception("Unsupported component:"+c);
	}
}
