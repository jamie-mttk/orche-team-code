package com.mttk.orche.agent.fileConvert.support;

import java.io.OutputStream;

/**
 * 输出回调接口
 * 用于为转换过程提供输出流
 */
@FunctionalInterface
public interface OutputCallback {

    /**
     * 获取输出流
     * 
     * @param indicator 指示器对象
     *                  - Excel: Sheet对象
     *                  - Word/PPT: null
     * @return 输出流
     * @throws Exception 异常
     */
    OutputStream getOutputStream(Object indicator) throws Exception;

    /**
     * 处理图片
     * 
     * @param indicator 指示器对象（同 getOutputStream）
     * @param imageData 图片字节数据
     * @return 图片的 Markdown 引用字符串，如果返回 null 则忽略图片
     * @throws Exception 异常
     */
    default String handleImage(Object indicator, byte[] imageData) throws Exception {
        return null; // 默认忽略图片
    }
}
