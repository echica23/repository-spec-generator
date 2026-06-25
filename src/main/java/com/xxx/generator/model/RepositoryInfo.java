package com.xxx.generator.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record RepositoryInfo(
        String name,
        String javadoc,
        List<MethodInfo> methods,
        Map<String, String> imports,
        Set<String> referencedTypes
) {
}
