package com.xxx.generator.excel;

final class ExcelNames {

    private ExcelNames() {
    }

    static String logicalName(String javadoc, String physicalName) {
        if (javadoc != null && !javadoc.isBlank()) {
            return javadoc;
        }
        return physicalName != null ? physicalName : "";
    }
}
