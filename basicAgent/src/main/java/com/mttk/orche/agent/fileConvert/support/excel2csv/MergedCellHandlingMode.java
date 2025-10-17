package com.mttk.orche.agent.fileConvert.support.excel2csv;

/**
 * 合并单元格处理模式枚举
 */
public enum MergedCellHandlingMode {
    /**
     * 只在左上角单元格导出值，其他位置为空（默认行为）
     */
    TOP_LEFT_ONLY,

    /**
     * 横向合并单元格时，所有横向单元格都导出相同的值
     */
    HORIZONTAL_FILL,

    /**
     * 纵向合并单元格时，所有纵向单元格都导出相同的值
     */
    VERTICAL_FILL,

    /**
     * 无论纵向还是横向，所有合并区域的单元格都导出相同的值
     */
    ALL_FILL
}
