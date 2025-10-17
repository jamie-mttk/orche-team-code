package com.mttk.orche.agent.test;

import com.mttk.orche.agent.fileConvert.support.ConvertOptions;
import com.mttk.orche.agent.fileConvert.support.OutputCallback;
import com.mttk.orche.agent.fileConvert.support.excel2csv.ExcelToCsvConverter;
import com.mttk.orche.agent.fileConvert.support.excel2csv.MergedCellHandlingMode;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Excel转CSV测试类
 * 用于诊断表头为什么变成空格的问题
 */
public class ExcelToCsvTest {

    public static void main(String[] args) {
        String inputPath = "D:\\temp\\a\\2\\水科院数据.xlsx";
        String outputPath = "D:\\temp\\a\\2\\out.csv";

        System.out.println("===== Excel转CSV测试开始 =====");
        System.out.println("输入文件: " + inputPath);
        System.out.println("输出文件: " + outputPath);
        System.out.println();

        // 首先直接读取Excel文件查看表头内容
        analyzeExcelHeader(inputPath);

        System.out.println("\n===== 开始转换 =====");

        // 执行转换
        performConversion(inputPath, outputPath);

        System.out.println("\n===== 转换完成，查看输出文件 =====");

        // 读取输出的CSV查看表头
        readCsvHeader(outputPath);

        System.out.println("\n===== 测试结束 =====");
    }

    /**
     * 分析Excel文件的表头信息
     */
    private static void analyzeExcelHeader(String excelPath) {
        System.out.println("===== 分析Excel原始表头 =====");
        try (InputStream is = new FileInputStream(excelPath);
                Workbook workbook = WorkbookFactory.create(is)) {

            int numberOfSheets = workbook.getNumberOfSheets();
            System.out.println("工作表数量: " + numberOfSheets);

            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                System.out.println("\n工作表 #" + i + ": " + sheet.getSheetName());
                System.out.println("总行数: " + sheet.getLastRowNum());

                // 分析表头（第一行）
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    System.out.println("警告: 表头行(第0行)为null!");
                    continue;
                }

                System.out.println("表头行号: " + headerRow.getRowNum());
                System.out.println("第一个单元格列号: " + headerRow.getFirstCellNum());
                System.out.println("最后一个单元格列号: " + headerRow.getLastCellNum());

                System.out.println("\n表头单元格详细信息:");
                for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                    Cell cell = headerRow.getCell(cellIndex);

                    System.out.print("列" + cellIndex + ": ");

                    if (cell == null) {
                        System.out.println("[null单元格]");
                        continue;
                    }

                    CellType cellType = cell.getCellType();
                    System.out.print("类型=" + cellType + ", ");

                    String value = getCellValueAsString(cell);
                    System.out.print("值=\"" + value + "\"");

                    // 显示字符串长度和每个字符的Unicode码
                    if (value != null && !value.isEmpty()) {
                        System.out.print(", 长度=" + value.length());
                        System.out.print(", 字符码点=[");
                        for (int j = 0; j < value.length(); j++) {
                            if (j > 0)
                                System.out.print(", ");
                            char ch = value.charAt(j);
                            System.out.print("'" + ch + "'(" + (int) ch + ")");
                        }
                        System.out.print("]");

                        // 检查是否全是空格
                        if (value.trim().isEmpty()) {
                            System.out.print(" [警告: 全是空白字符!]");
                        }
                    }

                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.err.println("分析Excel文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行转换
     */
    private static void performConversion(String inputPath, String outputPath) {
        try {
            System.out.println("\n【调试】开始分析循环条件问题...");

            // 先读取Excel查看行数
            try (FileInputStream fis = new FileInputStream(inputPath);
                    Workbook workbook = WorkbookFactory.create(fis)) {
                Sheet sheet = workbook.getSheetAt(0);

                int lastRowNum = sheet.getLastRowNum();
                int firstRowNum = sheet.getFirstRowNum();
                int physicalRows = sheet.getPhysicalNumberOfRows();

                System.out.println("sheet.getFirstRowNum() = " + firstRowNum);
                System.out.println("sheet.getLastRowNum() = " + lastRowNum);
                System.out.println("sheet.getPhysicalNumberOfRows() = " + physicalRows);
                System.out.println();
                System.out.println("❌ 当前代码: for (int i = 0; i < " + lastRowNum + "; i++)");
                System.out.println("   → 会处理行: 0 到 " + (lastRowNum - 1) + "，共 " + lastRowNum + " 行");
                System.out.println("   → 问题：少处理了第 " + lastRowNum + " 行！");
                System.out.println();
                System.out.println("✅ 正确代码: for (int i = 0; i <= " + lastRowNum + "; i++)");
                System.out.println("   → 会处理行: 0 到 " + lastRowNum + "，共 " + (lastRowNum + 1) + " 行");
                System.out.println();

                // 检查为什么表头变空
                System.out.println("【调试】检查第0行在循环中的处理...");
                Row row0 = sheet.getRow(0);
                if (row0 != null) {
                    System.out.println("第0行存在，共 " + row0.getLastCellNum() + " 个单元格");
                    System.out.println("第0行会在循环中被处理（i=0）");
                }
            }

            System.out.println("\n【调试】执行原始转换器（有BUG的版本）...");

            // 创建转换器
            ExcelToCsvConverter converter = new ExcelToCsvConverter();

            // 设置选项
            Map<String, Object> options = new HashMap<>();
            options.put(ConvertOptions.EXCEL_MERGED_CELL_MODE, MergedCellHandlingMode.ALL_FILL);
            options.put(ConvertOptions.EXCEL_SKIP_EMPTY_ROWS, false);
            options.put(ConvertOptions.ERROR_FORMULA_MODE, "RAW");
            options.put(ConvertOptions.EXCEL_DATE_FORMAT, "yyyy/MM/dd HH:mm:ss");

            // 创建输出回调
            OutputCallback callback = new OutputCallback() {
                @Override
                public OutputStream getOutputStream(Object indicator) throws Exception {
                    // indicator 是 Sheet 对象
                    return new FileOutputStream(outputPath);
                }
            };

            // 执行转换
            try (FileInputStream fis = new FileInputStream(inputPath)) {
                converter.convert(fis, callback, options);
            }

            System.out.println("转换成功!");

        } catch (Exception e) {
            System.err.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 读取CSV文件的表头
     */
    private static void readCsvHeader(String csvPath) {
        System.out.println("CSV文件表头内容:");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvPath), "UTF-8"))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.out.println("CSV文件为空!");
                return;
            }

            System.out.println("表头行: \"" + headerLine + "\"");
            System.out.println("表头长度: " + headerLine.length());

            // 解析CSV字段
            String[] fields = parseCsvLine(headerLine);
            System.out.println("字段数量: " + fields.length);

            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                System.out.print("字段" + i + ": \"" + field + "\"");

                if (field != null && !field.isEmpty()) {
                    System.out.print(", 长度=" + field.length());

                    // 检查是否全是空格
                    if (field.trim().isEmpty()) {
                        System.out.print(" [警告: 全是空白字符!]");
                        // 显示字符码点
                        System.out.print(", 字符码点=[");
                        for (int j = 0; j < field.length(); j++) {
                            if (j > 0)
                                System.out.print(", ");
                            char ch = field.charAt(j);
                            System.out.print("'" + ch + "'(" + (int) ch + ")");
                        }
                        System.out.print("]");
                    }
                }

                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("读取CSV文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取单元格值作为字符串
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            CellType cellType = cell.getCellType();

            switch (cellType) {
                case STRING:
                    return cell.getStringCellValue();

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    }
                    return String.valueOf(cell.getNumericCellValue());

                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());

                case FORMULA:
                    // 对于公式，尝试获取计算后的值
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception e) {
                        return "=" + cell.getCellFormula();
                    }

                case BLANK:
                    return "";

                case ERROR:
                    return "ERROR:" + cell.getErrorCellValue();

                default:
                    return cell.toString();
            }
        } catch (Exception e) {
            return "[Error: " + e.getMessage() + "]";
        }
    }

    /**
     * 简单的CSV行解析器（支持引号包裹的字段）
     */
    private static String[] parseCsvLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // 转义的引号
                    currentField.append('"');
                    i++;
                } else {
                    // 切换引号状态
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // 字段分隔符
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // 添加最后一个字段
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }
}
