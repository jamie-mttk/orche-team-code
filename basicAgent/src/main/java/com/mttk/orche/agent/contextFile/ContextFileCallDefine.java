package com.mttk.orche.agent.contextFile;

import com.mttk.orche.addon.annotation.ui.Control;

@Control(key = "operation", label = "操作类型", description = "文件操作类型:upload(上传)/download(下载)/list(列表)", mode = "select", size = 1, mandatory = true, props = "options:upload:上传,download:下载,list:列表")
@Control(key = "filename", label = "文件名", description = "文件名（上传和下载时必填）", size = 1, mandatory = false)
@Control(key = "content", label = "文件内容", description = "文件内容（上传时必填）", size = 1, mandatory = false)
@Control(key = "description", label = "文件描述", description = "文件描述（上传时可选）", size = 1, mandatory = false)
@Control(key = "_files", label = "文件", description = "相关文件.值为文件名,多个用逗号分割.", size = 1, mandatory = false, mode = "files")
public class ContextFileCallDefine {

}
