package com.mttk.orche.support.bson;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

public class LongConverter implements Converter<Long> {
	public   void convert(Long value, StrictJsonWriter writer) {
		writer.writeRaw(value.toString());
	}
}
