package com.xxx.generator.resolver;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.xxx.generator.model.FieldInfo;
import com.xxx.generator.model.RepositoryInfo;
import com.xxx.generator.model.TypeInfo;
import com.xxx.generator.parser.TypeReferenceExtractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class TypeResolver {

    private static final Set<String> BUILTIN_TYPES = Set.of(
            "String", "Integer", "Long", "BigDecimal", "LocalDate", "LocalDateTime",
            "OffsetDateTime", "Date", "UUID"
    );

    private final Map<String, Optional<TypeInfo>> cache = new LinkedHashMap<>();
    private final TypeReferenceExtractor typeReferenceExtractor = new TypeReferenceExtractor();

    public List<TypeInfo> resolveAll(Path srcRoot, RepositoryInfo repositoryInfo) throws IOException {
        Set<String> visited = new LinkedHashSet<>();
        List<TypeInfo> results = new ArrayList<>();

        for (String typeName : repositoryInfo.referencedTypes()) {
            if (!shouldResolve(typeName)) {
                continue;
            }
            resolveRecursively(srcRoot, typeName, repositoryInfo.imports(), visited, results);
        }

        return results;
    }

    public Optional<TypeInfo> resolve(Path srcRoot, String typeName, RepositoryInfo repositoryInfo) throws IOException {
        return resolve(srcRoot, typeName, repositoryInfo.imports());
    }

    private Optional<TypeInfo> resolve(Path srcRoot, String typeName, Map<String, String> imports) throws IOException {
        String cacheKey = toCacheKey(typeName, imports);
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        Optional<TypeInfo> result = resolveWithoutCache(srcRoot, typeName, imports);
        cache.put(cacheKey, result);
        return result;
    }

    private void resolveRecursively(Path srcRoot,
                                    String typeName,
                                    Map<String, String> imports,
                                    Set<String> visited,
                                    List<TypeInfo> results) throws IOException {
        String cacheKey = toCacheKey(typeName, imports);
        if (visited.contains(cacheKey)) {
            return;
        }
        visited.add(cacheKey);

        Optional<TypeInfo> typeInfo = resolve(srcRoot, typeName, imports);
        if (typeInfo.isEmpty()) {
            return;
        }

        results.add(typeInfo.get());

        Optional<TypeParseContext> typeParseContext = loadTypeParseContext(srcRoot, typeName, imports);
        if (typeParseContext.isEmpty()) {
            return;
        }

        for (String nestedTypeName : typeParseContext.get().nestedTypeNames()) {
            resolveRecursively(
                    srcRoot,
                    nestedTypeName,
                    typeParseContext.get().imports(),
                    visited,
                    results
            );
        }
    }

    private Optional<TypeInfo> resolveWithoutCache(Path srcRoot, String typeName, Map<String, String> imports)
            throws IOException {
        Optional<Path> sourceFile = findSourceFile(srcRoot, typeName, imports);
        if (sourceFile.isEmpty()) {
            return Optional.empty();
        }
        return parseType(sourceFile.get(), typeName);
    }

    private Optional<TypeParseContext> loadTypeParseContext(Path srcRoot, String typeName, Map<String, String> imports)
            throws IOException {
        Optional<Path> sourceFile = findSourceFile(srcRoot, typeName, imports);
        if (sourceFile.isEmpty()) {
            return Optional.empty();
        }

        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(sourceFile.get());
        CompilationUnit compilationUnit = parseResult.getResult()
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to parse: " + sourceFile.get() + " - " + parseResult.getProblems()));

        Optional<ClassOrInterfaceDeclaration> declaration = compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class, type -> type.getNameAsString().equals(typeName))
                .filter(type -> !type.isInterface());
        if (declaration.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new TypeParseContext(
                parseImports(compilationUnit),
                extractNestedTypeNames(declaration.get())
        ));
    }

    private Set<String> extractNestedTypeNames(ClassOrInterfaceDeclaration declaration) {
        Set<String> nestedTypeNames = new LinkedHashSet<>();
        for (FieldDeclaration fieldDeclaration : declaration.getFields()) {
            for (VariableDeclarator variable : fieldDeclaration.getVariables()) {
                typeReferenceExtractor.extract(variable.getType()).stream()
                        .filter(this::shouldResolve)
                        .forEach(nestedTypeNames::add);
            }
        }
        return nestedTypeNames;
    }

    private Map<String, String> parseImports(CompilationUnit compilationUnit) {
        Map<String, String> imports = new LinkedHashMap<>();
        for (ImportDeclaration importDeclaration : compilationUnit.getImports()) {
            if (importDeclaration.isStatic() || importDeclaration.isAsterisk()) {
                continue;
            }
            String fullyQualifiedName = importDeclaration.getNameAsString();
            int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
            if (lastDotIndex < 0) {
                continue;
            }
            String simpleName = fullyQualifiedName.substring(lastDotIndex + 1);
            imports.put(simpleName, fullyQualifiedName);
        }
        return imports;
    }

    private boolean shouldResolve(String typeName) {
        if (BUILTIN_TYPES.contains(typeName)) {
            return false;
        }
        if (typeName == null || typeName.isBlank() || "void".equals(typeName)) {
            return false;
        }
        if (!typeName.matches("[A-Z][A-Za-z0-9]*")) {
            return false;
        }
        return !typeName.endsWith("Repository");
    }

    private String toCacheKey(String typeName, Map<String, String> imports) {
        return imports.getOrDefault(typeName, typeName);
    }

    Optional<Path> findSourceFile(Path srcRoot, String typeName, Map<String, String> imports) throws IOException {
        Optional<Path> sourceFileByImport = findSourceFileByImport(srcRoot, typeName, imports);
        if (sourceFileByImport.isPresent()) {
            return sourceFileByImport;
        }
        return findSourceFileByFileName(srcRoot, typeName);
    }

    private Optional<Path> findSourceFileByImport(Path srcRoot, String typeName, Map<String, String> imports) {
        String fullyQualifiedName = imports.get(typeName);
        if (fullyQualifiedName == null) {
            return Optional.empty();
        }
        Path sourceFile = srcRoot.resolve(fullyQualifiedName.replace('.', '/') + ".java");
        if (Files.isRegularFile(sourceFile)) {
            return Optional.of(sourceFile);
        }
        return Optional.empty();
    }

    private Optional<Path> findSourceFileByFileName(Path srcRoot, String typeName) throws IOException {
        String fileName = typeName + ".java";
        try (Stream<Path> paths = Files.walk(srcRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(fileName))
                    .findFirst();
        }
    }

    private Optional<TypeInfo> parseType(Path sourceFile, String typeName) throws IOException {
        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(sourceFile);
        CompilationUnit compilationUnit = parseResult.getResult()
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to parse: " + sourceFile + " - " + parseResult.getProblems()));

        return compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class, type -> type.getNameAsString().equals(typeName))
                .filter(declaration -> !declaration.isInterface())
                .map(this::toTypeInfo);
    }

    private TypeInfo toTypeInfo(ClassOrInterfaceDeclaration declaration) {
        List<FieldInfo> fields = new ArrayList<>();
        for (FieldDeclaration fieldDeclaration : declaration.getFields()) {
            String javadoc = extractJavadocDescription(fieldDeclaration);
            for (VariableDeclarator variable : fieldDeclaration.getVariables()) {
                fields.add(new FieldInfo(
                        variable.getNameAsString(),
                        variable.getType().asString(),
                        javadoc
                ));
            }
        }

        return new TypeInfo(
                declaration.getNameAsString(),
                extractJavadocDescription(declaration),
                fields
        );
    }

    private String extractJavadocDescription(ClassOrInterfaceDeclaration node) {
        return node.getJavadoc()
                .map(javadoc -> javadoc.getDescription().toText())
                .orElse("");
    }

    private String extractJavadocDescription(FieldDeclaration node) {
        return node.getJavadoc()
                .map(javadoc -> javadoc.getDescription().toText())
                .orElse("");
    }

    private record TypeParseContext(Map<String, String> imports, Set<String> nestedTypeNames) {
    }
}
