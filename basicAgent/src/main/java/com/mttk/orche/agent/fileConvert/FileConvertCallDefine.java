package com.mttk.orche.agent.fileConvert;

import com.mttk.orche.addon.annotation.ui.Control;

@Control(key = "inputFileName", label = "输入文件名", description = "待转换的输入文件名（支持格式：ppt/pptx/pdf/doc/docx/xls/xlsx）", size = 1, mandatory = true)
@Control(key = "outputFileName", label = "输出文件名", description = "转换后的输出文件名（可选，如不指定则自动生成）", size = 1, mandatory = false)
@Control(key = "_files", label = "文件", description = "相关文件.值为文件名,多个用逗号分割.", size = 1, mandatory = false, mode = "files")
public class FileConvertCallDefine {

}
