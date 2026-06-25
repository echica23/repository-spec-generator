package com.xxx.generator.excel;

import java.util.Locale;

/**
 * Java 物理項目名（camelCase）を SQL 物理項目名（UPPER_SNAKE_CASE）へ変換する。
 */
public final class SqlPhysicalNameConverter {

    private SqlPhysicalNameConverter() {
    }

    public static String convert(String javaPhysicalName) {
        if (javaPhysicalName == null || javaPhysicalName.isBlank()) {
            return "";
        }
        return camelToSnake(javaPhysicalName).toUpperCase(Locale.ROOT);
    }

    private static String camelToSnake(String name) {
        StringBuilder result = new StringBuilder(name.length() + 4);
        for (int i = 0; i < name.length(); i++) {
            char current = name.charAt(i);
            if (Character.isUpperCase(current) && i > 0) {
                char previous = name.charAt(i - 1);
                if (Character.isLowerCase(previous)) {
                    result.append('_');
                } else if (i + 1 < name.length() && Character.isLowerCase(name.charAt(i + 1))) {
                    result.append('_');
                }
            }
            result.append(current);
        }
        return result.toString();
    }
}
