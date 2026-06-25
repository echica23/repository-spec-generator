package com.xxx.generator.excel;

import com.xxx.generator.model.FieldInfo;
import com.xxx.generator.model.MethodInfo;
import com.xxx.generator.model.ParameterInfo;
import com.xxx.generator.model.TypeInfo;
import com.xxx.generator.parser.TypeReferenceExtractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExcelTreeBuilder {

    private static final String INDENT = "  ";

    private final TypeReferenceExtractor typeReferenceExtractor = new TypeReferenceExtractor();

    Map<String, TypeInfo> toTypeMap(List<TypeInfo> types) {
        Map<String, TypeInfo> typeMap = new LinkedHashMap<>();
        for (TypeInfo type : types) {
            typeMap.putIfAbsent(type.name(), type);
        }
        return typeMap;
    }

    List<ExcelTreeRow> buildMethodContent(MethodInfo method, Map<String, TypeInfo> typeMap) {
        List<ExcelTreeRow> rows = new ArrayList<>();

        if (!method.parameters().isEmpty()) {
            rows.add(new ExcelTreeRow("INPUT", 0, "", "", "", "", "", ""));
            int inputItemNumber = 1;
            for (ParameterInfo parameter : method.parameters()) {
                inputItemNumber = appendParameterTree(rows, parameter, 0, inputItemNumber, typeMap, new HashSet<>());
            }
        }

        if (!method.outputTypes().isEmpty()) {
            rows.add(new ExcelTreeRow("OUTPUT", 0, "", "", "", "", "", ""));
            int outputItemNumber = 1;
            for (String outputType : method.outputTypes()) {
                outputItemNumber = appendTypeTree(rows, outputType, outputType, 0, outputItemNumber, typeMap, new HashSet<>());
            }
        }

        return rows;
    }

    private int appendParameterTree(List<ExcelTreeRow> rows,
                                    ParameterInfo parameter,
                                    int depth,
                                    int itemNumber,
                                    Map<String, TypeInfo> typeMap,
                                    Set<String> visited) {
        String javaType = parameter.type();
        String resolvedTypeName = resolvePrimaryTypeName(javaType);

        if (typeReferenceExtractor.isStandardType(resolvedTypeName)) {
            rows.add(createParameterRow(0, depth, parameter, javaType));
            return itemNumber;
        }

        TypeInfo typeInfo = typeMap.get(resolvedTypeName);
        if (typeInfo == null) {
            rows.add(createParameterRow(0, depth, parameter, javaType));
            return itemNumber;
        }

        if (visited.contains(typeInfo.name())) {
            rows.add(createParameterRow(itemNumber++, depth, parameter, javaType));
            return itemNumber;
        }
        visited.add(typeInfo.name());

        rows.add(createParameterRow(itemNumber++, depth, parameter, javaType));
        for (FieldInfo field : typeInfo.fields()) {
            rows.add(createRow(itemNumber++, depth + 1, field.javadoc(), field.name(), field.type()));
            if (!typeReferenceExtractor.isStandardType(field.type())) {
                itemNumber = appendNestedFieldTypes(rows, field, depth + 2, itemNumber, typeMap, visited);
            }
        }

        visited.remove(typeInfo.name());
        return itemNumber;
    }

    private String resolvePrimaryTypeName(String typeString) {
        List<String> extracted = typeReferenceExtractor.extractFromTypeName(typeString);
        if (!extracted.isEmpty()) {
            return extracted.get(0);
        }
        String trimmed = typeString.trim();
        if (trimmed.endsWith("[]")) {
            return trimmed.substring(0, trimmed.length() - 2);
        }
        return trimmed;
    }

    private int appendTypeTree(List<ExcelTreeRow> rows,
                               String displayTypeName,
                               String javaType,
                               int depth,
                               int itemNumber,
                               Map<String, TypeInfo> typeMap,
                               Set<String> visited) {
        if (typeReferenceExtractor.isStandardType(displayTypeName)) {
            rows.add(createRow(0, depth, "", displayTypeName, javaType));
            return itemNumber;
        }

        TypeInfo typeInfo = typeMap.get(displayTypeName);
        if (typeInfo == null) {
            rows.add(createRow(0, depth, "", displayTypeName, javaType));
            return itemNumber;
        }

        if (visited.contains(typeInfo.name())) {
            rows.add(createRow(itemNumber++, depth, typeInfo.javadoc(), typeInfo.name(), javaType));
            return itemNumber;
        }
        visited.add(typeInfo.name());

        rows.add(createRow(itemNumber++, depth, typeInfo.javadoc(), typeInfo.name(), javaType));
        for (FieldInfo field : typeInfo.fields()) {
            rows.add(createRow(itemNumber++, depth + 1, field.javadoc(), field.name(), field.type()));
            if (!typeReferenceExtractor.isStandardType(field.type())) {
                itemNumber = appendNestedFieldTypes(rows, field, depth + 2, itemNumber, typeMap, visited);
            }
        }

        visited.remove(typeInfo.name());
        return itemNumber;
    }

    private int appendNestedFieldTypes(List<ExcelTreeRow> rows,
                                       FieldInfo field,
                                       int depth,
                                       int itemNumber,
                                       Map<String, TypeInfo> typeMap,
                                       Set<String> visited) {
        for (String nestedTypeName : typeReferenceExtractor.extractFromTypeName(field.type())) {
            if (typeReferenceExtractor.isStandardType(nestedTypeName)) {
                continue;
            }
            if (!typeMap.containsKey(nestedTypeName)) {
                continue;
            }
            if (visited.contains(nestedTypeName)) {
                continue;
            }
            itemNumber = appendTypeFieldsOnly(rows, typeMap.get(nestedTypeName), depth, itemNumber, typeMap, visited);
        }
        return itemNumber;
    }

    private int appendTypeFieldsOnly(List<ExcelTreeRow> rows,
                                     TypeInfo typeInfo,
                                     int depth,
                                     int itemNumber,
                                     Map<String, TypeInfo> typeMap,
                                     Set<String> visited) {
        visited.add(typeInfo.name());
        for (FieldInfo field : typeInfo.fields()) {
            rows.add(createRow(itemNumber++, depth, field.javadoc(), field.name(), field.type()));
            if (!typeReferenceExtractor.isStandardType(field.type())) {
                itemNumber = appendNestedFieldTypes(rows, field, depth + 1, itemNumber, typeMap, visited);
            }
        }
        visited.remove(typeInfo.name());
        return itemNumber;
    }

    private ExcelTreeRow createParameterRow(int no,
                                            int depth,
                                            ParameterInfo parameter,
                                            String javaType) {
        String logical = parameterLogicalName(parameter);
        return new ExcelTreeRow(
                "",
                no,
                indent(logical, depth),
                "",
                indent(parameter.name(), depth),
                "",
                javaType,
                ""
        );
    }

    private String parameterLogicalName(ParameterInfo parameter) {
        if (parameter.javadoc() != null && !parameter.javadoc().isBlank()) {
            return parameter.javadoc();
        }
        return parameter.name();
    }

    private ExcelTreeRow createRow(int no,
                                   int depth,
                                   String javadoc,
                                   String physicalName,
                                   String javaType) {
        String logical = ExcelNames.logicalName(javadoc, physicalName);
        return new ExcelTreeRow(
                "",
                no,
                indent(logical, depth),
                "",
                indent(physicalName, depth),
                "",
                javaType,
                ""
        );
    }

    private String indent(String text, int depth) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return INDENT.repeat(Math.max(0, depth)) + text;
    }
}
