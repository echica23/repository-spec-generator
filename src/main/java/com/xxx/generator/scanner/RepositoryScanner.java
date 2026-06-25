package com.xxx.generator.scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RepositoryScanner {

    private static final String REPOSITORY_FILE_SUFFIX = "Repository.java";

    public List<Path> scan(Path srcRoot, String repositorySpecifier) throws IOException {
        List<Path> allRepositories = listRepositoryFiles(srcRoot);

        if (repositorySpecifier == null || repositorySpecifier.isBlank()) {
            return allRepositories;
        }

        if (repositorySpecifier.contains(".")) {
            return resolveByFullyQualifiedName(allRepositories, repositorySpecifier);
        }

        return resolveBySimpleName(allRepositories, repositorySpecifier);
    }

    private List<Path> listRepositoryFiles(Path srcRoot) throws IOException {
        try (Stream<Path> paths = Files.walk(srcRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(REPOSITORY_FILE_SUFFIX))
                    .sorted()
                    .toList();
        }
    }

    private List<Path> resolveByFullyQualifiedName(List<Path> allRepositories, String fullyQualifiedName)
            throws IOException {
        List<Path> matched = new ArrayList<>();
        for (Path path : allRepositories) {
            if (fullyQualifiedName.equals(RepositoryFqcnResolver.resolve(path))) {
                matched.add(path);
            }
        }

        if (matched.isEmpty()) {
            throw new IllegalArgumentException("Repository not found: " + fullyQualifiedName);
        }

        return List.of(matched.get(0));
    }

    private List<Path> resolveBySimpleName(List<Path> allRepositories, String repositorySpecifier)
            throws IOException {
        String expectedName = toSimpleRepositoryName(repositorySpecifier);
        List<Path> matched = allRepositories.stream()
                .filter(path -> expectedName.equals(simpleName(path)))
                .toList();

        if (matched.isEmpty()) {
            throw new IllegalArgumentException("Repository not found: " + repositorySpecifier);
        }
        if (matched.size() == 1) {
            return matched;
        }

        StringBuilder message = new StringBuilder();
        message.append("Multiple repositories found for '")
                .append(repositorySpecifier)
                .append("'. Specify the fully qualified name:")
                .append(System.lineSeparator());
        for (Path path : matched) {
            message.append("  ")
                    .append(RepositoryFqcnResolver.resolve(path))
                    .append(System.lineSeparator());
        }
        throw new IllegalArgumentException(message.toString().trim());
    }

    private String toSimpleRepositoryName(String repositorySpecifier) {
        return repositorySpecifier.endsWith("Repository")
                ? repositorySpecifier
                : repositorySpecifier + "Repository";
    }

    private String simpleName(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.length() - ".java".length());
    }
}
