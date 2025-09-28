package com.mttk.orche.agentTemplate;

import com.mttk.orche.addon.annotation.ui.Control;

@Control(key = "query", label = "任务", description = "提交给智能体的任务说明", size = 1, mandatory = true, mode = "editor", props = {
                "language:markdown", "height:480" })
@Control(key = "_files", label = "文件", description = "相关文件.值为文件名,多个用逗号分割.", size = 1, mandatory = false, mode = "files")
public class DefaultCallDefine {

}
