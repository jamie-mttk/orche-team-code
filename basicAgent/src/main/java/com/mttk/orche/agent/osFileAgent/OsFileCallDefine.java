package com.mttk.orche.agent.osFileAgent;

import com.mttk.orche.addon.annotation.ui.Control;

// @Control(key = "operation", label = "操作类型", mode = "select", size = 1, mandatory = true, props = "options:list:列表,read:读取,write:写入,touch:读取部分")

// // 
// @Control(key = "pathList", label = "列表目录", description = "操作类型为列表时有意义\n给出希望列表的目录\n如果没有设置,则列出根目录下所有文件", size = 1, bindings = "show:this.data.operation=='list'")
// @Control(key = "includeSubPath", label = "列表包含子目录", description = "操作类型为列表时有意义\ntrue代表列出所有子目录下的文件,否则只列表列表目录下文件.\n缺省为false", mode = "checkbox", defaultVal = "false", size = 1, bindings = "show:this.data.operation=='list'")
// @Control(key = "fileRead", label = "读取文件名", description = "操作类型为读取或部分读取时有意义\n给出要读取的文件名,可包含路径", size = 1, mandatory = true, bindings = "show:this.data.operation=='read'||this.data.operation=='touch'")
// @Control(key = "fileWrite", label = "写入文件名", description = "操作类型为读写入时有意义\n给出要写入的文件名,可包含路径", size = 1, mandatory = true, bindings = "show:this.data.operation=='write'")
// @Control(key = "fileContent", label = "写入内容", description = "操作类型为读写入时有意义\n给出要写入的文件的内容,字符串或byte[]", size = 1, mandatory = true, bindings = "show:this.data.operation=='write'")

@Control(key = "operation", label = "操作类型", description = """
        list:列出pathList下的所有文件,includeSubPath为true时列出子目录文件.pathList必须提供.
        writeContextFile:把本地文件(参数fileLocal给出)写入到上下文系统,返回写入的文件名.返回文件名可能时fileLocal去掉路径后的结果,也可能不同.fileLocal必须提供.
        readContextFile:从上下文文件管理读取文件(通过参数fileNameContext给出)写入到本地文件(fileLocal)中.fileLocal和fileNameContext必须提供.
            """, mode = "select", size = 1, mandatory = true, props = "options:list:列表,writeContextFile:写入上下文文件,readContextFile:从上下文文件管理读取")

//
@Control(key = "pathList", label = "列表目录", description = "操作类型为列表时有意义\n给出希望列表的目录\n如果没有设置,则列出根目录下所有文件", size = 1, bindings = "show:this.data.operation=='list'")
@Control(key = "filePattern", label = "文件匹配", description = "不输入匹配所有文件.\n输入后星号匹配任意字符,问号匹配一个字符", size = 1, bindings = "show:this.data.operation=='list'")
@Control(key = "includeSubPath", label = "列表包含子目录", description = "操作类型为列表时有意义\ntrue代表列出所有子目录下的文件,否则只列表列表目录下文件.\n缺省为false", mode = "checkbox", defaultVal = "false", size = 1, bindings = "show:this.data.operation=='list'")
@Control(key = "fileLocal", label = "读取文件名", description = "本地操作系统文件名,可包含路径", size = 1, bindings = "show:this.data.operation!='list''")
@Control(key = "fileNameContext", label = "读取文件名", description = "本地操作系统文件名,可包含路径", size = 1, bindings = "show:this.data.operation!='list''")
public class OsFileCallDefine {

}
