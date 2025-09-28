package com.mttk.orche.support.bson;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;
import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

public class DoubleConverter implements Converter<Double> {
	public void convert(Double value, StrictJsonWriter writer) {
		if (value != null && value.isNaN()) {
			// 必须输出"NaN"否则JSON是非法的
			writer.writeString(value.toString());
		} else {
			writer.writeRaw(value.toString());
		}
	}
}
