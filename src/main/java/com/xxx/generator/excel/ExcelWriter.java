package com.xxx.generator.excel;

import com.xxx.generator.model.MethodInfo;
import com.xxx.generator.model.RepositoryInfo;
import com.xxx.generator.model.TypeInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ExcelWriter {

    private static final int COLUMN_WIDTH_PADDING = 1024;
    private static final int MAX_COLUMN_WIDTH = 255 * 256;

    private final ExcelTreeBuilder treeBuilder = new ExcelTreeBuilder();

    public void write(Path outputPath, RepositoryInfo repository, List<TypeInfo> types) throws IOException {
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelCellStyles styles = new ExcelCellStyles(workbook);
            Map<String, TypeInfo> typeMap = treeBuilder.toTypeMap(types);
            Sheet sheet = writeRepositorySheet(workbook, styles, repository, typeMap);
            applySheetLayout(sheet);

            try (OutputStream outputStream = Files.newOutputStream(outputPath)) {
                workbook.write(outputStream);
            }
        }
    }

    private Sheet writeRepositorySheet(Workbook workbook,
                                       ExcelCellStyles styles,
                                       RepositoryInfo repository,
                                       Map<String, TypeInfo> typeMap) {
        Sheet sheet = workbook.createSheet("Repository");
        int rowIndex = 0;
        int firstHeaderRowIndex = -1;

        rowIndex = writeRepositorySection(sheet, rowIndex, repository, styles);
        fillEmptyCells(sheet.createRow(rowIndex++), styles);

        List<MethodInfo> methods = repository.methods();
        for (int methodIndex = 0; methodIndex < methods.size(); methodIndex++) {
            MethodInfo method = methods.get(methodIndex);

            rowIndex = writeMethodInfoSection(sheet, rowIndex, method, styles);

            if (firstHeaderRowIndex < 0) {
                firstHeaderRowIndex = rowIndex;
            }
            createHeader(sheet.createRow(rowIndex++), styles);

            for (ExcelTreeRow treeRow : treeBuilder.buildMethodContent(method, typeMap)) {
                writeTreeRow(sheet.createRow(rowIndex++), treeRow, styles);
            }

            if (methodIndex < methods.size() - 1) {
                fillEmptyCells(sheet.createRow(rowIndex++), styles);
            }
        }

        sheet.createFreezePane(0, firstHeaderRowIndex >= 0 ? firstHeaderRowIndex + 1 : 1);
        return sheet;
    }

    private int writeRepositorySection(Sheet sheet, int rowIndex, RepositoryInfo repository, ExcelCellStyles styles) {
        writeLabelValueRow(sheet.createRow(rowIndex++), "Repository", repository.name(), styles, false);
        writeLabelValueRow(
                sheet.createRow(rowIndex++),
                "Repository概要",
                ExcelNames.logicalName(repository.javadoc(), repository.name()),
                styles,
                false
        );
        return rowIndex;
    }

    private int writeMethodInfoSection(Sheet sheet, int rowIndex, MethodInfo method, ExcelCellStyles styles) {
        writeLabelValueRow(sheet.createRow(rowIndex++), "Method", method.name(), styles, true);
        writeLabelValueRow(
                sheet.createRow(rowIndex++),
                "Method概要",
                ExcelNames.logicalName(method.javadoc(), method.name()),
                styles,
                true
        );
        return rowIndex;
    }

    private void writeLabelValueRow(Row row, String label, String value, ExcelCellStyles styles, boolean emphasize) {
        CellStyle cellStyle = emphasize ? styles.sectionHeaderStyle() : styles.dataStyle();
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            if (columnIndex == 0) {
                cell.setCellValue(label);
            } else if (columnIndex == 2) {
                cell.setCellValue(value != null ? value : "");
            } else {
                cell.setCellValue("");
            }
            cell.setCellStyle(cellStyle);
        }
    }

    private void createHeader(Row row, ExcelCellStyles styles) {
        String[] headers = {
                "区分", "項番", "論理項目名", "物理項目名(SQL)",
                "物理項目名", "DB型", "Java型", "備考"
        };
        for (int columnIndex = 0; columnIndex < headers.length; columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            cell.setCellValue(headers[columnIndex]);
            cell.setCellStyle(styles.headerStyle());
        }
    }

    private void writeTreeRow(Row row, ExcelTreeRow treeRow, ExcelCellStyles styles) {
        writeCell(row, 0, treeRow.section(), styles);
        if (treeRow.no() > 0) {
            writeNumericCell(row, 1, treeRow.no(), styles);
        } else {
            writeCell(row, 1, "", styles);
        }
        writeCell(row, 2, treeRow.logicalName(), styles);
        writeCell(row, 3, treeRow.sqlPhysicalName(), styles);
        writeCell(row, 4, treeRow.physicalName(), styles);
        writeCell(row, 5, treeRow.dbType(), styles);
        writeCell(row, 6, treeRow.javaType(), styles);
        writeCell(row, 7, treeRow.note(), styles);
        fillEmptyCells(row, styles);
    }

    private void writeCell(Row row, int columnIndex, String value, ExcelCellStyles styles) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(styles.noteStyle(columnIndex));
    }

    private void writeNumericCell(Row row, int columnIndex, int value, ExcelCellStyles styles) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(styles.noteStyle(columnIndex));
    }

    private void fillEmptyCells(Row row, ExcelCellStyles styles) {
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            if (row.getCell(columnIndex) == null) {
                writeCell(row, columnIndex, "", styles);
            }
        }
    }

    private void applySheetLayout(Sheet sheet) {
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
            int adjustedWidth = Math.min(sheet.getColumnWidth(columnIndex) + COLUMN_WIDTH_PADDING, MAX_COLUMN_WIDTH);
            sheet.setColumnWidth(columnIndex, adjustedWidth);
        }
    }
}
