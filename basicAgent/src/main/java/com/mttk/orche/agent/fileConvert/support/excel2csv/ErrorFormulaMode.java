package com.mttk.orche.agent.fileConvert.support.excel2csv;

/**
 * 错误公式处理模式枚举
 */
public enum ErrorFormulaMode {
    /**
     * 模式1: 出错返回空字符串
     * 尝试评估公式，失败则设置为空字符串
     */
    NONE,

    /**
     * 模式2: 出错返回原始公式
     * 尝试评估公式，失败则设置为原始公式字符串
     */
    RAW
}
