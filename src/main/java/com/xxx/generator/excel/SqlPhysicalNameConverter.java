package com.xxx.generator.excel;

import com.xxx.generator.parser.TypeReferenceExtractor;

import java.util.Locale;

/**
 * Java 物理項目名（camelCase）を SQL 物理項目名（UPPER_SNAKE_CASE）へ変換する。
 */
public final class SqlPhysicalNameConverter {

    private static final String DASH = "-";

    private SqlPhysicalNameConverter() {
    }

    /**
     * Java 標準型フィールドのみ camelCase → UPPER_SNAKE_CASE へ変換する。
     * DTO・List・Optional・Map などは {@code -} を返す。
     */
    public static String resolve(String javaPhysicalName, String javaType, TypeReferenceExtractor extractor) {
        if (!isStandardTypeField(javaType, extractor)) {
            return DASH;
        }
        return convert(javaPhysicalName);
    }

    public static String convert(String javaPhysicalName) {
        if (javaPhysicalName == null || javaPhysicalName.isBlank()) {
            return "";
        }
        return camelToSnake(javaPhysicalName).toUpperCase(Locale.ROOT);
    }

    static boolean isStandardTypeField(String javaType, TypeReferenceExtractor extractor) {
        if (javaType == null || javaType.isBlank()) {
            return false;
        }

        String trimmed = javaType.trim();
        if (isCompositeType(trimmed)) {
            return false;
        }

        String primaryType = extractPrimaryType(trimmed);
        return extractor.isStandardType(primaryType);
    }

    private static boolean isCompositeType(String javaType) {
        if (javaType.endsWith("[]")) {
            return true;
        }
        int genericStart = javaType.indexOf('<');
        if (genericStart < 0) {
            return false;
        }
        String rawName = javaType.substring(0, genericStart).trim();
        return "List".equals(rawName) || "Optional".equals(rawName) || "Map".equals(rawName);
    }

    private static String extractPrimaryType(String javaType) {
        int genericStart = javaType.indexOf('<');
        if (genericStart >= 0) {
            return javaType.substring(0, genericStart).trim();
        }
        if (javaType.endsWith("[]")) {
            return javaType.substring(0, javaType.length() - 2).trim();
        }
        return javaType.trim();
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
