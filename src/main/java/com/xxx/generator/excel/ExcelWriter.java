package com.xxx.generator.excel;

import com.xxx.generator.model.MethodInfo;
import com.xxx.generator.model.RepositoryInfo;
import com.xxx.generator.model.TypeInfo;
import com.xxx.generator.model.XmlResource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExcelWriter {

    private static final int TOP_MARGIN_ROWS = 7;
    private static final int COLUMN_WIDTH_PADDING = 1024;
    private static final int MAX_COLUMN_WIDTH = 255 * 256;

    private static final String XML_NOT_FOUND_MESSAGE = "XMLファイルが存在しません";

    private final ExcelTreeBuilder treeBuilder = new ExcelTreeBuilder();

    public void write(Path outputPath,
                      RepositoryInfo repository,
                      List<TypeInfo> types,
                      Optional<XmlResource> xmlResource,
                      Path templatePath) throws IOException {
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }

        boolean templateMode = templatePath != null;
        try (Workbook workbook = openWorkbook(templatePath)) {
            ExcelCellStyles styles = templateMode
                    ? ExcelCellStyles.fromTemplate(workbook.getSheetAt(0))
                    : ExcelCellStyles.createProgrammatic(workbook);
            TemplateStyleCache styleCache = new TemplateStyleCache(workbook);
            Map<String, TypeInfo> typeMap = treeBuilder.toTypeMap(types);
            Sheet sheet = writeRepositorySheet(workbook, styles, styleCache, repository, typeMap, xmlResource, templateMode);
            if (!templateMode) {
                applySheetLayout(sheet);
            }

            try (OutputStream outputStream = Files.newOutputStream(outputPath)) {
                workbook.write(outputStream);
            }
        }
    }

    private Workbook openWorkbook(Path templatePath) throws IOException {
        if (templatePath == null) {
            return new XSSFWorkbook();
        }
        try (InputStream inputStream = Files.newInputStream(templatePath)) {
            return WorkbookFactory.create(inputStream);
        }
    }

    private Sheet writeRepositorySheet(Workbook workbook,
                                       ExcelCellStyles styles,
                                       TemplateStyleCache styleCache,
                                       RepositoryInfo repository,
                                       Map<String, TypeInfo> typeMap,
                                       Optional<XmlResource> xmlResource,
                                       boolean templateMode) {
        Sheet sheet = templateMode ? workbook.getSheetAt(0) : workbook.createSheet(DefaultTemplateGenerator.SHEET_NAME);
        int rowIndex = 0;
        int repositorySummaryRowIndex = -1;

        for (int i = 0; i < TOP_MARGIN_ROWS; i++) {
            prepareRow(sheet, rowIndex++, TemplateRowKind.MARGIN, styles, styleCache);
        }

        rowIndex = writeRepositorySection(sheet, rowIndex, repository, styles, styleCache);
        repositorySummaryRowIndex = rowIndex - 1;
        prepareRow(sheet, rowIndex++, TemplateRowKind.BLANK, styles, styleCache);

        List<MethodInfo> methods = repository.methods();
        for (int methodIndex = 0; methodIndex < methods.size(); methodIndex++) {
            MethodInfo method = methods.get(methodIndex);

            rowIndex = writeMethodInfoSection(sheet, rowIndex, method, styles, styleCache);
            writeHeaderRow(sheet, rowIndex++, styles, styleCache);

            for (ExcelTreeRow treeRow : treeBuilder.buildMethodContent(method, typeMap)) {
                writeTreeRow(sheet, rowIndex++, treeRow, styles, styleCache);
            }

            if (methodIndex < methods.size() - 1) {
                prepareRow(sheet, rowIndex++, TemplateRowKind.BLANK, styles, styleCache);
            }
        }

        writeXmlSection(sheet, rowIndex, xmlResource, styles, styleCache);

        if (!templateMode) {
            sheet.createFreezePane(0, repositorySummaryRowIndex >= 0
                    ? repositorySummaryRowIndex + 1
                    : TOP_MARGIN_ROWS + 1);
        }
        return sheet;
    }

    private int writeXmlSection(Sheet sheet,
                                int rowIndex,
                                Optional<XmlResource> xmlResource,
                                ExcelCellStyles styles,
                                TemplateStyleCache styleCache) {
        prepareRow(sheet, rowIndex++, TemplateRowKind.BLANK, styles, styleCache);

        if (xmlResource.isEmpty() || xmlResource.get().content().isBlank()) {
            writeLabelValueRow(sheet, rowIndex++, "XML", XML_NOT_FOUND_MESSAGE, TemplateRowKind.XML_HEADER, styles, styleCache);
            return rowIndex;
        }

        XmlResource xml = xmlResource.get();
        writeLabelValueRow(sheet, rowIndex++, "XML", xml.fileName(), TemplateRowKind.XML_HEADER, styles, styleCache);

        for (String line : xml.content().lines().toList()) {
            writeXmlLineRow(sheet, rowIndex++, line, styles);
        }

        return rowIndex;
    }

    private void writeXmlLineRow(Sheet sheet,
                                 int rowIndex,
                                 String line,
                                 ExcelCellStyles styles) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        CellStyle xmlBodyStyle = styles.xmlBodyStyle();
        Cell cell = row.getCell(0);
        if (cell == null) {
            cell = row.createCell(0);
        }
        cell.setCellStyle(xmlBodyStyle);
        cell.setCellValue(line != null ? line : "");
    }

    private int writeRepositorySection(Sheet sheet,
                                       int rowIndex,
                                       RepositoryInfo repository,
                                       ExcelCellStyles styles,
                                       TemplateStyleCache styleCache) {
        writeLabelValueRow(sheet, rowIndex++, "Repository", repository.name(), TemplateRowKind.REPOSITORY, styles, styleCache);
        writeLabelValueRow(
                sheet,
                rowIndex++,
                "Repository概要",
                ExcelNames.logicalName(repository.javadoc(), repository.name()),
                TemplateRowKind.REPOSITORY_SUMMARY,
                styles,
                styleCache
        );
        return rowIndex;
    }

    private int writeMethodInfoSection(Sheet sheet,
                                       int rowIndex,
                                       MethodInfo method,
                                       ExcelCellStyles styles,
                                       TemplateStyleCache styleCache) {
        writeLabelValueRow(sheet, rowIndex++, "Method", method.name(), TemplateRowKind.METHOD, styles, styleCache);
        writeLabelValueRow(
                sheet,
                rowIndex++,
                "Method概要",
                ExcelNames.logicalName(method.javadoc(), method.name()),
                TemplateRowKind.METHOD_SUMMARY,
                styles,
                styleCache
        );
        return rowIndex;
    }

    private void writeLabelValueRow(Sheet sheet,
                                    int rowIndex,
                                    String label,
                                    String value,
                                    TemplateRowKind kind,
                                    ExcelCellStyles styles,
                                    TemplateStyleCache styleCache) {
        Row row = prepareRow(sheet, rowIndex, kind, styles, styleCache);
        ExcelRowAccessor.setStringValue(row, 0, label);
        ExcelRowAccessor.setStringValue(row, 1, "");
        ExcelRowAccessor.setStringValue(row, 2, value != null ? value : "");
        for (int columnIndex = 3; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            if (row.getCell(columnIndex) == null) {
                ExcelRowAccessor.setStringValue(row, columnIndex, "");
            }
        }
    }

    private void writeHeaderRow(Sheet sheet, int rowIndex, ExcelCellStyles styles, TemplateStyleCache styleCache) {
        Row row = prepareRow(sheet, rowIndex, TemplateRowKind.HEADER, styles, styleCache);
        String[] headers = {
                "区分", "項番", "論理項目名", "物理項目名(SQL)",
                "物理項目名", "DB型", "Java型", "備考"
        };
        for (int columnIndex = 0; columnIndex < headers.length; columnIndex++) {
            ExcelRowAccessor.setStringValue(row, columnIndex, headers[columnIndex]);
        }
    }

    private void writeTreeRow(Sheet sheet,
                              int rowIndex,
                              ExcelTreeRow treeRow,
                              ExcelCellStyles styles,
                              TemplateStyleCache styleCache) {
        TemplateRowKind kind = resolveTreeRowKind(treeRow);
        Row row = prepareRow(sheet, rowIndex, kind, styles, styleCache);
        ExcelRowAccessor.setStringValue(row, 0, treeRow.section());
        if (treeRow.no() > 0) {
            ExcelRowAccessor.setNumericValue(row, 1, treeRow.no());
        } else {
            ExcelRowAccessor.setStringValue(row, 1, "");
        }
        ExcelRowAccessor.setStringValue(row, 2, treeRow.logicalName());
        ExcelRowAccessor.setStringValue(row, 3, treeRow.sqlPhysicalName());
        ExcelRowAccessor.setStringValue(row, 4, treeRow.physicalName());
        ExcelRowAccessor.setStringValue(row, 5, treeRow.dbType());
        ExcelRowAccessor.setStringValue(row, 6, treeRow.javaType());
        ExcelRowAccessor.setStringValue(row, 7, treeRow.note());
        fillMissingCells(row);
    }

    private TemplateRowKind resolveTreeRowKind(ExcelTreeRow treeRow) {
        if ("INPUT".equals(treeRow.section())) {
            return TemplateRowKind.INPUT;
        }
        if ("OUTPUT".equals(treeRow.section())) {
            return TemplateRowKind.OUTPUT;
        }
        return TemplateRowKind.DATA;
    }

    private Row prepareRow(Sheet sheet,
                           int rowIndex,
                           TemplateRowKind kind,
                           ExcelCellStyles styles,
                           TemplateStyleCache styleCache) {
        return ExcelRowAccessor.prepareRow(sheet, rowIndex, kind, styles, styleCache);
    }

    private void fillMissingCells(Row row) {
        for (int columnIndex = 0; columnIndex < ExcelCellStyles.COLUMN_COUNT; columnIndex++) {
            if (row.getCell(columnIndex) == null) {
                ExcelRowAccessor.setStringValue(row, columnIndex, "");
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
