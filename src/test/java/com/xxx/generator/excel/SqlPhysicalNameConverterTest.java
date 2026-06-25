package com.xxx.generator.excel;

import com.xxx.generator.parser.TypeReferenceExtractor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlPhysicalNameConverterTest {

    @Test
    void returnsDashForDtoAndCompositeTypes() {
        TypeReferenceExtractor extractor = new TypeReferenceExtractor();
        assertEquals("-", SqlPhysicalNameConverter.resolve("book", "Book", extractor));
        assertEquals("-", SqlPhysicalNameConverter.resolve("books", "List<Book>", extractor));
        assertEquals("-", SqlPhysicalNameConverter.resolve("user", "Optional<User>", extractor));
        assertEquals("-", SqlPhysicalNameConverter.resolve("stats", "Map<String, Object>", extractor));
    }

    @Test
    void convertsStandardTypeFields() {
        TypeReferenceExtractor extractor = new TypeReferenceExtractor();
        assertEquals("BOOK_ID", SqlPhysicalNameConverter.resolve("bookId", "Long", extractor));
        assertEquals("TITLE", SqlPhysicalNameConverter.resolve("title", "String", extractor));
    }

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
