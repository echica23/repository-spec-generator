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

    private final boolean templateMode;
    private final CellStyle headerStyle;
    private final CellStyle sectionHeaderStyle;
    private final CellStyle dataStyle;
    private final CellStyle noteStyle;
    private final int marginRowIndex;
    private final int blankRowIndex;
    private final int repositoryRowIndex;
    private final int repositorySummaryRowIndex;
    private final int methodRowIndex;
    private final int methodSummaryRowIndex;
    private final int headerRowIndex;
    private final int inputRowIndex;
    private final int outputRowIndex;
    private final int dataRowIndex;
    private final int xmlHeaderRowIndex;
    private final int xmlLineRowIndex;

    private ExcelCellStyles(boolean templateMode,
                            CellStyle headerStyle,
                            CellStyle sectionHeaderStyle,
                            CellStyle dataStyle,
                            CellStyle noteStyle,
                            int marginRowIndex,
                            int blankRowIndex,
                            int repositoryRowIndex,
                            int repositorySummaryRowIndex,
                            int methodRowIndex,
                            int methodSummaryRowIndex,
                            int headerRowIndex,
                            int inputRowIndex,
                            int outputRowIndex,
                            int dataRowIndex,
                            int xmlHeaderRowIndex,
                            int xmlLineRowIndex) {
        this.templateMode = templateMode;
        this.headerStyle = headerStyle;
        this.sectionHeaderStyle = sectionHeaderStyle;
        this.dataStyle = dataStyle;
        this.noteStyle = noteStyle;
        this.marginRowIndex = marginRowIndex;
        this.blankRowIndex = blankRowIndex;
        this.repositoryRowIndex = repositoryRowIndex;
        this.repositorySummaryRowIndex = repositorySummaryRowIndex;
        this.methodRowIndex = methodRowIndex;
        this.methodSummaryRowIndex = methodSummaryRowIndex;
        this.headerRowIndex = headerRowIndex;
        this.inputRowIndex = inputRowIndex;
        this.outputRowIndex = outputRowIndex;
        this.dataRowIndex = dataRowIndex;
        this.xmlHeaderRowIndex = xmlHeaderRowIndex;
        this.xmlLineRowIndex = xmlLineRowIndex;
    }

    static ExcelCellStyles createProgrammatic(Workbook workbook) {
        return new ExcelCellStyles(
                false,
                createHeaderStyle(workbook),
                createSectionHeaderStyle(workbook),
                createDataStyle(workbook, false),
                createDataStyle(workbook, true),
                DefaultTemplateGenerator.MARGIN_ROW,
                DefaultTemplateGenerator.BLANK_ROW,
                DefaultTemplateGenerator.REPOSITORY_ROW,
                DefaultTemplateGenerator.REPOSITORY_SUMMARY_ROW,
                DefaultTemplateGenerator.METHOD_ROW,
                DefaultTemplateGenerator.METHOD_SUMMARY_ROW,
                DefaultTemplateGenerator.HEADER_ROW,
                DefaultTemplateGenerator.INPUT_ROW,
                DefaultTemplateGenerator.OUTPUT_ROW,
                DefaultTemplateGenerator.DATA_ROW,
                DefaultTemplateGenerator.XML_HEADER_ROW,
                DefaultTemplateGenerator.XML_LINE_ROW
        );
    }

    static ExcelCellStyles fromTemplate(Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        return new ExcelCellStyles(
                true,
                styleAt(sheet, DefaultTemplateGenerator.HEADER_ROW, 0, createHeaderStyle(workbook)),
                styleAt(sheet, DefaultTemplateGenerator.METHOD_ROW, 0, createSectionHeaderStyle(workbook)),
                styleAt(sheet, DefaultTemplateGenerator.DATA_ROW, 0, createDataStyle(workbook, false)),
                styleAt(sheet, DefaultTemplateGenerator.DATA_ROW, NOTE_COLUMN_INDEX, createDataStyle(workbook, true)),
                DefaultTemplateGenerator.MARGIN_ROW,
                DefaultTemplateGenerator.BLANK_ROW,
                DefaultTemplateGenerator.REPOSITORY_ROW,
                DefaultTemplateGenerator.REPOSITORY_SUMMARY_ROW,
                DefaultTemplateGenerator.METHOD_ROW,
                DefaultTemplateGenerator.METHOD_SUMMARY_ROW,
                DefaultTemplateGenerator.HEADER_ROW,
                DefaultTemplateGenerator.INPUT_ROW,
                DefaultTemplateGenerator.OUTPUT_ROW,
                DefaultTemplateGenerator.DATA_ROW,
                DefaultTemplateGenerator.XML_HEADER_ROW,
                DefaultTemplateGenerator.XML_LINE_ROW
        );
    }

    boolean templateMode() {
        return templateMode;
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

    int templateRowIndex(TemplateRowKind kind) {
        return switch (kind) {
            case MARGIN -> marginRowIndex;
            case BLANK -> blankRowIndex;
            case REPOSITORY -> repositoryRowIndex;
            case REPOSITORY_SUMMARY -> repositorySummaryRowIndex;
            case METHOD -> methodRowIndex;
            case METHOD_SUMMARY -> methodSummaryRowIndex;
            case HEADER -> headerRowIndex;
            case INPUT -> inputRowIndex;
            case OUTPUT -> outputRowIndex;
            case DATA -> dataRowIndex;
            case XML_HEADER -> xmlHeaderRowIndex;
            case XML_LINE -> xmlLineRowIndex;
        };
    }

    private static CellStyle styleAt(Sheet sheet, int rowIndex, int columnIndex, CellStyle fallback) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return fallback;
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null || cell.getCellStyle() == null) {
            return fallback;
        }
        return cell.getCellStyle();
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        return createFilledStyle(workbook, IndexedColors.GREY_25_PERCENT);
    }

    private static CellStyle createSectionHeaderStyle(Workbook workbook) {
        return createFilledStyle(workbook, IndexedColors.PALE_BLUE);
    }

    private static CellStyle createFilledStyle(Workbook workbook, IndexedColors fillColor) {
        CellStyle style = workbook.createCellStyle();
        applyThinBorder(style);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        style.setFillForegroundColor(fillColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook, boolean wrapText) {
        CellStyle style = workbook.createCellStyle();
        applyThinBorder(style);
        style.setWrapText(wrapText);
        return style;
    }

    private static void applyThinBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}
