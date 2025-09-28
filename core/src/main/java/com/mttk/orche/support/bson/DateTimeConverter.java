package com.mttk.orche.support.bson;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

public class DateTimeConverter implements Converter<Long> {
	public   void convert(Long value, StrictJsonWriter writer) {
		Date date=new Date(value);
		
		//这个格式是模仿缺省格式,否则无法导入
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Document doc=new Document();
		doc.append( "$date", sdf.format(date));
		writer.writeRaw(doc.toJson());
		
		//
		//System.out.println("~~~"+value+"::::::"+date+"::::::"+doc.toJson());
	}
}
