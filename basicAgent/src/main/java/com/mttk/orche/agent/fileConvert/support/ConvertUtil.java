package com.mttk.orche.agent.fileConvert.support;

import java.util.Map;

/**
 * 转换选项工具类
 * 提供统一的选项值获取方法
 */
public class ConvertUtil {

    private ConvertUtil() {
        // 工具类，禁止实例化
    }

    /**
     * 从options中获取指定key的值，如果不存在则返回默认值
     * 
     * @param options      选项Map
     * @param key          选项键
     * @param defaultValue 默认值
     * @param <T>          值类型
     * @return 选项值或默认值
     * @throws Exception 如果类型不支持
     */
    @SuppressWarnings("unchecked")
    public static <T> T getOptionValue(Map<String, Object> options, String key, T defaultValue) throws Exception {
        // 如果options为null，返回默认值
        if (options == null) {
            return defaultValue;
        }

        // 如果key不存在，返回默认值
        if (!options.containsKey(key)) {
            return defaultValue;
        }

        // 获取值
        Object value = options.get(key);
        if (value == null) {
            return defaultValue;
        }

        // 检查默认值类型，只支持String/Integer/Boolean
        if (defaultValue != null) {
            Class<?> defaultType = defaultValue.getClass();
            if (!isSupportedType(defaultType)) {
                throw new IllegalArgumentException(
                        "不支持的类型: " + defaultType.getName() + "，只支持 String/Integer/Boolean");
            }

            // 检查值的类型是否匹配
            if (!defaultType.isInstance(value)) {
                throw new IllegalArgumentException(
                        "选项值类型不匹配: 期望 " + defaultType.getName() + "，实际 " + value.getClass().getName());
            }
        } else {
            // 默认值为null时，检查实际值的类型
            if (!isSupportedType(value.getClass())) {
                throw new IllegalArgumentException(
                        "不支持的类型: " + value.getClass().getName() + "，只支持 String/Integer/Boolean");
            }
        }

        return (T) value;
    }

    /**
     * 检查类型是否被支持
     * 
     * @param type 类型
     * @return 是否支持
     */
    private static boolean isSupportedType(Class<?> type) {
        return type == String.class || type == Integer.class || type == Boolean.class;
    }
}
