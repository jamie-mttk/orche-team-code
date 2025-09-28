package com.mttk.orche.agent.webSearch.download;

import java.util.function.IntPredicate;

import com.mttk.orche.util.StringUtil;

//试图去掉字符串中的敏感字符,防止LLM调用失败
public class TextSanitizer {
    // 高效字符过滤：直接位置遍历，避免Stream开销
    public static String sanitize(String input) {
        if (StringUtil.isEmpty(input))
            return "";

        StringBuilder result = new StringBuilder(input.length()); // 预分配容量

        for (int i = 0; i < input.length();) {
            int codePoint = input.codePointAt(i);
            if (isSafeChar().test(codePoint)) {
                result.appendCodePoint(codePoint);
            }
            i += Character.charCount(codePoint); // 正确处理Unicode代理对
        }

        return result.toString();
    }

    // 定义大模型安全的Unicode字符范围
    private static IntPredicate isSafeChar() {
        return codePoint -> {
            // 1. 保留基本ASCII控制字符（换行、制表符、回车）
            if (codePoint <= 31) {
                return codePoint == '\t' || codePoint == '\n' || codePoint == '\r';
            }

            // 2. 排除DEL字符(127)和C1控制字符(0x80-0x9F)
            if (codePoint == 127 || (codePoint >= 0x80 && codePoint <= 0x9F)) {
                return false;
            }

            // 3. 排除零宽字符（可能影响大模型处理）
            if (codePoint == 0x200B || // 零宽空格
                    codePoint == 0x200C || // 零宽非连接符
                    codePoint == 0x200D || // 零宽连接符
                    codePoint == 0xFEFF) { // 零宽非断行空格
                return false;
            }

            // 4. 检查Unicode字符类别
            int charType = Character.getType(codePoint);

            // 5. 只保留大模型友好的字符类型
            return charType == Character.UPPERCASE_LETTER || // 大写字母 A-Z
                    charType == Character.LOWERCASE_LETTER || // 小写字母 a-z
                    charType == Character.TITLECASE_LETTER || // 标题字母
                    charType == Character.MODIFIER_LETTER || // 修饰字母
                    charType == Character.OTHER_LETTER || // 其他字母（中文、日文等）
                    charType == Character.DECIMAL_DIGIT_NUMBER || // 数字 0-9
                    charType == Character.LETTER_NUMBER || // 字母数字
                    charType == Character.OTHER_NUMBER || // 其他数字
                    charType == Character.DASH_PUNCTUATION || // 连字符 -
                    charType == Character.START_PUNCTUATION || // 开始标点 ( [ {
                    charType == Character.END_PUNCTUATION || // 结束标点 ) ] }
                    charType == Character.CONNECTOR_PUNCTUATION || // 连接标点 _
                    charType == Character.OTHER_PUNCTUATION || // 其他标点 . , ! ? : ; " '
                    charType == Character.SPACE_SEPARATOR; // 空格
            // 排除：MATH_SYMBOL, CURRENCY_SYMBOL, MODIFIER_SYMBOL, OTHER_SYMBOL
            // 排除：表情符号、特殊符号、数学符号、货币符号等
        };
    }

    // public static void main(String[] args) {
    // System.out.println("=== TextSanitizer 大模型文本清理测试 ===\n");

    // // 1. 基本测试
    // String test1 = "Hello\u0003世界！\n正常文本\t\u200B隐藏字符🚀";
    // System.out.println("1. 基本测试：");
    // System.out.println("原始文本：" + test1);
    // System.out.println("清理结果：" + sanitize(test1));

    // // 2. 表情符号和特殊符号测试
    // String test2 = "A🚀B🎉C🌟D💡E⭐F";
    // System.out.println("\n2. 表情符号测试：");
    // System.out.println("原始文本：" + test2);
    // System.out.println("清理结果：" + sanitize(test2));

    // // 3. 零宽字符测试
    // String test3 = "正常\u200B文本\u200C测试\u200D内容\uFEFF";
    // System.out.println("\n3. 零宽字符测试：");
    // System.out.println("原始文本：" + test3);
    // System.out.println("清理结果：" + sanitize(test3));

    // // 4. 数学符号和货币符号测试
    // String test4 = "价格$100 + 50% = 150€ 数学符号∑∏∫";
    // System.out.println("\n4. 符号测试：");
    // System.out.println("原始文本：" + test4);
    // System.out.println("清理结果：" + sanitize(test4));

    // // 5. 控制字符测试
    // String test5 = "文本\u0001控制\u0002字符\u0003测试\u007F";
    // System.out.println("\n5. 控制字符测试：");
    // System.out.println("原始文本：" + test5);
    // System.out.println("清理结果：" + sanitize(test5));

    // // 6. 大模型友好文本测试
    // String test6 = "这是一个正常的文本，包含中文、English、数字123和标点符号！";
    // System.out.println("\n6. 大模型友好文本测试：");
    // System.out.println("原始文本：" + test6);
    // System.out.println("清理结果：" + sanitize(test6));
    // System.out.println("是否保持不变：" + test6.equals(sanitize(test6)));

    // // 7. 性能测试
    // String longText = "长文本测试🚀💡⭐".repeat(1000);
    // long start = System.currentTimeMillis();
    // String result = sanitize(longText);
    // long duration = System.currentTimeMillis() - start;
    // System.out.println("\n7. 性能测试：");
    // System.out.println("原始长度：" + longText.length());
    // System.out.println("结果长度：" + result.length());
    // System.out.println("处理耗时：" + duration + "ms");
    // System.out.println("包含表情符号：" + result.contains("🚀"));

    // System.out.println("\n=== 测试完成 ===");
    // }
}
