package com.mttk.orche.agent.email;

import com.mttk.orche.addon.annotation.ui.Control;

@Control(key = "to", label = "收件邮箱地址", description = "多个用逗号分隔.如果没有使用配置里的邮件接收者", size = 1, mandatory = false)
@Control(key = "cc", label = "抄送邮箱地址", description = "多个用逗号分隔.如果没有使用配置里的邮件抄送者", size = 1, mandatory = false)
@Control(key = "subject", label = "主题", description = "邮件主题", size = 1, mandatory = true)
@Control(key = "content", label = "内容", description = "邮件内容", size = 1, mandatory = true, mode = "editor", props = {
                "language:markdown", "height:480" })
@Control(key = "_attchments", label = "附件", description = "邮件附件.值为文件名,多个用逗号分割.", size = 1, mandatory = false, mode = "files")
public class EmailSendCallDefine {

}
