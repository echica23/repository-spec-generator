package com.xxx.generator.excel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlPhysicalNameConverterTest {

    @Test
    void convertsCamelCaseToUpperSnakeCase() {
        assertEquals("BOOK_ID", SqlPhysicalNameConverter.convert("bookId"));
        assertEquals("HOGE_CD", SqlPhysicalNameConverter.convert("hogeCd"));
        assertEquals("URL", SqlPhysicalNameConverter.convert("URL"));
        assertEquals("BOOK_URL", SqlPhysicalNameConverter.convert("bookURL"));
        assertEquals("ID", SqlPhysicalNameConverter.convert("id"));
        assertEquals("PUBLISHED_DATE", SqlPhysicalNameConverter.convert("publishedDate"));
    }

    @Test
    void returnsEmptyForBlankInput() {
        assertEquals("", SqlPhysicalNameConverter.convert(null));
        assertEquals("", SqlPhysicalNameConverter.convert(""));
        assertEquals("", SqlPhysicalNameConverter.convert("   "));
    }
}
