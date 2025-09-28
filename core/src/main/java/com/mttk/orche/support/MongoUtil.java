package com.mttk.orche.support;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.json.Converter;
import org.bson.json.JsonWriterSettings;
import org.bson.json.StrictJsonWriter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;

import com.mttk.orche.util.BeanUtil;

public class MongoUtil {
	/**
	 * 判断两个文档_id是否相等
	 * 
	 * @param document1
	 * @param document2
	 * @return
	 */
	public static boolean isSame(Document document1, Document document2) {
		if (document1 == null || document2 == null) {
			return false;
		}
		return document1.get("_id").equals(document2.get("_id"));
	}

	public static String id2string(ObjectId objectId) {
		return objectId == null ? null : objectId.toString();
	}

	// 得到主键
	public static String getId(Document document) {
		if (document == null) {
			return null;
		}
		if (document.containsKey("_id")) {
			Object objectId = document.get("_id");
			if (objectId instanceof ObjectId) {
				return id2string((ObjectId) objectId);
			} else if (objectId instanceof String) {
				return (String) objectId;
			} else {
				return null;
			}

		}
		//
		return null;
	}

	// 把一个document里的属性都设置到bean里
	public static void fillProps(Object bean, Document doc, Logger logger) {
		if (doc == null || doc.keySet() == null) {
			return;
		}
		doc.keySet().forEach((key) -> {
			try {
				BeanUtil.setProperty(bean, key, doc.get(key));
			} catch (Exception e) {
				// logger.info("@@@"+(!key.endsWith("_NAME")) );
				if (!key.endsWith("_NAME")) {
					// _NAME结尾的可能是SELECT产生的名称
					logger.warn("Set property for bean [" + bean + "] of property [" + key + "] with value ["
							+ doc.get(key) + "] failed", e);
				}
			}
		});
	}

	// 把一个document里的key/value对都设置到bean里
	public static void fillKeyValues(Object bean, List<Document> list, Logger logger) {
		if (list == null || list.size() == 0) {
			return;
		}
		list.forEach((d) -> {
			try {
				BeanUtil.setProperty(bean, d.getString("key"), d.get("value"));
			} catch (Exception e) {
				logger.error("Set property for bean [" + bean + "] failed with " + d, e);
			}
		});
	}

	//
	public static JsonWriterSettings createDefaultSettings() {
		JsonWriterSettings settings = JsonWriterSettings.builder().dateTimeConverter(new DateTimeConverter())
				.objectIdConverter(new ObjectIdConverter()).build();
		//
		return settings;
	}
}

// 把日期转换成yyyy/MM/dd HH:mm:ss的格式,也可以自定义格式
class DateTimeConverter implements Converter<Long> {
	private String format;

	public DateTimeConverter() {
		this("yyyy/MM/dd HH:mm:ss");
	}

	public DateTimeConverter(String format) {
		this.format = format;
	}

	public void convert(Long value, StrictJsonWriter writer) {
		Date date = new Date(value);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		writer.writeString(sdf.format(date));
	}
}

// 把ObjectID转换成字符串
class ObjectIdConverter implements Converter<ObjectId> {
	public void convert(ObjectId value, StrictJsonWriter writer) {

		writer.writeString(value.toString());
	}
}