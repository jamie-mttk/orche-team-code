package com.mttk.orche.agent.fileConvert.support;

/**
 * 转换选项常量类
 * 定义各种转换器支持的选项参数
 */
public class ConvertOptions {

    // PPT 转 Markdown 选项
    /** PPT 是否包含演讲者备注 (Boolean) */
    public static final String PPT_INCLUDE_NOTES = "PPT_INCLUDE_NOTES";

    // PDF 转 Markdown 选项
    /** PDF 是否包含页码标题 (Boolean) */
    public static final String PDF_INCLUDE_PAGE_NUMBERS = "PDF_INCLUDE_PAGE_NUMBERS";

    // Excel 转 CSV 选项
    /** Excel 合并单元格处理模式 (MergedCellHandlingMode) */
    public static final String EXCEL_MERGED_CELL_MODE = "EXCEL_MERGED_CELL_MODE";

    /** Excel 是否跳过空行 (Boolean)，true-忽略空行，false-保留空行 */
    public static final String EXCEL_SKIP_EMPTY_ROWS = "EXCEL_SKIP_EMPTY_ROWS";

    /** Excel 错误公式处理模式 (String)，值为 "NONE"/"RAW"/"TRY_NONE"/"TRY_RAW"，缺省为 "NONE" */
    public static final String ERROR_FORMULA_MODE = "ERROR_FORMULA_MODE";

    /** Excel 日期格式 (String)，用于格式化日期类型的单元格，缺省为 "yyyy/MM/dd HH:mm:ss" */
    public static final String EXCEL_DATE_FORMAT = "EXCEL_DATE_FORMAT";

    private ConvertOptions() {
        // 工具类，禁止实例化
    }
}
