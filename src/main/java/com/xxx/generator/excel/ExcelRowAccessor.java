package com.xxx.generator.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.Map;

final class TemplateStyleCache {

    private final Workbook workbook;
    private final Map<String, CellStyle> cache = new HashMap<>();

    TemplateStyleCache(Workbook workbook) {
        this.workbook = workbook;
    }

    CellStyle styleFor(Sheet sheet, int templateRowIndex, int columnIndex) {
        String key = templateRowIndex + ":" + columnIndex;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        Row templateRow = sheet.getRow(templateRowIndex);
        if (templateRow == null) {
            return null;
        }
        Cell templateCell = templateRow.getCell(columnIndex);
        if (templateCell == null || templateCell.getCellStyle() == null) {
            return null;
        }

        CellStyle cloned = workbook.createCellStyle();
        cloned.cloneStyleFrom(templateCell.getCellStyle());
        cache.put(key, cloned);
        return cloned;
    }
}

final class ExcelRowAccessor {

    private ExcelRowAccessor() {
    }

    static Row prepareRow(Sheet sheet,
                          int rowIndex,
                          TemplateRowKind kind,
                          ExcelCellStyles styles,
                          TemplateStyleCache styleCache) {
        Row row = sheet.getRow(rowIndex);
        boolean isNewRow = row == null;
        if (isNewRow) {
            row = sheet.createRow(rowIndex);
        }

        if (isNewRow) {
            if (styles.templateMode()) {
                applyTemplateRow(sheet, row, styles.templateRowIndex(kind), styleCache);
            } else {
                applyProgrammaticRow(row, kind, styles);
            }
        }

        return row;
    }

    static void setStringValue(Row row, int columnIndex, String value) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.setCellValue(value != null ? value : "");
    }

    static void setNumericValue(Row row, int columnIndex, int value) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.setCellValue(value);
    }

    private static void applyTemplateRow(Sheet sheet,
                                         Row targetRow,
                                         int templateRowIndex,
                                         TemplateStyleCache styleCache) {
        Row templateRow = sheet.getRow(templateRowIndex);
        if (templateRow == null) {
            return;
        }

        targetRow.setHeight(templateRow.getHeight());

        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            Cell templateCell = templateRow.getCell(columnIndex);
            Cell targetCell = targetRow.getCell(columnIndex);
            if (targetCell == null) {
                targetCell = targetRow.createCell(columnIndex);
            }
            if (templateCell != null) {
                CellStyle clonedStyle = styleCache.styleFor(sheet, templateRowIndex, columnIndex);
                if (clonedStyle != null) {
                    targetCell.setCellStyle(clonedStyle);
                }
            }
        }

        copyMergedRegions(sheet, templateRowIndex, targetRow.getRowNum());
    }

    private static void applyProgrammaticRow(Row row, TemplateRowKind kind, ExcelCellStyles styles) {
        CellStyle rowStyle = programmaticStyle(kind, styles);
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            CellStyle cellStyle = columnIndex == ExcelCellStyles.NOTE_COLUMN_INDEX
                    ? styles.noteStyle(columnIndex)
                    : rowStyle;
            cell.setCellStyle(cellStyle);
        }
    }

    private static CellStyle programmaticStyle(TemplateRowKind kind, ExcelCellStyles styles) {
        return switch (kind) {
            case METHOD, METHOD_SUMMARY -> styles.sectionHeaderStyle();
            case HEADER -> styles.headerStyle();
            default -> styles.dataStyle();
        };
    }

    private static void copyMergedRegions(Sheet sheet, int templateRowIndex, int targetRowIndex) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.getFirstRow() == templateRowIndex && region.getLastRow() == templateRowIndex) {
                CellRangeAddress copied = new CellRangeAddress(
                        targetRowIndex,
                        targetRowIndex,
                        region.getFirstColumn(),
                        region.getLastColumn()
                );
                if (!containsEquivalentMerge(sheet, copied)) {
                    sheet.addMergedRegion(copied);
                }
            }
        }
    }

    private static boolean containsEquivalentMerge(Sheet sheet, CellRangeAddress candidate) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress existing = sheet.getMergedRegion(i);
            if (existing.getFirstRow() == candidate.getFirstRow()
                    && existing.getLastRow() == candidate.getLastRow()
                    && existing.getFirstColumn() == candidate.getFirstColumn()
                    && existing.getLastColumn() == candidate.getLastColumn()) {
                return true;
            }
        }
        return false;
    }
}
