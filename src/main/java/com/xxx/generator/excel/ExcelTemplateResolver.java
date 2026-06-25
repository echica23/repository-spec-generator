package com.xxx.generator.excel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExcelTemplateResolver {

    private static final Path DEFAULT_TEMPLATE = Path.of("template/default.xlsx");

    private ExcelTemplateResolver() {
    }

    public static Path resolve(String templateOption) throws IOException {
        if (templateOption != null && !templateOption.isBlank()) {
            Path templatePath = Path.of(templateOption);
            if (!Files.isRegularFile(templatePath)) {
                throw new IOException("Template file not found: " + templatePath.toAbsolutePath());
            }
            return templatePath;
        }

        if (Files.isRegularFile(DEFAULT_TEMPLATE)) {
            return DEFAULT_TEMPLATE;
        }

        return null;
    }
}
