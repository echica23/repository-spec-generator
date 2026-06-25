package com.xxx.generator.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.type.Type;
import com.xxx.generator.model.MethodInfo;
import com.xxx.generator.model.ParameterInfo;
import com.xxx.generator.model.RepositoryInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaSourceParser {

    private final TypeReferenceExtractor typeReferenceExtractor = new TypeReferenceExtractor();

    public RepositoryInfo parse(Path sourceFile) throws IOException {
        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(sourceFile);
        CompilationUnit compilationUnit = parseResult.getResult()
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to parse: " + sourceFile + " - " + parseResult.getProblems()));

        String typeName = sourceFile.getFileName().toString().replace(".java", "");
        ClassOrInterfaceDeclaration declaration = compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class, type -> type.getNameAsString().equals(typeName))
                .orElseThrow(() -> new IllegalStateException("Type not found: " + typeName));

        return toRepositoryInfo(compilationUnit, declaration);
    }

    private RepositoryInfo toRepositoryInfo(CompilationUnit compilationUnit,
                                            ClassOrInterfaceDeclaration declaration) {
        Set<String> referencedTypes = new LinkedHashSet<>();
        List<MethodInfo> methods = declaration.getMethods().stream()
                .map(method -> toMethodInfo(method, referencedTypes))
                .toList();

        return new RepositoryInfo(
                declaration.getNameAsString(),
                extractJavadocDescription(declaration),
                methods,
                parseImports(compilationUnit),
                referencedTypes
        );
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

    private MethodInfo toMethodInfo(MethodDeclaration method, Set<String> referencedTypes) {
        referencedTypes.addAll(typeReferenceExtractor.extract(method.getType()));
        method.getParameters().forEach(parameter ->
                referencedTypes.addAll(typeReferenceExtractor.extract(parameter.getType())));

        List<String> inputTypes = new ArrayList<>();
        method.getParameters().forEach(parameter ->
                inputTypes.addAll(extractTypeNames(parameter.getType())));

        List<ParameterInfo> parameters = method.getParameters().stream()
                .map(this::toParameterInfo)
                .toList();

        return new MethodInfo(
                method.getNameAsString(),
                extractJavadocDescription(method),
                method.getType().asString(),
                parameters,
                inputTypes,
                extractTypeNames(method.getType())
        );
    }

    private List<String> extractTypeNames(Type type) {
        Set<String> typeNames = typeReferenceExtractor.extract(type);
        if (!typeNames.isEmpty()) {
            return new ArrayList<>(typeNames);
        }

        String typeName = type.asString();
        if (typeName.isBlank() || "void".equals(typeName)) {
            return List.of();
        }

        List<String> extracted = typeReferenceExtractor.extractFromTypeName(typeName);
        if (!extracted.isEmpty()) {
            return extracted;
        }

        return List.of(typeName);
    }

    private ParameterInfo toParameterInfo(Parameter parameter) {
        return new ParameterInfo(
                parameter.getNameAsString(),
                parameter.getType().asString()
        );
    }

    private String extractJavadocDescription(ClassOrInterfaceDeclaration node) {
        return node.getJavadoc()
                .map(javadoc -> javadoc.getDescription().toText())
                .orElse("");
    }

    private String extractJavadocDescription(MethodDeclaration node) {
        return node.getJavadoc()
                .map(javadoc -> javadoc.getDescription().toText())
                .orElse("");
    }
}
