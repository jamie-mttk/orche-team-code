package com.mttk.orche.agent.contextSummary;

public class Prompt {
  public static final String PRMOPT_SUMMARY = """
        你作为对话纪要专家，需分析以下对话历史：

      ``` markdown

      ${HISTORYS}
      ```


      请执行：
      1. 提取核心主题作为文件名（短横命名，如"trip-plan.md"）
      2. 用20字内描述文档价值
      3. 生成Markdown内容，包含：
         - 关键决策点（加粗重点）
         - 工具调用细节（代码块展示参数）
         - 待解决问题（无序列表）
         - 用户原始需求（引用块）

      严格输出JSON，禁止额外解释：
      {
        "fileName": "",
        "description": "",
        "content": ""
      }

        """;
}
