package com.mttk.orche.agent.fileConvert.support.ppt2md;

import com.mttk.orche.agent.fileConvert.support.ConvertOptions;
import com.mttk.orche.agent.fileConvert.support.ConvertUtil;
import com.mttk.orche.agent.fileConvert.support.FileConverter;
import com.mttk.orche.agent.fileConvert.support.OutputCallback;
import org.apache.poi.xslf.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * PPT 文档转 Markdown 转换器
 */
public class PptToMarkdownConverter implements FileConverter {

    private OutputCallback callback;

    /**
     * 实现 FileConverter 接口的转换方法
     */
    @Override
    public void convert(InputStream inputStream, OutputCallback callback,
            Map<String, Object> options) throws Exception {
        this.callback = callback;

        OutputStream output = callback.getOutputStream(null);
        convertInternal(inputStream, output, options);
    }

    /**
     * 内部转换方法
     */
    private void convertInternal(InputStream pptInput, OutputStream markdownOutput,
            Map<String, Object> options) throws Exception {

        try (XMLSlideShow ppt = new XMLSlideShow(pptInput);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(markdownOutput, "UTF-8"))) {

            // 获取选项值
            boolean includeNotes = ConvertUtil.getOptionValue(options, ConvertOptions.PPT_INCLUDE_NOTES, false);

            List<XSLFSlide> slides = ppt.getSlides();
            int slideNumber = 1;

            for (XSLFSlide slide : slides) {
                processSlide(slide, writer, slideNumber, includeNotes);
                slideNumber++;
            }

            writer.flush();
        } catch (Exception e) {
            throw new IOException("PPT 文档转换失败", e);
        }
    }

    /**
     * 处理单张幻灯片
     */
    private void processSlide(XSLFSlide slide, BufferedWriter writer, int slideNumber, boolean includeNotes)
            throws IOException {
        // 输出幻灯片标题
        String title = slide.getTitle();
        if (title != null && !title.trim().isEmpty()) {
            writer.write("# 幻灯片 " + slideNumber + ": " + title.trim());
        } else {
            writer.write("# 幻灯片 " + slideNumber);
        }
        writer.newLine();
        writer.newLine();

        // 处理幻灯片中的所有形状
        List<XSLFShape> shapes = slide.getShapes();
        for (XSLFShape shape : shapes) {
            processShape(shape, writer);
        }

        // 处理演讲者备注
        if (includeNotes) {
            XSLFNotes notes = slide.getNotes();
            if (notes != null) {
                String notesText = extractNotesText(notes);
                if (notesText != null && !notesText.trim().isEmpty()) {
                    writer.write("> **备注：** " + notesText.trim());
                    writer.newLine();
                    writer.newLine();
                }
            }
        }

        writer.newLine();
    }

    /**
     * 处理形状
     */
    private void processShape(XSLFShape shape, BufferedWriter writer) throws IOException {
        if (shape instanceof XSLFTextShape && !(shape instanceof XSLFTable)) {
            // 处理文本形状（排除表格，因为表格也继承自 TextShape）
            XSLFTextShape textShape = (XSLFTextShape) shape;
            // 跳过标题框（已经在幻灯片标题中处理）
            if (!isTitle(textShape)) {
                processTextShape(textShape, writer);
            }
        } else if (shape instanceof XSLFPictureShape) {
            // 处理图片
            String imageMarkdown = handleImageViaCallback((XSLFPictureShape) shape);
            if (imageMarkdown != null && !imageMarkdown.isEmpty()) {
                writer.write(imageMarkdown);
                writer.newLine();
                writer.newLine();
            }
        } else if (shape instanceof XSLFTable) {
            // 处理表格
            processTable((XSLFTable) shape, writer);
        } else if (shape instanceof XSLFGroupShape) {
            // 处理组合形状，递归处理其中的子形状
            XSLFGroupShape groupShape = (XSLFGroupShape) shape;
            for (XSLFShape childShape : groupShape.getShapes()) {
                processShape(childShape, writer);
            }
        }
    }

    /**
     * 判断文本形状是否为标题
     */
    private boolean isTitle(XSLFTextShape textShape) {
        try {
            return textShape.getPlaceholder() != null &&
                    textShape.getPlaceholder().toString().contains("TITLE");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 处理文本形状
     */
    private void processTextShape(XSLFTextShape textShape, BufferedWriter writer) throws IOException {
        List<XSLFTextParagraph> paragraphs = textShape.getTextParagraphs();

        for (XSLFTextParagraph paragraph : paragraphs) {
            String text = paragraph.getText();
            if (text == null || text.trim().isEmpty()) {
                continue;
            }

            // 检查是否为列表项
            Integer indentLevel = paragraph.getIndentLevel();
            boolean isBullet = paragraph.isBullet();

            if (isBullet && indentLevel != null) {
                // 输出列表项
                String indent = "  ".repeat(indentLevel);
                writer.write(indent + "- ");
            }

            // 处理段落中的文本运行（runs）
            List<XSLFTextRun> runs = paragraph.getTextRuns();
            for (XSLFTextRun run : runs) {
                String runText = run.getRawText();
                if (runText == null || runText.isEmpty()) {
                    continue;
                }

                // 转义 Markdown 特殊字符
                runText = escapeMarkdown(runText);

                // 应用格式
                boolean isBold = run.isBold();
                boolean isItalic = run.isItalic();
                boolean isStrikethrough = run.isStrikethrough();

                if (isBold && isItalic) {
                    writer.write("***" + runText + "***");
                } else if (isBold) {
                    writer.write("**" + runText + "**");
                } else if (isItalic) {
                    writer.write("*" + runText + "*");
                } else if (isStrikethrough) {
                    writer.write("~~" + runText + "~~");
                } else {
                    writer.write(runText);
                }
            }

            writer.newLine();
        }

        writer.newLine();
    }

    /**
     * 通过回调处理图片
     */
    private String handleImageViaCallback(XSLFPictureShape picture) {
        try {
            XSLFPictureData pictureData = picture.getPictureData();
            byte[] imageData = pictureData.getData();
            return callback.handleImage(null, imageData);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 处理表格
     */
    private void processTable(XSLFTable table, BufferedWriter writer) throws IOException {
        List<XSLFTableRow> rows = table.getRows();
        if (rows.isEmpty()) {
            return;
        }

        // 处理表头
        XSLFTableRow headerRow = rows.get(0);
        List<XSLFTableCell> headerCells = headerRow.getCells();

        writer.write("|");
        for (XSLFTableCell cell : headerCells) {
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
            XSLFTableRow row = rows.get(i);
            List<XSLFTableCell> cells = row.getCells();

            writer.write("|");
            for (XSLFTableCell cell : cells) {
                String cellText = cell.getText().replace("\n", " ").replace("|", "\\|");
                writer.write(" " + cellText + " |");
            }
            writer.newLine();
        }

        writer.newLine();
    }

    /**
     * 提取演讲者备注文本
     */
    private String extractNotesText(XSLFNotes notes) {
        StringBuilder notesText = new StringBuilder();

        for (XSLFShape shape : notes.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                XSLFTextShape textShape = (XSLFTextShape) shape;
                String text = textShape.getText();
                if (text != null && !text.trim().isEmpty()) {
                    // 跳过默认的占位符文本
                    if (!text.contains("Click to edit") && !text.contains("点击编辑")) {
                        if (notesText.length() > 0) {
                            notesText.append(" ");
                        }
                        notesText.append(text.trim());
                    }
                }
            }
        }

        return notesText.toString();
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
