package com.xxx.generator.excel;

import com.xxx.generator.model.FieldInfo;
import com.xxx.generator.model.MethodInfo;
import com.xxx.generator.model.ParameterInfo;
import com.xxx.generator.model.TypeInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExcelTreeBuilderTest {

    private final ExcelTreeBuilder builder = new ExcelTreeBuilder();

    @Test
    void usesDashForSqlPhysicalNameOnDtoRows() {
        MethodInfo method = new MethodInfo(
                "findById",
                "IDで書籍を取得する。",
                "Book",
                List.of(new ParameterInfo("id", "Long", "書籍ID")),
                List.of("Long"),
                List.of("Book")
        );
        TypeInfo book = new TypeInfo("Book", "書籍", List.of(
                new FieldInfo("id", "Long", "書籍ID"),
                new FieldInfo("title", "String", "タイトル")
        ));

        List<ExcelTreeRow> rows = builder.buildMethodContent(method, builder.toTypeMap(List.of(book)));

        ExcelTreeRow outputDtoRow = rows.stream()
                .filter(row -> "Book".equals(row.physicalName()))
                .findFirst()
                .orElseThrow();
        assertEquals("-", outputDtoRow.sqlPhysicalName());

        ExcelTreeRow nestedFieldRow = rows.stream()
                .filter(row -> "  id".equals(row.physicalName()))
                .findFirst()
                .orElseThrow();
        assertEquals("  ID", nestedFieldRow.sqlPhysicalName());
    }

    @Test
    void usesDashForSqlPhysicalNameOnDtoParameter() {
        MethodInfo method = new MethodInfo(
                "save",
                "書籍を保存する。",
                "Book",
                List.of(new ParameterInfo("book", "Book", "書籍")),
                List.of("Book"),
                List.of("Book")
        );
        TypeInfo book = new TypeInfo("Book", "書籍", List.of(
                new FieldInfo("title", "String", "タイトル")
        ));

        List<ExcelTreeRow> rows = builder.buildMethodContent(method, builder.toTypeMap(List.of(book)));

        ExcelTreeRow dtoParameterRow = rows.stream()
                .filter(row -> "book".equals(row.physicalName()))
                .findFirst()
                .orElseThrow();
        assertEquals("-", dtoParameterRow.sqlPhysicalName());
    }

    @Test
    void fillsSqlPhysicalNameAndDbTypeForInputParameter() {
        MethodInfo method = new MethodInfo(
                "findById",
                "IDで書籍を取得する。",
                "Book",
                List.of(new ParameterInfo("id", "Long", "書籍ID")),
                List.of("Long"),
                List.of("Book")
        );
        TypeInfo book = new TypeInfo("Book", "書籍", List.of(
                new FieldInfo("id", "Long", "書籍ID")
        ));

        List<ExcelTreeRow> rows = builder.buildMethodContent(method, builder.toTypeMap(List.of(book)));

        ExcelTreeRow inputRow = rows.stream()
                .filter(row -> row.no() == 1 && !row.section().startsWith("●"))
                .findFirst()
                .orElseThrow();
        assertEquals("書籍ID", inputRow.logicalName());
        assertEquals("ID", inputRow.sqlPhysicalName());
        assertEquals("id", inputRow.physicalName());
        assertEquals("数値", inputRow.dbType());
    }

    @Test
    void usesDashForNamelessPrimitiveReturn() {
        MethodInfo method = new MethodInfo(
                "countByAuthor",
                "著者名に一致する書籍件数を取得する。",
                "int",
                List.of(new ParameterInfo("author", "String", "著者名")),
                List.of("String"),
                List.of("int")
        );

        List<ExcelTreeRow> rows = builder.buildMethodContent(method, Map.of());

        ExcelTreeRow outputRow = rows.stream()
                .filter(row -> "int".equals(row.javaType()) && "-".equals(row.logicalName()))
                .findFirst()
                .orElseThrow();
        assertEquals("-", outputRow.sqlPhysicalName());
        assertEquals("-", outputRow.physicalName());
        assertEquals("-", outputRow.dbType());
    }

    @Test
    void showsOutputForVoidReturn() {
        MethodInfo method = new MethodInfo(
                "deleteById",
                "書籍を削除する。",
                "void",
                List.of(new ParameterInfo("id", "Long", "書籍ID")),
                List.of("Long"),
                List.of()
        );

        List<ExcelTreeRow> rows = builder.buildMethodContent(method, Map.of());

        assertEquals(ExcelTreeBuilder.OUTPUT_SECTION, rows.stream()
                .filter(row -> ExcelTreeBuilder.OUTPUT_SECTION.equals(row.section()))
                .findFirst()
                .orElseThrow()
                .section());

        ExcelTreeRow outputRow = rows.stream()
                .filter(row -> "void".equals(row.javaType()))
                .findFirst()
                .orElseThrow();
        assertEquals("-", outputRow.logicalName());
        assertEquals("-", outputRow.dbType());
    }
}
