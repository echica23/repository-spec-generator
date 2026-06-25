package com.xxx.generator.scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class RepositoryScanner {

    private static final String REPOSITORY_FILE_SUFFIX = "Repository.java";

    public List<Path> scan(Path srcRoot, String repositoryName) throws IOException {
        try (Stream<Path> paths = Files.walk(srcRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(REPOSITORY_FILE_SUFFIX))
                    .filter(path -> matchesRepository(path, repositoryName))
                    .sorted()
                    .toList();
        }
    }

    private boolean matchesRepository(Path path, String repositoryName) {
        if (repositoryName == null || repositoryName.isBlank()) {
            return true;
        }

        String fileName = path.getFileName().toString();
        String baseName = fileName.substring(0, fileName.length() - ".java".length());
        String expectedName = repositoryName.endsWith("Repository")
                ? repositoryName
                : repositoryName + "Repository";
        return baseName.equals(expectedName);
    }
}
