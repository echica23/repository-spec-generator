package com.xxx.generator.model;

import java.util.List;

public record TypeInfo(
        String name,
        String javadoc,
        List<FieldInfo> fields
) {
}
