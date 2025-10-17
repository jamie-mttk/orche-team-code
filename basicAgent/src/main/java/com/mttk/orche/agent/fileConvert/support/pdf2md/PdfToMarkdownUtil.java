package com.mttk.orche.agent.fileConvert.support.pdf2md;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF 转 Markdown 工具类
 * 包含文本提取、表格转换、位置计算等工具方法
 */
public class PdfToMarkdownUtil {

    /**
     * 计算文字与单个表格的位置关系（核心方法）
     * 
     * 返回值采用九宫格位置编码（0-8）：
     * 1 | 2 | 3 左上 | 正上 | 右上
     * ---+---+--- -----+------+-----
     * 4 | 0 | 5 正左 | 内部 | 正右
     * ---+---+--- -----+------+-----
     * 6 | 7 | 8 左下 | 正下 | 右下
     */
    public static int calculateRelation(TextParagraph para, Table table) {
        float textX = para.getX();
        float textY = para.getY();
        float tableX = (float) table.getLeft();
        float tableY = (float) table.getTop();
        float tableWidth = (float) table.getWidth();
        float tableHeight = (float) table.getHeight();

        // 添加边距容差，避免边界文本被误判为在表格内
        final float MARGIN = 0.0f;

        // Y方向判断（更严格）
        int yRelation;
        if (textY < tableY - MARGIN) {
            yRelation = 0; // 上方 (1,2,3)
        } else if (textY > tableY + tableHeight + MARGIN) {
            yRelation = 2; // 下方 (6,7,8)
        } else {
            yRelation = 1; // 中间 (4,5) 或内部 (0)
        }

        // X方向判断（更严格）
        int xRelation;
        if (textX < tableX - MARGIN) {
            xRelation = 0; // 左边 (1,4,6)
        } else if (textX > tableX + tableWidth + MARGIN) {
            xRelation = 2; // 右边 (3,5,8)
        } else {
            xRelation = 1; // 中间 (2,7) 或内部 (0)
        }

        // 组合判断：只有当文本明确在表格内部区域（考虑边距）时才返回0
        if (yRelation == 1 && xRelation == 1) {
            // 进一步检查：文本必须在表格内部区域内（带边距）
            boolean inTableX = textX >= tableX + MARGIN && textX <= tableX + tableWidth - MARGIN;
            boolean inTableY = textY >= tableY + MARGIN && textY <= tableY + tableHeight - MARGIN;

            if (inTableX && inTableY) {
                return 0; // 确实在表格内
            }

            // 否则根据更细致的位置判断
            if (textY < tableY + MARGIN) {
                yRelation = 0; // 实际在上方
            } else if (textY > tableY + tableHeight - MARGIN) {
                yRelation = 2; // 实际在下方
            }
        }

        return yRelation * 3 + xRelation + 1; // 映射到 1-8
    }

    /**
     * 将 Tabula Table 转换为 Markdown 表格格式
     * 直接输出原始表格，不处理多级表头
     */
    @SuppressWarnings("rawtypes")
    public static String convertTableToMarkdown(Table table) {
        if (table == null || table.getRowCount() == 0) {
            return null;
        }

        StringBuilder markdown = new StringBuilder();
        List<List<RectangularTextContainer>> rows = table.getRows();

        // 直接输出所有行，不进行表头检测和合并
        for (int i = 0; i < rows.size(); i++) {
            List<RectangularTextContainer> row = rows.get(i);
            markdown.append("|");
            for (RectangularTextContainer cell : row) {
                String cellText = cleanCellText(cell.getText());
                markdown.append(" ").append(cellText).append(" |");
            }
            markdown.append("\n");

            // 在第一行后添加分隔线
            if (i == 0) {
                markdown.append("|");
                for (int j = 0; j < row.size(); j++) {
                    markdown.append(" --- |");
                }
                markdown.append("\n");
            }
        }

        return markdown.toString();
    }

    /**
     * 清理单元格文本，使其适合 Markdown 表格
     */
    public static String cleanCellText(String text) {
        if (text == null) {
            return "";
        }

        // 1. 去除首尾空白
        text = text.trim();

        // 2. 替换所有类型的换行符为空格
        text = text.replace("\r\n", " ") // Windows 换行
                .replace("\n", " ") // Unix/Linux 换行
                .replace("\r", " "); // Mac 旧式换行

        // 3. 转义 Markdown 表格中的特殊字符 |
        text = text.replace("|", "\\|");

        // 4. 压缩多个连续空格为单个空格
        text = text.replaceAll("\\s+", " ");

        // 5. 再次去除首尾空白（处理后可能产生的）
        text = text.trim();

        return text;
    }

    /**
     * 获取图片格式
     */
    public static String getImageFormat(String suffix) {
        if (suffix != null && !suffix.isEmpty()) {
            // 常见格式：jpg, png, tiff, bmp
            if (suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("jpeg")) {
                return "jpg";
            } else if (suffix.equalsIgnoreCase("png")) {
                return "png";
            }
        }

        // 默认使用 PNG 格式
        return "png";
    }

    /**
     * 提取页面段落（必须在提取表格之前调用）
     */
    static List<TextParagraph> extractParagraphs(PDDocument document, int pageIndex) throws IOException {
        List<TextParagraph> paragraphs = new ArrayList<>();

        // 使用 writeString 方法来收集 TextPosition
        // 关键：必须在提取表格之前调用，否则会失败
        List<TextPosition> allPositions = new ArrayList<>();

        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                // 从 writeString 收集 TextPosition
                if (textPositions != null && !textPositions.isEmpty()) {
                    allPositions.addAll(textPositions);
                }

                super.writeString(text, textPositions);
            }
        };

        stripper.setStartPage(pageIndex + 1);
        stripper.setEndPage(pageIndex + 1);
        stripper.setSortByPosition(true);

        stripper.getText(document);

        // 按行分组 TextPosition，然后合并成段落
        if (!allPositions.isEmpty()) {
            List<TextPosition> currentLine = new ArrayList<>();
            List<List<TextPosition>> allLines = new ArrayList<>(); // 保存所有行的 TextPosition
            float lastY = allPositions.get(0).getYDirAdj();
            final float LINE_THRESHOLD = 2.0f; // Y坐标变化阈值

            for (TextPosition pos : allPositions) {
                float currentY = pos.getYDirAdj();

                // 如果Y坐标变化超过阈值，说明是新行
                if (Math.abs(currentY - lastY) > LINE_THRESHOLD) {
                    // 保存当前行
                    if (!currentLine.isEmpty()) {
                        allLines.add(new ArrayList<>(currentLine));
                        currentLine.clear();
                    }
                    lastY = currentY;
                }

                currentLine.add(pos);
            }

            // 添加最后一行
            if (!currentLine.isEmpty()) {
                allLines.add(new ArrayList<>(currentLine));
            }

            // 将每行转换为 TextParagraph，使用该行实际的位置信息
            for (List<TextPosition> line : allLines) {
                if (!line.isEmpty()) {
                    String lineText = buildLineText(line);
                    if (lineText.length() > 0) {
                        // 使用该行第一个字符的实际位置
                        float lineX = line.get(0).getXDirAdj();
                        float lineY = line.get(0).getYDirAdj();
                        paragraphs.add(new TextParagraph(lineText, lineX, lineY));
                    }
                }
            }
        }

        return paragraphs;
    }

    /**
     * 根据 TextPosition 列表构建文本行，考虑字符间距自动添加空格
     */
    private static String buildLineText(List<TextPosition> positions) {
        if (positions.isEmpty()) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        TextPosition lastPos = null;

        for (TextPosition pos : positions) {
            if (lastPos != null) {
                // 计算当前字符与上一个字符之间的间距
                float lastEndX = lastPos.getXDirAdj() + lastPos.getWidthDirAdj();
                float currentStartX = pos.getXDirAdj();
                float gap = currentStartX - lastEndX;

                // 如果间距大于字符宽度的40%，认为需要添加空格
                float spaceThreshold = lastPos.getWidthDirAdj() * 0.4f;
                if (gap > spaceThreshold) {
                    // 根据间距大小决定添加几个空格
                    int spaceCount = Math.max(1, (int) (gap / lastPos.getWidthDirAdj()));
                    for (int i = 0; i < spaceCount && i < 3; i++) { // 最多添加3个空格
                        text.append(" ");
                    }
                }
            }

            text.append(pos.getUnicode());
            lastPos = pos;
        }

        return text.toString().trim();
    }
}
