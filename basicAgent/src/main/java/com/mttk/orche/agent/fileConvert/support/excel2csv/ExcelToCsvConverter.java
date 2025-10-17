package com.mttk.orche.agent.fileConvert.support.excel2csv;

import com.mttk.orche.agent.fileConvert.support.ConvertOptions;
import com.mttk.orche.agent.fileConvert.support.ConvertUtil;
import com.mttk.orche.agent.fileConvert.support.FileConverter;
import com.mttk.orche.agent.fileConvert.support.OutputCallback;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel 文档转 CSV 转换器
 * 支持 .xls 和 .xlsx 格式，支持公式计算和格式化显示
 * 
 * 注意：此类不支持并发调用，每次转换会修改实例状态
 */
public class ExcelToCsvConverter implements FileConverter {

    // 实例变量，不支持并发调用
    private Map<String, Object> options;
    private FormulaEvaluator evaluator;
    private DataFormatter formatter;
    private ErrorFormulaMode errorFormulaMode;
    private boolean skipEmptyRows;
    private String dateFormat;

    /**
     * 实现 FileConverter 接口的转换方法
     * 将 Excel 文件转换为 CSV 格式
     * 
     * 注意：此方法不支持并发调用
     * 
     * @param inputStream Excel 文件输入流，支持 .xls 和 .xlsx 格式
     * @param callback    输出回调接口，用于获取输出流和处理图片等资源
     * @param options     转换选项参数（可选），支持以下选项：
     * 
     *                    <ul>
     *                    <li><b>EXCEL_MERGED_CELL_MODE</b>
     *                    (MergedCellHandlingMode枚举):
     *                    合并单元格处理模式
     *                    <ul>
     *                    <li>TOP_LEFT_ONLY - 只保留左上角单元格的值（默认）</li>
     *                    <li>HORIZONTAL_FILL - 水平填充，同一行的合并单元格都填充值</li>
     *                    <li>VERTICAL_FILL - 垂直填充，同一列的合并单元格都填充值</li>
     *                    <li>ALL_FILL - 全部填充，合并区域内所有单元格都填充值</li>
     *                    </ul>
     *                    </li>
     * 
     *                    <li><b>EXCEL_SKIP_EMPTY_ROWS</b> (Boolean): 是否跳过空行
     *                    <ul>
     *                    <li>true - 忽略空行，不输出到 CSV</li>
     *                    <li>false - 保留空行（默认）</li>
     *                    </ul>
     *                    </li>
     * 
     *                    <li><b>ERROR_FORMULA_MODE</b> (String): 错误公式处理模式
     *                    <ul>
     *                    <li>"NONE" - 公式计算错误时返回空字符串（默认）</li>
     *                    <li>"RAW" - 公式计算错误时返回原始公式</li>
     *                    </ul>
     *                    </li>
     * 
     *                    <li><b>EXCEL_DATE_FORMAT</b> (String): 日期格式化模式
     *                    <ul>
     *                    <li>用于格式化日期类型的单元格</li>
     *                    <li>默认值："yyyy/MM/dd HH:mm:ss"</li>
     * 
     *                    </ul>
     *                    </li>
     *                    </ul>
     * 
     * @throws Exception 转换过程中的任何异常
     * 
     * @example 使用示例：
     * 
     *          <pre>
     *          Map&lt;String, Object&gt; options = new HashMap&lt;&gt;();
     *          options.put(ConvertOptions.EXCEL_MERGED_CELL_MODE, MergedCellHandlingMode.ALL_FILL);
     *          options.put(ConvertOptions.EXCEL_SKIP_EMPTY_ROWS, true);
     *          options.put(ConvertOptions.ERROR_FORMULA_MODE, "RAW");
     * 
     *          converter.convert(inputStream, callback, options);
     *          </pre>
     */
    @Override
    public void convert(InputStream inputStream, OutputCallback callback,
            Map<String, Object> options) throws Exception {

        // 初始化实例变量
        this.options = options;

        // 解析错误公式处理模式
        String modeStr = ConvertUtil.getOptionValue(options, ConvertOptions.ERROR_FORMULA_MODE, "NONE");
        try {
            this.errorFormulaMode = ErrorFormulaMode.valueOf(modeStr);
        } catch (IllegalArgumentException e) {
            // 如果传入的值无效，使用默认值 NONE
            this.errorFormulaMode = ErrorFormulaMode.NONE;
        }

        // 解析是否跳过空行选项
        this.skipEmptyRows = ConvertUtil.getOptionValue(options, ConvertOptions.EXCEL_SKIP_EMPTY_ROWS, false);

        // 解析日期格式选项
        this.dateFormat = ConvertUtil.getOptionValue(options, ConvertOptions.EXCEL_DATE_FORMAT, "yyyy/MM/dd HH:mm:ss");

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            convertInternal(workbook, callback);
        }
    }

    /**
     * 内部转换方法
     */
    private void convertInternal(Workbook workbook, OutputCallback callback) throws Exception {

        // 创建公式计算器和数据格式化器
        this.evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        // 设置公式评估选项，忽略缺失的工作簿
        this.evaluator.setIgnoreMissingWorkbooks(true);

        this.formatter = new DataFormatter();

        int numberOfSheets = workbook.getNumberOfSheets();
        // 遍历所有 Sheet
        for (int i = 0; i < numberOfSheets; i++) {

            Sheet sheet = workbook.getSheetAt(i);
            // 检查 sheet 是否为空，如果为空则跳过
            if (isSheetEmpty(sheet)) {
                continue;
            }

            // 通过回调获取输出流（传递 sheet 对象）
            OutputStream outputStream = callback.getOutputStream(sheet);
            if (outputStream == null) {
                // 认为不希望保存
                continue;
            }

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"))) {
                processSheet(sheet, writer);
                writer.flush();
            }
        }
    }

    /**
     * 处理单个 Sheet
     */
    private void processSheet(Sheet sheet, BufferedWriter writer) throws Exception {

        // 构建合并单元格映射
        Map<String, String> mergedCellValues = buildMergedCellMap(sheet);

        // 遍历所有行 (修复: 应该使用 <= 以包含最后一行)
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row)) {
                if (!skipEmptyRows) {
                    writer.newLine();
                }
                continue;
            }

            // 获取行中最后一个单元格的列号
            short lastCellNum = row.getLastCellNum();

            // 遍历该行的所有单元格
            for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                Cell cell = row.getCell(cellIndex);

                // 获取单元格值
                String cellValue;
                String cellKey = row.getRowNum() + "," + cellIndex;

                // 检查是否是合并单元格的一部分
                if (mergedCellValues.containsKey(cellKey)) {
                    cellValue = mergedCellValues.get(cellKey);
                } else {
                    cellValue = getCellValue(cell);
                }

                // 转义并写入 CSV
                if (cellIndex > 0) {
                    writer.write(",");
                }
                writer.write(escapeCsvField(cellValue));
            }

            writer.newLine();
        }
    }

    /**
     * 构建合并单元格的值映射
     * 根据处理模式，为合并区域内的单元格填充值
     */
    private Map<String, String> buildMergedCellMap(Sheet sheet) throws Exception {
        Map<String, String> mergedCellValues = new HashMap<>();

        // 获取选项值
        MergedCellHandlingMode mode = MergedCellHandlingMode.TOP_LEFT_ONLY;
        if (options != null && options.containsKey(ConvertOptions.EXCEL_MERGED_CELL_MODE)) {
            mode = (MergedCellHandlingMode) options.get(ConvertOptions.EXCEL_MERGED_CELL_MODE);
        }

        if (mode == MergedCellHandlingMode.TOP_LEFT_ONLY) {
            // 默认模式，不需要填充
            return mergedCellValues;
        }

        // 获取所有合并区域
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();

        for (CellRangeAddress region : mergedRegions) {
            int firstRow = region.getFirstRow();
            int lastRow = region.getLastRow();
            int firstCol = region.getFirstColumn();
            int lastCol = region.getLastColumn();

            // 获取左上角单元格的值
            Row topRow = sheet.getRow(firstRow);
            if (topRow == null) {
                continue;
            }

            Cell topLeftCell = topRow.getCell(firstCol);
            String mergedValue = getCellValue(topLeftCell);

            // 根据模式填充合并区域
            for (int rowIdx = firstRow; rowIdx <= lastRow; rowIdx++) {
                for (int colIdx = firstCol; colIdx <= lastCol; colIdx++) {
                    // 跳过左上角单元格（已经有值）
                    if (rowIdx == firstRow && colIdx == firstCol) {
                        continue;
                    }

                    boolean shouldFill = false;

                    switch (mode) {
                        case HORIZONTAL_FILL:
                            // 只填充同一行的单元格
                            shouldFill = (rowIdx == firstRow);
                            break;

                        case VERTICAL_FILL:
                            // 只填充同一列的单元格
                            shouldFill = (colIdx == firstCol);
                            break;

                        case ALL_FILL:
                            // 填充所有单元格
                            shouldFill = true;
                            break;

                        default:
                            break;
                    }

                    if (shouldFill) {
                        String cellKey = rowIdx + "," + colIdx;
                        mergedCellValues.put(cellKey, mergedValue);
                    }
                }
            }
        }

        return mergedCellValues;
    }

    /**
     * 获取单元格的值（支持公式计算和格式化）
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            // 检查是否是日期格式的单元格（仅对NUMERIC类型检查，避免对STRING等类型误判）
            // 修复：DateUtil.isCellDateFormatted对某些单元格可能抛异常，导致STRING类型的表头返回空值
            CellType cellType = cell.getCellType();
            if (cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                // 使用自定义日期格式
                try {
                    Date date = cell.getDateCellValue();
                    if (date != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                        return sdf.format(date);
                    }
                } catch (Exception e) {
                    // 日期格式化失败，继续使用默认格式化
                }
            }

            // 使用 DataFormatter 配合 FormulaEvaluator 来获取格式化后的显示值
            // 这会自动处理所有类型（包括公式），并返回格式化后的文本
            String value = formatter.formatCellValue(cell, evaluator);
            return value != null ? value : "";

        } catch (Exception e) {
            // 公式计算错误处理
            try {
                if (cell.getCellType() == CellType.FORMULA) {
                    // 根据 ERROR_FORMULA_MODE 处理
                    if (errorFormulaMode == ErrorFormulaMode.RAW) {
                        // RAW 模式：返回原始公式
                        return "=" + cell.getCellFormula();
                    } else {
                        // NONE 模式（默认）：返回空字符串
                        return "";
                    }
                }
            } catch (Exception ex) {
                // 获取公式失败，返回空字符串
            }
            return "";
        }
    }

    /**
     * 转义 CSV 字段
     * 如果字段包含逗号、双引号或换行符，需要用双引号包裹，并将双引号转义为两个双引号
     */
    private String escapeCsvField(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        // 检查是否需要转义
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            // 将双引号转义为两个双引号
            String escaped = value.replace("\"", "\"\"");
            // 用双引号包裹整个字段
            return "\"" + escaped + "\"";
        }

        return value;
    }

    /**
     * 检查 sheet 是否为空
     * 如果 sheet 中没有任何非空行，则认为是空的
     */
    private boolean isSheetEmpty(Sheet sheet) {
        if (sheet == null) {
            return true;
        }

        // 检查是否有任何非空行
        for (Row row : sheet) {
            if (row != null && !isRowEmpty(row)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查行是否完全为空
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String cellValue = cell.toString();
                if (cellValue != null && !cellValue.trim().isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }
}
