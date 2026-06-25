package com.xxx.generator.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@code template/default.xlsx} を生成するユーティリティ。
 */
public final class DefaultTemplateGenerator {

    static final String SHEET_NAME = "RepositorySpec";
    static final int TOP_MARGIN_ROWS = 7;
    static final int MARGIN_ROW = 0;
    static final int REPOSITORY_ROW = 7;
    static final int REPOSITORY_SUMMARY_ROW = 8;
    static final int BLANK_ROW = 9;
    static final int METHOD_ROW = 10;
    static final int METHOD_SUMMARY_ROW = 11;
    static final int HEADER_ROW = 12;
    static final int DATA_ROW = 13;
    static final int XML_LINE_ROW = 14;
    static final int INPUT_ROW = 15;
    static final int OUTPUT_ROW = 16;
    static final int XML_HEADER_ROW = REPOSITORY_ROW;

    private DefaultTemplateGenerator() {
    }

    public static void main(String[] args) throws IOException {
        Path outputPath = args.length > 0 ? Path.of(args[0]) : Path.of("template/default.xlsx");
        generate(outputPath);
        System.out.println("Template generated: " + outputPath.toAbsolutePath());
    }

    static void generate(Path outputPath) throws IOException {
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            ExcelCellStyles styles = ExcelCellStyles.createProgrammatic(workbook);

            for (int i = 0; i < TOP_MARGIN_ROWS; i++) {
                createSectionRow(sheet, i, styles.dataStyle());
            }

            createLabelRow(sheet, REPOSITORY_ROW, "Repository", styles.dataStyle());
            createLabelRow(sheet, REPOSITORY_SUMMARY_ROW, "Repository概要", styles.dataStyle());
            createSectionRow(sheet, BLANK_ROW, styles.dataStyle());
            createSqlIdRow(sheet, METHOD_ROW, "SQL_ID", "SQL概要", styles.sectionHeaderStyle());
            createSqlIdRow(sheet, METHOD_SUMMARY_ROW, "selectXX", "XXを取得する", styles.sectionHeaderStyle());

            Row headerRow = sheet.createRow(HEADER_ROW);
            String[] headers = {
                    "区分", "項番", "論理項目名", "物理項目名(SQL)",
                    "物理項目名", "DB型", "Java型", "備考"
            };
            for (int columnIndex = 0; columnIndex < headers.length; columnIndex++) {
                Cell cell = headerRow.createCell(columnIndex);
                cell.setCellValue(headers[columnIndex]);
                cell.setCellStyle(styles.headerStyle());
            }

            createSectionRow(sheet, DATA_ROW, styles.dataStyle());
            createXmlBodyRow(sheet, XML_LINE_ROW, styles.xmlBodyStyle());
            createInputOutputSectionRow(sheet, INPUT_ROW, ExcelTreeBuilder.INPUT_SECTION, styles.dataStyle());
            createInputOutputSectionRow(sheet, OUTPUT_ROW, ExcelTreeBuilder.OUTPUT_SECTION, styles.dataStyle());

            sheet.createFreezePane(0, REPOSITORY_SUMMARY_ROW + 1);

            for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
                sheet.setColumnWidth(columnIndex, 4000);
            }
            sheet.setColumnWidth(2, 8000);
            sheet.setColumnWidth(4, 6000);
            sheet.setColumnWidth(6, 6000);

            try (OutputStream outputStream = Files.newOutputStream(outputPath)) {
                workbook.write(outputStream);
            }
        }
    }

    private static void createLabelRow(Sheet sheet, int rowIndex, String label, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            if (columnIndex == 0) {
                cell.setCellValue(label);
            }
            cell.setCellStyle(style);
        }
    }

    private static void createSqlIdRow(Sheet sheet, int rowIndex, String left, String right, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            cell.setCellStyle(style);
        }
        row.getCell(0).setCellValue(left);
        row.getCell(2).setCellValue(right);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 2, 7));
    }

    private static void createInputOutputSectionRow(Sheet sheet, int rowIndex, String sectionLabel, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            if (columnIndex == 0) {
                cell.setCellValue(sectionLabel);
            }
            cell.setCellStyle(style);
        }
    }

    private static void createSectionRow(Sheet sheet, int rowIndex, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            cell.setCellStyle(style);
        }
    }

    private static void createXmlBodyRow(Sheet sheet, int rowIndex, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            cell.setCellStyle(style);
        }
    }
}
