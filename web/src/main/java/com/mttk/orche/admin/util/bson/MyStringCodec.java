package com.mttk.orche.admin.util.bson;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import  org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;;
//配合MyCodecRegistry使用
public class MyStringCodec implements Codec<Object> {
    @Override
    public void encode(final BsonWriter writer, final Object value, final EncoderContext encoderContext) {
          //writer.writeString((String)value);
    	throw new UnsupportedOperationException("MyStringCodec can only be used as decode!");
    }

    @Override
    public Object decode(final BsonReader reader, final DecoderContext decoderContext) {
    	if (reader.getCurrentBsonType() == BsonType.SYMBOL) {
            return reader.readSymbol();
        } else {
        	if ("_id".equals(reader.getCurrentName())){
        		return new ObjectId(reader.readString());
        	}else {
        		return reader.readString();
        	}           
        }
    }

    @Override
    public Class<Object> getEncoderClass() {
        return Object.class;
    }
}
