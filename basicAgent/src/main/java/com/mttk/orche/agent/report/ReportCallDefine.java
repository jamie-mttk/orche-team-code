package com.mttk.orche.agent.report;

import com.mttk.orche.addon.annotation.ui.Control;

@Control(key = "task", label = "任务描述", description = "生成文件任务的具体要求及详细描述,以及需要在报告中体现的内容,例如,上下文中需要输出的数据细节", size = 1, mandatory = true)
@Control(key = "fileType", label = "文件类型", description = "仅支持 markdown ppt 和 html 三种类型,如果指定了输出 html 或 网页版 格式,则是html,如果指定了输出 ppt、pptx 则是 ppt,否则使用 markdown .", size = 1, mandatory = true, mode = "select", props = "options:markdown,ppt,html")
@Control(key = "inputFileNames", label = "输入文件列表", description = "为了生成此报告需要的输入文件名称,多个文件名称之间使用逗号分隔.注意仅仅包含生成此报告需要的文件名,严格禁止包含与生成本报告无关的或重复内容的文件名", size = 1, mandatory = false)
@Control(key = "fileDescription", label = "文件描述", description = "生成报告的文件描述,一定是跟用户的任务强相关的", size = 1, mandatory = true)
@Control(key = "outputFileName", label = "输出文件名称", description = "生成的文件名称,文件的前缀中文名称一定是跟用户的任务和文件内容强相关,如果是markdown,则文件名称后缀是 .md,如果是ppt、html文件,则是文件名称后缀是 .html,一定要包含文件后缀.文件名称不能使用特殊符号,不能使用、,？等符号,如果需要,可以使用下划线_.", size = 1, mandatory = true)

public class ReportCallDefine {

}
