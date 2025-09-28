package com.mttk.orche.admin.util.bson;

import java.util.Calendar;
import java.util.TimeZone;

import org.bson.BsonDateTime;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;;

//配合MyCodecRegistry使用
public class MyDateCodec implements Codec<Object> {
    @Override
    public void encode(final BsonWriter writer, final Object value, final EncoderContext encoderContext) {
        // writer.writeString((String)value);
        throw new UnsupportedOperationException("MyDateCodec can only be used as decode!");
    }

    @Override
    public Object decode(final BsonReader reader, final DecoderContext decoderContext) {
        long value = reader.readDateTime();
        // 需要扣除时否则更新后会相差几个小时
        value -= TimeZone.getDefault().getRawOffset();
        //
        return new BsonDateTime(value);
    }

    @Override
    public Class<Object> getEncoderClass() {
        return Object.class;
    }
}
