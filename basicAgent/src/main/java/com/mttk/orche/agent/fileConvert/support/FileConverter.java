package com.mttk.orche.agent.fileConvert.support;

import java.io.InputStream;
import java.util.Map;

/**
 * 文件转换器接口
 * 统一的文件转换接口，支持不同格式的转换
 */
public interface FileConverter {

    /**
     * 文件转换
     * 
     * @param inputStream 输入流
     * @param callback    输出回调
     * @param options     转换选项（可选参数）
     * @throws Exception 转换异常
     */
    void convert(InputStream inputStream, OutputCallback callback,
            Map<String, Object> options) throws Exception;
}
