/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

class Source {

    private String name;
    private Integer size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}

class Target {

    private String testName;
    private Integer size;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}

@Mapper
interface SourceMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target map(Source source);
}

@Mapper
interface ExpressionMappingsMapper {

    @Mappings({
        @Mapping(target = "testName", expression = "java(\"My name\")")
    })
    Target map(Source source);
}

@Mapper
interface ConstantMapper {

    @Mapping(target = "testName", constant = "My name")
    void update(@MappingTarget Target target, Source source);
}

@Mapper
interface IgnoreMappingMapper {

    @Mapping(target = "testName", ignore = true)
    Target map(Source source);
}

@Mapper
interface DependsOnMapper {

    @Mapping(target = "testName", dependsOn = "size")
    Target map(Source source);
}

@Mapper
interface QualifiedByNameMapper {

    @Mapping(target = "size", qualifiedByName = "bitCount")
    Target map(Source source);

    @Named("bitCount")
    default Integer bitCount(Integer in) {
        return Integer.bitCount(in);
    }
}

