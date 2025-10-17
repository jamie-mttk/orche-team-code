package com.mttk.orche.agent.fileConvert.support.pdf2md;

import com.mttk.orche.agent.fileConvert.support.ConvertOptions;
import com.mttk.orche.agent.fileConvert.support.ConvertUtil;
import com.mttk.orche.agent.fileConvert.support.FileConverter;
import com.mttk.orche.agent.fileConvert.support.OutputCallback;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.cos.COSName;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PDF 文档转 Markdown 转换器
 * 支持表格提取和转换为 Markdown 表格格式
 */
public class PdfToMarkdownConverter implements FileConverter {

    private OutputCallback callback;

    /**
     * 实现 FileConverter 接口的转换方法
     */
    @Override
    public void convert(InputStream inputStream, OutputCallback callback,
            Map<String, Object> options) throws Exception {
        this.callback = callback;

        byte[] pdfBytes = inputStream.readAllBytes();

        try (PDDocument document = Loader.loadPDF(pdfBytes);
                OutputStream output = callback.getOutputStream(null);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"))) {

            // 释放内存
            pdfBytes = null;

            convertInternal(document, writer, options);
        }
    }

    /**
     * 内部转换方法
     */
    private void convertInternal(PDDocument document, BufferedWriter writer,
            Map<String, Object> options) throws Exception {

        // 获取选项值
        boolean includePageNumbers = ConvertUtil.getOptionValue(options, ConvertOptions.PDF_INCLUDE_PAGE_NUMBERS,
                false);
        boolean parseTable = ConvertUtil.getOptionValue(options, "PDF_PARSE_TABLE", true);

        int numberOfPages = document.getNumberOfPages();

        // 一次性提取所有页面的元素（图片、文本段落、表格）
        List<PageElements> allPageElements = extractAllPageElements(document, numberOfPages, parseTable);

        // 遍历所有页面（逐页处理）
        for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
            // 输出页码标题（如果启用）
            if (includePageNumbers) {
                writer.write("# 页面 " + (pageIndex + 1));
                writer.newLine();
                writer.newLine();
            }

            PageElements elements = allPageElements.get(pageIndex);

            // 输出页面内容（文字和表格）
            outputPageContent(document, pageIndex, elements, parseTable, writer);

            // 输出该页的图片
            outputImages(elements.getImages(), writer);

            // 如果不包含页码，在页面间添加分隔符
            if (!includePageNumbers && pageIndex < numberOfPages - 1) {
                writer.write("---");
                writer.newLine();
                writer.newLine();
            }
        }

        writer.flush();
    }

    /**
     * 一次性提取所有页面的元素（图片、文本段落、表格）
     */
    private List<PageElements> extractAllPageElements(PDDocument document, int numberOfPages, boolean parseTable)
            throws IOException {
        List<PageElements> allPageElements = new ArrayList<>();

        // 1. 先提取所有页面的图片（在使用 ObjectExtractor 之前）
        List<List<String>> allPageImages = new ArrayList<>();
        for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
            PDPage page = document.getPage(pageIndex);
            List<String> imageMarkdowns = extractImagesFromPage(page);
            allPageImages.add(imageMarkdowns);
        }

        if (parseTable) {
            // 2. 提取所有页面的文本段落
            List<List<TextParagraph>> allPageParagraphs = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
                List<TextParagraph> paragraphs = PdfToMarkdownUtil.extractParagraphs(document, pageIndex);
                allPageParagraphs.add(paragraphs);
            }

            // 3. 一次性提取所有页面的表格
            List<List<Table>> allPageTables = new ArrayList<>();
            try (ObjectExtractor extractor = new ObjectExtractor(document)) {
                SpreadsheetExtractionAlgorithm algorithm = new SpreadsheetExtractionAlgorithm();

                for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
                    try {
                        Page page = extractor.extract(pageIndex + 1);
                        List<Table> tables = algorithm.extract(page);
                        allPageTables.add(tables);
                    } catch (Exception e) {
                        // 单个页面提取失败，添加空列表
                        allPageTables.add(new ArrayList<>());
                    }
                }
            }

            // 4. 组装 PageElements
            for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
                PageElements elements = new PageElements(
                        allPageParagraphs.get(pageIndex),
                        allPageTables.get(pageIndex),
                        allPageImages.get(pageIndex));
                allPageElements.add(elements);
            }
        } else {
            // 纯文本模式，只有图片
            for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
                PageElements elements = new PageElements(
                        new ArrayList<>(),
                        new ArrayList<>(),
                        allPageImages.get(pageIndex));
                allPageElements.add(elements);
            }
        }

        return allPageElements;
    }

    /**
     * 输出页面内容（文字和表格）
     */
    private void outputPageContent(PDDocument document, int pageIndex, PageElements elements,
            boolean parseTable, BufferedWriter writer) throws IOException {
        if (parseTable) {
            // 解析表格：智能混合文字和表格
            processPageWithData(elements, writer);
        } else {
            // 纯文本提取：直接使用 PDFTextStripper
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageIndex + 1);
            stripper.setEndPage(pageIndex + 1);
            stripper.setSortByPosition(true);

            String pageText = stripper.getText(document);
            if (pageText != null && !pageText.trim().isEmpty()) {
                writer.write(pageText.trim());
                writer.newLine();
                writer.newLine();
            }
        }
    }

    /**
     * 输出图片
     */
    private void outputImages(List<String> imageMarkdowns, BufferedWriter writer) throws IOException {
        for (String imageMarkdown : imageMarkdowns) {
            if (imageMarkdown != null && !imageMarkdown.isEmpty()) {
                writer.write(imageMarkdown);
                writer.newLine();
                writer.newLine();
            }
        }
    }

    /**
     * 处理单个页面，基于文字与表格位置关系进行智能排序
     * 优化版本：按顺序处理，避免重复计算
     */
    private void processPageWithData(PageElements elements, BufferedWriter writer) throws IOException {
        List<TextParagraph> paragraphs = elements.getParagraphs();
        List<Table> remainingTables = new ArrayList<>(elements.getTables());

        // 处理每个段落
        for (TextParagraph para : paragraphs) {
            // 循环：输出所有应该在当前文字之前的表格
            while (!remainingTables.isEmpty()) {
                int outputOrder = getOutputOrder(para, remainingTables.get(0));

                if (outputOrder == 0) {
                    // 文字在表格内，跳过该文字，中断循环
                    break;
                } else if (outputOrder == 1) {
                    // 文字在表格前面，输出文字，中断循环
                    break;
                } else {
                    // outputOrder == -1: 表格在文字前面，输出表格，删除第一个表格，继续循环
                    Table table = remainingTables.remove(0);
                    writer.write(PdfToMarkdownUtil.convertTableToMarkdown(table));
                    writer.newLine();
                    // writer.newLine();
                }
            }

            // 只有当文字不在表格内时才输出文字
            if (remainingTables.isEmpty() || getOutputOrder(para, remainingTables.get(0)) == 1) {
                writer.write(para.getText());
                writer.newLine();
                // writer.newLine();
            }
        }

        // 输出剩余表格
        for (Table table : remainingTables) {
            writer.write(PdfToMarkdownUtil.convertTableToMarkdown(table));
            writer.newLine();
            writer.newLine();
        }
    }

    /**
     * 获取文字和表格的输出顺序
     * 
     * @param para  文本段落
     * @param table 表格
     * @return 0: 文字在表格内（跳过文字）
     *         1: 文字在表格前面（先输出文字）
     *         -1: 表格在文字前面（先输出表格）
     */
    private int getOutputOrder(TextParagraph para, Table table) {
        int relation = PdfToMarkdownUtil.calculateRelation(para, table);

        // 如果文字在表格内，跳过该文字
        if (relation == 0) {
            return 0; // 文字在表格内
        }

        // 如果文字在表格下方或右边（relation >= 5），表格应该先输出
        if (relation >= 5 && relation <= 8) {
            return -1; // 表格在前
        }

        // 其他情况（文字在表格上方或左边），文字应该先输出
        return 1; // 文字在前
    }

    /**
     * 从页面提取所有图片
     */
    private List<String> extractImagesFromPage(PDPage page) {
        List<String> imageMarkdowns = new ArrayList<>();

        try {
            PDResources resources = page.getResources();
            if (resources == null) {
                return imageMarkdowns;
            }

            // 遍历所有 XObject（图片资源）
            for (COSName name : resources.getXObjectNames()) {
                try {
                    var xobject = resources.getXObject(name);
                    if (xobject instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) xobject;
                        String imageMarkdown = handleImageViaCallback(image);
                        if (imageMarkdown != null) {
                            imageMarkdowns.add(imageMarkdown);
                        }
                    }
                } catch (Exception e) {
                    // 单个图片提取失败，继续处理其他图片
                }
            }
        } catch (Exception e) {
            // 页面资源访问失败，返回空列表
        }

        return imageMarkdowns;
    }

    /**
     * 通过回调处理图片
     */
    private String handleImageViaCallback(PDImageXObject image) {
        try {
            // 将 PDImageXObject 转换为字节数组
            BufferedImage bufferedImage = image.getImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // 根据图片类型选择格式
            String format = PdfToMarkdownUtil.getImageFormat(image.getSuffix());
            ImageIO.write(bufferedImage, format, baos);

            byte[] imageData = baos.toByteArray();
            return callback.handleImage(null, imageData);
        } catch (Exception e) {
            return null;
        }
    }

}
