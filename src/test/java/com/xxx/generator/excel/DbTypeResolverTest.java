package com.xxx.generator.excel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DbTypeResolverTest {

    @Test
    void resolvesStringTypes() {
        assertEquals("文字列", DbTypeResolver.resolve("String"));
        assertEquals("文字列", DbTypeResolver.resolve("char"));
        assertEquals("文字列", DbTypeResolver.resolve("Character"));
    }

    @Test
    void resolvesNumericTypes() {
        assertEquals("数値", DbTypeResolver.resolve("Integer"));
        assertEquals("数値", DbTypeResolver.resolve("int"));
        assertEquals("数値", DbTypeResolver.resolve("Long"));
        assertEquals("数値", DbTypeResolver.resolve("BigDecimal"));
    }

    @Test
    void resolvesBooleanTypes() {
        assertEquals("真偽値", DbTypeResolver.resolve("Boolean"));
        assertEquals("真偽値", DbTypeResolver.resolve("boolean"));
    }

    @Test
    void resolvesDateTypes() {
        assertEquals("日付", DbTypeResolver.resolve("Date"));
        assertEquals("日付", DbTypeResolver.resolve("Timestamp"));
        assertEquals("日付", DbTypeResolver.resolve("LocalDate"));
        assertEquals("日付", DbTypeResolver.resolve("LocalDateTime"));
        assertEquals("日付", DbTypeResolver.resolve("OffsetDateTime"));
    }

    @Test
    void resolvesCompositeAndReferenceTypesToDash() {
        assertEquals("-", DbTypeResolver.resolve("List<Book>"));
        assertEquals("-", DbTypeResolver.resolve("Optional<User>"));
        assertEquals("-", DbTypeResolver.resolve("Map<String, Object>"));
        assertEquals("-", DbTypeResolver.resolve("Book"));
        assertEquals("-", DbTypeResolver.resolve("void"));
        assertEquals("-", DbTypeResolver.resolve("UUID"));
    }
}
