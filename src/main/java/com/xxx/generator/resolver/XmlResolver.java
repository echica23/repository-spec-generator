package com.xxx.generator.resolver;

import com.xxx.generator.model.XmlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Repository の Java ファイルパスから対応する MyBatis XML を読み込む。
 * <p>
 * {@code src/main/java/.../BookRepository.java}
 * → {@code src/main/resources/.../BookRepository.xml}
 */
public class XmlResolver {

    public Optional<XmlResource> resolve(Path repositoryJavaFile) throws IOException {
        Optional<Path> xmlPath = toXmlPath(repositoryJavaFile);
        if (xmlPath.isEmpty() || !Files.isRegularFile(xmlPath.get())) {
            return Optional.empty();
        }
        Path path = xmlPath.get();
        return Optional.of(new XmlResource(path.getFileName().toString(), Files.readString(path)));
    }

    Optional<Path> toXmlPath(Path repositoryJavaFile) {
        if (repositoryJavaFile == null) {
            return Optional.empty();
        }

        Path normalized = repositoryJavaFile.normalize();
        String fileName = normalized.getFileName().toString();
        if (!fileName.endsWith(".java")) {
            return Optional.empty();
        }

        Path root = normalized.getRoot();
        int nameCount = normalized.getNameCount();
        if (nameCount < 1) {
            return Optional.empty();
        }

        Path result = root != null ? root : Path.of("");
        boolean javaReplaced = false;

        for (int i = 0; i < nameCount - 1; i++) {
            String segment = normalized.getName(i).toString();
            if ("java".equals(segment)) {
                result = result.resolve("resources");
                javaReplaced = true;
            } else {
                result = result.resolve(segment);
            }
        }

        if (!javaReplaced) {
            return Optional.empty();
        }

        String xmlFileName = fileName.substring(0, fileName.length() - ".java".length()) + ".xml";
        return Optional.of(result.resolve(xmlFileName));
    }
}
