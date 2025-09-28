package com.mttk.orche.admin.util.bson;

import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import java.util.Date;

import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * 定制的codec注册,加入了当遇到_id时转换成ObjectId
 * @author jamie
 *
 */
public class MyCodecRegistry implements CodecRegistry {
	private static final CodecRegistry DEFAULT_REGISTRY = fromProviders(asList(new ValueCodecProvider(),
            new BsonValueCodecProvider(),
            new DocumentCodecProvider()));
	private static final Codec MY_STRING_CODEC=new MyStringCodec();	
	private static final Codec MY_DATE_CODEC=new MyDateCodec();	
	@Override
	public <T> Codec<T> get(Class<T> clazz) {
		//System.out.println("@@"+clazz+"==>"+DEFAULT_REGISTRY.get(clazz));
		if (clazz.equals(String.class)) {
			//System.out.println("~~~~"+clazz);
			return MY_STRING_CODEC;
		}else 	if (clazz.equals(Date.class)) {
			//System.out.println("~~~~"+clazz);
			return MY_DATE_CODEC;
		}
		//
		return DEFAULT_REGISTRY.get(clazz);
		
	}

}
