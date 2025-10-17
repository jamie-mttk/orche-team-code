package com.mttk.orche.agent.react;

public class PromptShare {

     // 任务背景信息
     public static final String SYSTEM_BACKGROUND = """
                ## 背景信息

                ### 当前日期
                ${__now}

                ### 原始任务
               用户第一个提示词为原始任务

                ### 工具列表

                ```json
                ${__tools}
                ```

                ### 会话文件管理
                会话文件是指在整个大模型对话过程中相关的文件.读取和写入使用"会话文件管理智能体"实现.
                除了操作系统文件管理之外的其他工具,除非特别说明,输入输出文件都是会话文件.调用前必须确保是会话文件,否则严禁调用.
                当前会话文件列表如下:

                ${__files}


                """;

     public static final String NEXT_STEP_BACKGROUND = """
               ## 上次工具执行结果
                ${lastToolCallResults}

               ## 会话文件列表
                ${__files}


                """;
}
