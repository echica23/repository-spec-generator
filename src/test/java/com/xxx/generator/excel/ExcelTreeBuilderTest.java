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
