package com.mttk.orche.agent.javaCode;

import com.mttk.orche.addon.annotation.ui.Control;

@Control(key = "task", label = "任务描述", description = "生成代码任务的.注意:此任务描述将会被直接用于生成代码,请务给出具体要求和必详细描述.", size = 1, mandatory = true)
@Control(key = "inputFileNames", label = "输入文件列表", description = "为了代码需要的或代码需要读取的输入文件名称,多个文件名称之间使用逗号分隔.注意仅仅包含此工具需要的文件名,严格禁止包含与本此调用无关的或重复内容的文件名.如果任务描述需要输入文件,则必须提供.", size = 1, mandatory = false)

public class JavaCodelCallDefine {

}
