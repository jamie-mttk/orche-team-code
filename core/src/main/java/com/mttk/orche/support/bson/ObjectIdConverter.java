package com.mttk.orche.support.bson;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;
import org.bson.types.ObjectId;

public class ObjectIdConverter implements Converter<ObjectId> {
	public void convert(ObjectId value, StrictJsonWriter writer) {
		writer.writeString(value.toString());
	}
}
