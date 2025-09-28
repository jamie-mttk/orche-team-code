package com.mttk;

public class Foo {
    public static void main(String[] args) {
        String str = buildToolName("报告生成助手", "__68b5b2de879bb5255f88a7b2");
        System.out.println(str);
    }

    /**
     * 构建工具名称，确保总长度不超过64字符
     * 
     * @param name 工具名称
     * @param id   工具ID
     * @return 构建后的工具名称
     */
    public static String buildToolName(String name, String id) {
        name = removeSpecialChars(name);
        String suffix = "__" + id;

        // 如果suffix本身就超过64字符，直接返回suffix
        if (suffix.length() > 64) {
            return suffix;
        }

        // 计算name可以使用的最大长度
        int maxNameLength = 64 - suffix.length();

        // 如果name长度超过限制，截断name
        if (name.length() > maxNameLength) {
            name = name.substring(0, maxNameLength);
        }

        return name + suffix;
    }

    public static String removeSpecialChars(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[^a-zA-Z0-9_\\u4e00-\\u9fff]", "");
    }
}
