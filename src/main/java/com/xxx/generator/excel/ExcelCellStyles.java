package com.xxx.generator.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

final class ExcelCellStyles {

    static final int COLUMN_COUNT = 8;
    static final int NOTE_COLUMN_INDEX = 7;

    private final CellStyle headerStyle;
    private final CellStyle sectionHeaderStyle;
    private final CellStyle dataStyle;
    private final CellStyle noteStyle;

    ExcelCellStyles(Workbook workbook) {
        headerStyle = createHeaderStyle(workbook);
        sectionHeaderStyle = createSectionHeaderStyle(workbook);
        dataStyle = createDataStyle(workbook, false);
        noteStyle = createDataStyle(workbook, true);
    }

    CellStyle headerStyle() {
        return headerStyle;
    }

    CellStyle sectionHeaderStyle() {
        return sectionHeaderStyle;
    }

    CellStyle dataStyle() {
        return dataStyle;
    }

    CellStyle noteStyle(int columnIndex) {
        return columnIndex == NOTE_COLUMN_INDEX ? noteStyle : dataStyle;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        return createFilledStyle(workbook, IndexedColors.GREY_25_PERCENT);
    }

    private CellStyle createSectionHeaderStyle(Workbook workbook) {
        return createFilledStyle(workbook, IndexedColors.PALE_BLUE);
    }

    private CellStyle createFilledStyle(Workbook workbook, IndexedColors fillColor) {
        CellStyle style = workbook.createCellStyle();
        applyThinBorder(style);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        style.setFillForegroundColor(fillColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook, boolean wrapText) {
        CellStyle style = workbook.createCellStyle();
        applyThinBorder(style);
        style.setWrapText(wrapText);
        return style;
    }

    private void applyThinBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}
