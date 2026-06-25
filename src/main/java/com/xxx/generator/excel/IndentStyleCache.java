package com.xxx.generator.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

final class IndentStyleCache {

    private final Workbook workbook;
    private final Map<String, CellStyle> cache = new HashMap<>();

    IndentStyleCache(Workbook workbook) {
        this.workbook = workbook;
    }

    CellStyle withIndent(CellStyle baseStyle, short indent) {
        if (baseStyle == null || indent <= 0) {
            return baseStyle;
        }

        String key = System.identityHashCode(baseStyle) + ":" + indent;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        CellStyle indented = workbook.createCellStyle();
        indented.cloneStyleFrom(baseStyle);
        indented.setIndention(indent);
        cache.put(key, indented);
        return indented;
    }
}
