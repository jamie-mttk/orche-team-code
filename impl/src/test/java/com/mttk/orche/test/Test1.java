package com.mttk.orche.test;

import java.util.ArrayList;
import java.util.List;

public class Test1 {

    // 分析输入的以 - 开头的项目,得到解析后的数组
    private static List<String> parseItems(String input) {
        List<String> resultList = new ArrayList<>();
        // 处理空输入
        if (input == null || input.isEmpty()) {
            return resultList;
        }

        // 按行分割输入
        String[] lines = input.split("\\r?\\n");

        for (String line : lines) {
            // 去除行首空白字符
            String trimmedLine = line.trim();

            // 检查是否以 - 开头
            if (trimmedLine.startsWith("-")) {
                // 提取 - 后面的内容
                String content = trimmedLine.substring(1).trim();
                if (!content.isEmpty()) {
                    resultList.add(content);
                }
            }
        }

        return resultList;
    }

    public static void main(String[] args) {
        String str = "Line 1\n" +
                "Line 2\n" +
                "Line 3 - aaa Line 3.1\n" +
                "### COntent\n" +
                "- AAA\n" +
                "- BBB\n" +
                "- CCC\n" +
                "\n";

        List<String> list = parseItems(str);
        for (String s : list) {
            System.out.println(s);
        }
    }
}