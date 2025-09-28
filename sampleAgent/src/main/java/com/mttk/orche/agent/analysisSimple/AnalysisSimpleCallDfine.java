package com.mttk.orche.agent.analysisSimple;

import com.mttk.orche.addon.annotation.ui.Control;

@Control(key = "inputFile", label = "输入数据文件名", description = "输入数据文件名.CSV格式.", size = 1, mandatory = true)
@Control(key = "outputFile", label = "输出数据文件名", description = "输出数据文件名.CSV格式.", size = 1, mandatory = true)
@Control(key = "independentVars", label = "自变量列表", description = "多项式回归的自变量列表,多个使用逗号分割.严格要求名称存在于输入数据文件的CSV表头中.", size = 1, mandatory = true)
@Control(key = "dependentVar", label = "因变量", description = "多项式回归的因变量.严格要求名称存在于输入数据文件的CSV表头中.", size = 1, mandatory = false)

public class AnalysisSimpleCallDfine {

}
