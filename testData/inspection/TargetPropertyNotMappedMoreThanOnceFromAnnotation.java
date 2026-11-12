/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


class TargetClass {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

class SourceClass {
    private String sourceName;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target( ElementType.METHOD )
@Mappings({
    @Mapping(target = "name", source = "sourceName"),
    @Mapping(target = "value", ignore = true)
})
@interface MyMappingAnnotation {
}

@Mapper
interface MyMappingAnnotationMapper {

    @MyMappingAnnotation
    TargetClass map(SourceClass source);
}