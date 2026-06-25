package com.xxx.generator.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlResolverTest {

    private final XmlResolver xmlResolver = new XmlResolver();

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void toXmlPath_convertsWindowsAbsolutePath() {
        Path javaPath = Path.of(
                "C:",
                "Users",
                "user",
                "project",
                "src",
                "main",
                "java",
                "com",
                "xxx",
                "BookRepository.java"
        );
        Path expected = Path.of(
                "C:",
                "Users",
                "user",
                "project",
                "src",
                "main",
                "resources",
                "com",
                "xxx",
                "BookRepository.xml"
        );

        Optional<Path> actual = xmlResolver.toXmlPath(javaPath);

        assertEquals(expected, actual.orElseThrow());
    }

    @Test
    void toXmlPath_convertsRelativePath() {
        Path javaPath = Path.of("src", "main", "java", "com", "example", "repository", "BookRepository.java");
        Path expected = Path.of("src", "main", "resources", "com", "example", "repository", "BookRepository.xml");

        Optional<Path> actual = xmlResolver.toXmlPath(javaPath);

        assertEquals(expected, actual.orElseThrow());
    }

    @Test
    void toXmlPath_returnsEmptyWhenJavaSegmentIsMissing() {
        Path javaPath = Path.of("src", "main", "com", "example", "BookRepository.java");

        Optional<Path> actual = xmlResolver.toXmlPath(javaPath);

        assertTrue(actual.isEmpty());
    }
}
