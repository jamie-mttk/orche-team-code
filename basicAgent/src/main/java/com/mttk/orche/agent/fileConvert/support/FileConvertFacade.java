package com.mttk.orche.agent.fileConvert.support;

import com.mttk.orche.agent.fileConvert.support.excel2csv.ExcelToCsvConverter;
import com.mttk.orche.agent.fileConvert.support.pdf2md.PdfToMarkdownConverter;
import com.mttk.orche.agent.fileConvert.support.ppt2md.PptToMarkdownConverter;
import com.mttk.orche.agent.fileConvert.support.word2md.WordToMarkdownConverter;

import java.io.InputStream;
import java.util.Map;

/**
 * 文件转换门面类
 * 根据文件名后缀自动选择合适的转换器
 */
public class FileConvertFacade {

    /**
     * 转换文件
     * 
     * @param fileName    文件名（用于判断文件类型）
     * @param inputStream 输入流
     * @param callback    输出回调
     * @param options     转换选项
     * @throws Exception 转换失败或不支持的文件类型
     */
    public static void convert(String fileName, InputStream inputStream, OutputCallback callback,
            Map<String, Object> options) throws Exception {

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 获取文件后缀
        String extension = getFileExtension(fileName);
        if (extension == null || extension.isEmpty()) {
            throw new IllegalArgumentException("无法识别文件类型：文件名没有后缀 - " + fileName);
        }

        // 根据后缀选择转换器
        FileConverter converter = getConverter(extension);
        if (converter == null) {
            throw new UnsupportedOperationException("不支持的文件类型: " + extension + " (文件: " + fileName + ")");
        }

        // 执行转换
        converter.convert(inputStream, callback, options);
    }

    /**
     * 获取文件扩展名（小写）
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 根据文件扩展名获取对应的转换器
     */
    private static FileConverter getConverter(String extension) {
        switch (extension) {
            // PDF 转 Markdown
            case "pdf":
                return new PdfToMarkdownConverter();

            // Word 转 Markdown
            case "doc":
            case "docx":
                return new WordToMarkdownConverter();

            // PowerPoint 转 Markdown
            case "ppt":
            case "pptx":
                return new PptToMarkdownConverter();

            // Excel 转 CSV
            case "xls":
            case "xlsx":
                return new ExcelToCsvConverter();

            // 不支持的类型
            default:
                return null;
        }
    }

    // /**
    // * 检查是否支持指定的文件类型
    // *
    // * @param fileName 文件名
    // * @return true 表示支持，false 表示不支持
    // */
    // public static boolean isSupported(String fileName) {
    // if (fileName == null || fileName.trim().isEmpty()) {
    // return false;
    // }

    // String extension = getFileExtension(fileName);
    // if (extension == null || extension.isEmpty()) {
    // return false;
    // }

    // return getConverter(extension) != null;
    // }

    // /**
    // * 获取支持的文件类型列表
    // *
    // * @return 支持的文件扩展名数组
    // */
    // public static String[] getSupportedExtensions() {
    // return new String[] { "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx" };
    // }
}
