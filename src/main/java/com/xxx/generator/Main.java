package com.xxx.generator;

import com.xxx.generator.excel.ExcelWriter;
import com.xxx.generator.model.MethodInfo;
import com.xxx.generator.model.ParameterInfo;
import com.xxx.generator.model.RepositoryInfo;
import com.xxx.generator.model.TypeInfo;
import com.xxx.generator.model.XmlResource;
import com.xxx.generator.parser.JavaSourceParser;
import com.xxx.generator.resolver.TypeResolver;
import com.xxx.generator.resolver.XmlResolver;
import com.xxx.generator.scanner.RepositoryScanner;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class Main {

    private static final String DEFAULT_OUTPUT_DIR = "output";

    public static void main(String[] args) throws Exception {
        CliOptions options = CliOptions.parse(args);
        Path srcRoot = Path.of(options.srcRoot());
        Path outputDir = Path.of(options.out() != null ? options.out() : DEFAULT_OUTPUT_DIR);

        RepositoryScanner scanner = new RepositoryScanner();
        JavaSourceParser parser = new JavaSourceParser();
        TypeResolver typeResolver = new TypeResolver();
        XmlResolver xmlResolver = new XmlResolver();
        ExcelWriter excelWriter = new ExcelWriter();

        List<Path> repositoryPaths = scanner.scan(srcRoot, options.repository());
        for (Path repositoryPath : repositoryPaths) {
            RepositoryInfo repositoryInfo = parser.parse(repositoryPath);
            printRepository(repositoryInfo);

            List<TypeInfo> typeInfos = typeResolver.resolveAll(srcRoot, repositoryInfo);
            for (TypeInfo typeInfo : typeInfos) {
                printResolvedType(typeInfo);
            }

            for (String typeName : repositoryInfo.referencedTypes()) {
                if (typeInfos.stream().noneMatch(type -> type.name().equals(typeName))) {
                    printUnresolvedType(typeName);
                }
            }

            Path outputPath = outputDir.resolve(repositoryInfo.name() + ".xlsx");
            Optional<XmlResource> xmlResource = xmlResolver.resolve(repositoryPath);
            excelWriter.write(outputPath, repositoryInfo, typeInfos, xmlResource);
            System.out.println("Excel output: " + outputPath.toAbsolutePath());
        }
    }

    private static void printRepository(RepositoryInfo repository) {
        System.out.println("Repository: " + repository.name());
        System.out.println("Javadoc: " + repository.javadoc());
        System.out.println("Imports:");
        repository.imports().forEach((simpleName, fullyQualifiedName) ->
                System.out.println("  " + simpleName + " -> " + fullyQualifiedName));
        System.out.println("Methods:");

        for (MethodInfo method : repository.methods()) {
            System.out.println("  - " + formatMethodSignature(method));
            System.out.println("    Javadoc: " + method.javadoc());
        }

        System.out.println();
    }

    private static void printResolvedType(TypeInfo type) {
        System.out.println("Type: " + type.name());
        System.out.println("Javadoc: " + type.javadoc());
        System.out.println("Fields:");

        type.fields().forEach(field -> {
            System.out.println("  - " + field.name() + ": " + field.type());
            System.out.println("    Javadoc: " + field.javadoc());
        });

        System.out.println();
    }

    private static void printUnresolvedType(String typeName) {
        System.out.println("Type: " + typeName);
        System.out.println();
    }

    private static String formatMethodSignature(MethodInfo method) {
        String parameters = method.parameters().stream()
                .map(Main::formatParameter)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");

        return method.name() + "(" + parameters + "): " + method.returnType();
    }

    private static String formatParameter(ParameterInfo parameter) {
        return parameter.name() + ": " + parameter.type();
    }

    record CliOptions(String srcRoot, String out, String repository, String template) {

        static CliOptions parse(String[] args) {
            String srcRoot = null;
            String out = null;
            String repository = null;
            String template = null;

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--srcRoot" -> srcRoot = requireValue(args, ++i, "--srcRoot");
                    case "--out" -> out = requireValue(args, ++i, "--out");
                    case "--repository" -> repository = requireValue(args, ++i, "--repository");
                    case "--template" -> template = requireValue(args, ++i, "--template");
                    default -> throw new IllegalArgumentException("Unknown option: " + args[i]);
                }
            }

            if (srcRoot == null) {
                throw new IllegalArgumentException("Required option: --srcRoot");
            }

            return new CliOptions(srcRoot, out, repository, template);
        }

        private static String requireValue(String[] args, int index, String option) {
            if (index >= args.length) {
                throw new IllegalArgumentException("Missing value for option: " + option);
            }
            return args[index];
        }
    }
}
