package com.xxx.generator.parser;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * JavaParser の Type AST から解析対象の型名を抽出する。
 */
public class TypeReferenceExtractor {

    private static final Set<String> SUPPORTED_GENERIC_WRAPPERS = Set.of("List", "Optional");

    public static final Set<String> BUILTIN_TYPES = Set.of(
            "String", "Integer", "Long", "BigDecimal", "LocalDate", "LocalDateTime",
            "OffsetDateTime", "Date", "UUID"
    );

    public Set<String> extract(Type type) {
        Set<String> typeNames = new LinkedHashSet<>();
        collect(type, typeNames);
        return typeNames;
    }

    public List<String> extractFromTypeName(String typeName) {
        Set<String> typeNames = new LinkedHashSet<>();
        collectFromTypeName(typeName, typeNames);
        return new ArrayList<>(typeNames);
    }

    public boolean isBuiltinType(String typeName) {
        return isStandardType(typeName);
    }

    public boolean isStandardType(String typeName) {
        if (typeName == null || typeName.isBlank()) {
            return false;
        }
        if (BUILTIN_TYPES.contains(typeName)) {
            return true;
        }
        return STANDARD_PRIMITIVE_TYPES.contains(typeName);
    }

    private static final Set<String> STANDARD_PRIMITIVE_TYPES = Set.of(
            "int", "long", "boolean", "void", "double", "float", "short", "byte", "char"
    );

    private void collect(Type type, Set<String> typeNames) {
        if (!(type instanceof ClassOrInterfaceType classOrInterfaceType)) {
            return;
        }

        String name = classOrInterfaceType.getNameAsString();
        if (SUPPORTED_GENERIC_WRAPPERS.contains(name)) {
            classOrInterfaceType.getTypeArguments()
                    .ifPresent(typeArguments -> typeArguments.forEach(argument -> collect(argument, typeNames)));
            return;
        }

        if (isReferencedTypeName(name)) {
            typeNames.add(name);
        }
    }

    private void collectFromTypeName(String typeName, Set<String> typeNames) {
        if (typeName == null || typeName.isBlank() || "void".equals(typeName)) {
            return;
        }

        for (String wrapper : SUPPORTED_GENERIC_WRAPPERS) {
            String prefix = wrapper + "<";
            if (typeName.startsWith(prefix) && typeName.endsWith(">")) {
                collectFromTypeName(typeName.substring(prefix.length(), typeName.length() - 1), typeNames);
                return;
            }
        }

        if (isReferencedTypeName(typeName)) {
            typeNames.add(typeName);
        }
    }

    private boolean isReferencedTypeName(String name) {
        if (name == null || name.isBlank() || "void".equals(name)) {
            return false;
        }
        if (!name.matches("[A-Z][A-Za-z0-9]*")) {
            return false;
        }
        return !name.endsWith("Repository");
    }
}
