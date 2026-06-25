package com.xxx.generator.scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Path;

final class RepositoryFqcnResolver {

    private RepositoryFqcnResolver() {
    }

    static String resolve(Path javaFile) throws IOException {
        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(javaFile);
        CompilationUnit compilationUnit = parseResult.getResult()
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to parse: " + javaFile + " - " + parseResult.getProblems()));

        String className = javaFile.getFileName().toString().replace(".java", "");
        return compilationUnit.getPackageDeclaration()
                .map(packageDeclaration -> packageDeclaration.getNameAsString() + "." + className)
                .orElse(className);
    }
}
