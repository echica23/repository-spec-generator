package com.xxx.generator.excel;

public record ExcelTreeRow(
        String section,
        int no,
        int depth,
        String logicalName,
        String sqlPhysicalName,
        String physicalName,
        String dbType,
        String javaType,
        String note
) {
    static ExcelTreeRow blank() {
        return new ExcelTreeRow("", 0, 0, "", "", "", "", "", "");
    }
}
