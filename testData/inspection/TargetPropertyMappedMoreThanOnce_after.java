/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class Target {
    private String testName;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}

class Source {
    private String name;
    private String lastName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "testName", source = "name")
@interface MyMappingAnnotation {
}


@Mapper
interface TargetMappedMoreThanOnceByMappingAnnotationMapper {

    @Mapping(target = "testName", source = "lastName")
    Target map(Source source);
}

@Mapper
interface TargetMappedMoreThanOnceByMappingsAnnotationsMapper {

    @Mappings({
            @Mapping(target = "testName", source = "name")
    })
    Target map(Source source);
}

@Mapper
interface TargetMappedMoreThanOnceByMappingsAnnotationsAndMappingAnnotationMapper {

    @Mappings({
            @Mapping(target = "testName", source = "lastName")
    })
    Target map(Source source);
}

@Mapper
interface TargetMappedMoreThanOnceByMyMappingAnnotationAndMappingAnnotationMapper {

    @Mapping(target = "testName", source = "lastName")
    Target map(Source source);
}

@Mapper
interface TargetMappedMoreThanOnceByMyMappingAnnotationAndMappingsAnnotationMapper {

    @Mappings({
            @Mapping(target = "testName", source = "lastName")
    })
    @MyMappingAnnotation
    Target map(Source source);
}