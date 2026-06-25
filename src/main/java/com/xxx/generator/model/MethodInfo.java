package com.xxx.generator.model;

import java.util.List;
import java.util.Set;

public record MethodInfo(
        String name,
        String javadoc,
        String returnType,
        List<ParameterInfo> parameters,
        List<String> inputTypes,
        List<String> outputTypes
) {
}
