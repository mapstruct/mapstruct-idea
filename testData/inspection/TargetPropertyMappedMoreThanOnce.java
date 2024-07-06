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

    @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "name")
    @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "lastName")
    Target map(Source source);
}

@Mapper
interface TargetMappedMoreThanOnceByMappingsAnnotationsMapper {

    @Mappings({
            @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "name"),
            @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "lastName")
    })
    Target map(Source source);
}

@Mapper
interface TargetMappedMoreThanOnceByMappingsAnnotationsAndMappingAnnotationMapper {

    @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "name")
    @Mappings({
            @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "lastName")
    })
    Target map(Source source);
}

@Mapper
interface TargetMappedMoreThanOnceByMyMappingAnnotationAndMappingAnnotationMapper {

    @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "lastName")
    <error descr="Target property 'testName' must not be mapped more than once.">@MyMappingAnnotation</error>
    Target map(Source source);
}

@Mapper
interface TargetMappedMoreThanOnceByMyMappingAnnotationAndMappingsAnnotationMapper {

    @Mappings({
            @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "lastName")
    })
    <error descr="Target property 'testName' must not be mapped more than once.">@MyMappingAnnotation</error>
    Target map(Source source);
}