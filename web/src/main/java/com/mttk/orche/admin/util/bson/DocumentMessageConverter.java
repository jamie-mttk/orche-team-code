package com.mttk.orche.admin.util.bson;

import java.io.IOException;

import org.bson.Document;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.Decoder;
import org.bson.codecs.DocumentCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.mttk.orche.support.BsonUtil;
import com.mttk.orche.util.IOUtil;

public class DocumentMessageConverter extends AbstractHttpMessageConverter<Document> {
    private static Logger logger = LoggerFactory.getLogger(DocumentMessageConverter.class);
    private static final Decoder<Document> CODEC = new DocumentCodec(new MyCodecRegistry(), new BsonTypeClassMap());

    public DocumentMessageConverter() {
        //
        super(MediaType.APPLICATION_JSON_UTF8);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // 表明只处理Document类型的参
        return Document.class.isAssignableFrom(clazz);
    }

    /**
     * 重写readlntenal 方法，处理请求的数据
     */
    @Override
    public Document readInternal(Class<? extends Document> clazz,
            HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        String temp = new String(IOUtil.toArray(inputMessage.getBody()), "UTF-8");

        return Document.parse(temp, CODEC);

    }

    /**
     * 重写writeInternal ，处理如何输出数据到response
     */
    @Override
    public void writeInternal(Document doc,
            HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        outputMessage.getBody().write(BsonUtil.doc2json(doc).getBytes("utf-8"));
    }

}
