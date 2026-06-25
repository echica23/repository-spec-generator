package com.xxx.generator.excel;

import java.util.Set;

/**
 * Java 型から DB 型（Excel 出力用ラベル）を判定する。
 */
public final class DbTypeResolver {

    private static final String DASH = "-";
    private static final String STRING_TYPE = "文字列";
    private static final String NUMERIC_TYPE = "数値";
    private static final String BOOLEAN_TYPE = "真偽値";
    private static final String DATE_TYPE = "日付";

    private static final Set<String> STRING_TYPES = Set.of("String", "char", "Character");
    private static final Set<String> NUMERIC_TYPES = Set.of(
            "Integer", "int", "Long", "long", "Short", "short",
            "Float", "float", "Double", "double", "BigDecimal"
    );
    private static final Set<String> BOOLEAN_TYPES = Set.of("Boolean", "boolean");
    private static final Set<String> DATE_TYPES = Set.of(
            "Date", "Timestamp", "LocalDate", "LocalDateTime", "OffsetDateTime"
    );

    private DbTypeResolver() {
    }

    public static String resolve(String javaType) {
        if (javaType == null || javaType.isBlank()) {
            return DASH;
        }

        String trimmed = javaType.trim();
        if (isCompositeType(trimmed)) {
            return DASH;
        }

        String primaryType = extractPrimaryType(trimmed);
        if (primaryType.isBlank() || "void".equals(primaryType)) {
            return DASH;
        }

        if (STRING_TYPES.contains(primaryType)) {
            return STRING_TYPE;
        }
        if (NUMERIC_TYPES.contains(primaryType)) {
            return NUMERIC_TYPE;
        }
        if (BOOLEAN_TYPES.contains(primaryType)) {
            return BOOLEAN_TYPE;
        }
        if (DATE_TYPES.contains(primaryType)) {
            return DATE_TYPE;
        }

        return DASH;
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
}
