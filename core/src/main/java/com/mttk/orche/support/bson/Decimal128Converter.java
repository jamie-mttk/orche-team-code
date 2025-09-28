package com.mttk.orche.support.bson;

import static java.lang.String.format;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;
import org.bson.types.Decimal128;

public class Decimal128Converter implements Converter<Decimal128> {
	@Override
	public void convert(final Decimal128 value, final StrictJsonWriter writer) {
		if (value != null && value.isNaN()) {
			// 必须输出"NaN"否则JSON是非法的
			writer.writeString(value.toString());
		} else {
			writer.writeRaw(value.toString());
		}
	}
}
