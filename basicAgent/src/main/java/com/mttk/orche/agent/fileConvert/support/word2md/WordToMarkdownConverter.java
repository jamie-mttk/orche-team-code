package com.mttk.orche.agent.fileConvert.support.word2md;

import com.mttk.orche.agent.fileConvert.support.FileConverter;
import com.mttk.orche.agent.fileConvert.support.OutputCallback;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Base64;
import java.util.Map;

/**
 * Word 文档转 Markdown 转换器
 */
public class WordToMarkdownConverter implements FileConverter {

    private OutputCallback callback;

    /**
     * 实现 FileConverter 接口的转换方法
     */
    @Override
    public void convert(InputStream inputStream, OutputCallback callback,
            Map<String, Object> options) throws Exception {
        this.callback = callback;

        OutputStream output = callback.getOutputStream(null);
        convertInternal(inputStream, output);
    }

    /**
     * 内部转换方法
     */
    private void convertInternal(InputStream wordInput, OutputStream markdownOutput) throws IOException {

        try (XWPFDocument doc = new XWPFDocument(wordInput);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(markdownOutput, "UTF-8"))) {

            List<IBodyElement> bodyElements = doc.getBodyElements();

            for (IBodyElement element : bodyElements) {
                if (element instanceof XWPFParagraph) {
                    processParagraph((XWPFParagraph) element, writer);
                } else if (element instanceof XWPFTable) {
                    processTable((XWPFTable) element, writer);
                } else if (element instanceof XWPFSDT) {
                    // 处理结构化文档标签（内容控件）
                    processSDT((XWPFSDT) element, writer);
                }
            }

            writer.flush();
        } catch (Exception e) {
            throw new IOException("Word 文档转换失败", e);
        }
    }

    /**
     * 处理段落
     */
    private void processParagraph(XWPFParagraph paragraph, BufferedWriter writer) throws IOException {
        String style = paragraph.getStyle();
        String text = paragraph.getText();

        // 跳过空段落
        if (text == null || text.trim().isEmpty()) {
            // 检查是否有图片
            List<XWPFRun> runs = paragraph.getRuns();
            boolean hasImage = false;
            for (XWPFRun run : runs) {
                if (!run.getEmbeddedPictures().isEmpty()) {
                    hasImage = true;
                    break;
                }
            }
            if (!hasImage) {
                writer.newLine();
                return;
            }
        }

        // 处理标题 - 改进识别逻辑
        int headingLevel = detectHeadingLevel(paragraph, style);
        if (headingLevel > 0) {
            writer.write("#".repeat(headingLevel) + " ");
            writeFormattedText(paragraph, writer);
            writer.newLine();
            writer.newLine();
            return;
        }

        // 处理列表
        CTDecimalNumber numId = paragraph.getCTP().getPPr() != null &&
                paragraph.getCTP().getPPr().getNumPr() != null ? paragraph.getCTP().getPPr().getNumPr().getNumId()
                        : null;

        if (numId != null) {
            int ilvl = paragraph.getCTP().getPPr().getNumPr().getIlvl() != null
                    ? paragraph.getCTP().getPPr().getNumPr().getIlvl().getVal().intValue()
                    : 0;

            String indent = "  ".repeat(ilvl);

            // 尝试判断是有序还是无序列表（简化处理，统一用 - ）
            writer.write(indent + "- ");
            writeFormattedText(paragraph, writer);
            writer.newLine();
            return;
        }

        // 普通段落
        writeFormattedText(paragraph, writer);
        writer.newLine();
        writer.newLine();
    }

    /**
     * 写入带格式的文本
     */
    private void writeFormattedText(XWPFParagraph paragraph, BufferedWriter writer) throws IOException {
        List<XWPFRun> runs = paragraph.getRuns();

        for (XWPFRun run : runs) {
            // 处理图片
            List<XWPFPicture> pictures = run.getEmbeddedPictures();
            if (!pictures.isEmpty()) {
                for (XWPFPicture picture : pictures) {
                    String imageMarkdown = handleImageViaCallback(picture);
                    if (imageMarkdown != null && !imageMarkdown.isEmpty()) {
                        writer.write(imageMarkdown);
                    }
                }
                continue;
            }

            String text = run.getText(0);
            if (text == null || text.isEmpty()) {
                continue;
            }

            // 转义 Markdown 特殊字符
            text = escapeMarkdown(text);

            boolean isBold = run.isBold();
            boolean isItalic = run.isItalic();
            boolean isStrikethrough = run.isStrikeThrough();

            // 应用格式
            if (isBold && isItalic) {
                writer.write("***" + text + "***");
            } else if (isBold) {
                writer.write("**" + text + "**");
            } else if (isItalic) {
                writer.write("*" + text + "*");
            } else if (isStrikethrough) {
                writer.write("~~" + text + "~~");
            } else {
                writer.write(text);
            }
        }
    }

    /**
     * 通过回调处理图片
     */
    private String handleImageViaCallback(XWPFPicture picture) {
        try {
            XWPFPictureData pictureData = picture.getPictureData();
            byte[] imageData = pictureData.getData();
            return callback.handleImage(null, imageData);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 处理结构化文档标签（内容控件）
     * SDT 可以包含段落、表格等内容，需要递归处理
     */
    private void processSDT(XWPFSDT sdt, BufferedWriter writer) throws IOException {
        try {
            // 获取 SDT 的内容
            ISDTContent content = sdt.getContent();

            if (content != null) {
                // 尝试获取段落
                if (content instanceof XWPFParagraph) {
                    processParagraph((XWPFParagraph) content, writer);
                }
                // 尝试获取表格
                else if (content instanceof XWPFTable) {
                    processTable((XWPFTable) content, writer);
                }
                // 如果 content 是 XWPFSDTContent，递归处理其中的元素
                else if (content.getClass().getName().contains("XWPFSDTContent")) {
                    // XWPFSDTContent 通常包含段落或表格，尝试提取文本
                    String text = sdt.getContent().getText();
                    if (text != null && !text.trim().isEmpty()) {
                        writer.write(text);
                        writer.newLine();
                        writer.newLine();
                    }
                }
            }
        } catch (Exception e) {
            // SDT 处理失败，尝试简单提取文本
            try {
                String text = sdt.getContent().getText();
                if (text != null && !text.trim().isEmpty()) {
                    writer.write(text);
                    writer.newLine();
                    writer.newLine();
                }
            } catch (Exception ex) {
                // 忽略无法处理的 SDT
            }
        }
    }

    /**
     * 处理表格
     */
    private void processTable(XWPFTable table, BufferedWriter writer) throws IOException {
        List<XWPFTableRow> rows = table.getRows();
        if (rows.isEmpty()) {
            return;
        }

        // 处理表头
        XWPFTableRow headerRow = rows.get(0);
        List<XWPFTableCell> headerCells = headerRow.getTableCells();

        writer.write("|");
        for (XWPFTableCell cell : headerCells) {
            String cellText = cell.getText().replace("\n", " ").replace("|", "\\|");
            writer.write(" " + cellText + " |");
        }
        writer.newLine();

        // 写入分隔行
        writer.write("|");
        for (int i = 0; i < headerCells.size(); i++) {
            writer.write(" --- |");
        }
        writer.newLine();

        // 处理数据行
        for (int i = 1; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            List<XWPFTableCell> cells = row.getTableCells();

            writer.write("|");
            for (XWPFTableCell cell : cells) {
                String cellText = cell.getText().replace("\n", " ").replace("|", "\\|");
                writer.write(" " + cellText + " |");
            }
            writer.newLine();
        }

        writer.newLine();
    }

    /**
     * 检测标题级别（改进版）
     * 支持多种标题样式识别，包括中文和英文，以及大纲级别
     */
    private int detectHeadingLevel(XWPFParagraph paragraph, String style) {
        // 方法1：通过大纲级别判断（最可靠）
        int outlineLevel = -1;
        if (paragraph.getCTP().getPPr() != null &&
                paragraph.getCTP().getPPr().getOutlineLvl() != null) {
            outlineLevel = paragraph.getCTP().getPPr().getOutlineLvl().getVal().intValue();
            if (outlineLevel >= 0 && outlineLevel <= 8) {
                return outlineLevel + 1; // 大纲级别从0开始，标题从1开始
            }
        }

        // 方法2：通过样式名称判断
        if (style != null && !style.isEmpty()) {
            int level = getHeadingLevelFromStyle(style);
            if (level > 0) {
                return level;
            }
        }

        // 方法3：通过字体大小判断（作为备选方案）
        List<XWPFRun> runs = paragraph.getRuns();
        if (!runs.isEmpty()) {
            XWPFRun firstRun = runs.get(0);
            Double fontSizeDouble = firstRun.getFontSizeAsDouble();
            if (fontSizeDouble != null && fontSizeDouble > 0) {
                double fontSize = fontSizeDouble;
                // 根据字体大小推断标题级别（通常标题字体较大）
                if (fontSize >= 22)
                    return 1; // 一级标题通常 22pt 或更大
                if (fontSize >= 18)
                    return 2; // 二级标题通常 18pt
                if (fontSize >= 16)
                    return 3; // 三级标题通常 16pt
                if (fontSize >= 14 && firstRun.isBold())
                    return 4; // 四级标题可能是 14pt 加粗
            }
        }

        return 0; // 不是标题
    }

    /**
     * 从样式名称获取标题级别
     * 支持中英文样式名称
     */
    private int getHeadingLevelFromStyle(String style) {
        if (style == null || style.isEmpty()) {
            return 0;
        }

        String lowerStyle = style.toLowerCase();

        // 英文样式：Heading1, Heading2, heading 1, heading 2 等
        if (lowerStyle.contains("heading")) {
            // 提取数字
            for (char c : style.toCharArray()) {
                if (Character.isDigit(c)) {
                    int level = Character.getNumericValue(c);
                    if (level >= 1 && level <= 6) {
                        return level;
                    }
                }
            }
        }

        // 中文样式：标题1, 标题2, 标题 1, 标题 2 等
        if (style.contains("标题")) {
            for (char c : style.toCharArray()) {
                if (Character.isDigit(c)) {
                    int level = Character.getNumericValue(c);
                    if (level >= 1 && level <= 6) {
                        return level;
                    }
                }
            }
        }

        // 特殊样式：Title, Subtitle
        if (lowerStyle.equals("title")) {
            return 1;
        }
        if (lowerStyle.equals("subtitle")) {
            return 2;
        }

        // 直接是数字的样式名
        if (style.length() == 1 && Character.isDigit(style.charAt(0))) {
            int level = Character.getNumericValue(style.charAt(0));
            if (level >= 1 && level <= 6) {
                return level;
            }
        }

        return 0;
    }

    /**
     * 转义 Markdown 特殊字符
     */
    private String escapeMarkdown(String text) {
        // 只转义可能影响格式的字符
        return text.replace("\\", "\\\\")
                .replace("`", "\\`")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("<", "\\<")
                .replace(">", "\\>");
    }
}
