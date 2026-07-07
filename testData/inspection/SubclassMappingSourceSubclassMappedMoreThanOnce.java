/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class Source {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class SourceSubclass1 extends Source {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

class SourceSubclass2 extends Source {
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

class Target {
    private String testName;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}

class TargetSubclass1 extends Target {
    private String kind;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}

class TargetSubclass2 extends Target {
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

@Retention(RetentionPolicy.CLASS)
@SubclassMapping(source = SourceSubclass1.class, target = TargetSubclass1.class)
@interface MySubclassMappingAnnotation {
}


@Mapper
interface SubclassMappingSourceMappedMoreThanOnceBySubclassMappingAnnotationMapper {

    @SubclassMapping(source = <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">SourceSubclass1.class</error>, target = TargetSubclass1.class)
    @SubclassMapping(source = <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">SourceSubclass1.class</error>, target = TargetSubclass2.class)
    Target map(Source source);
}

@Mapper
interface SubclassMappingSourceMappedMoreThanOnceBySubclassMappingsAnnotationMapper {

    @SubclassMappings({
            @SubclassMapping(source = <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">SourceSubclass1.class</error>, target = TargetSubclass1.class),
            @SubclassMapping(source = <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">SourceSubclass1.class</error>, target = TargetSubclass2.class)
    })
    Target map(Source source);
}

@Mapper
interface SubclassMappingSourceMappedMoreThanOnceBySubclassMappingAnnotationAndSubclassMappingsAnnotationMapper {

    @SubclassMapping(source = <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">SourceSubclass1.class</error>, target = TargetSubclass1.class)
    @SubclassMappings({
            @SubclassMapping(source = <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">SourceSubclass1.class</error>, target = TargetSubclass2.class)
    })
    Target map(Source source);
}

@Mapper
interface SubclassMappingSourceMappedMoreThanOnceByMySubclassMappingAnnotationAndSubclassMappingAnnotationMapper {

    @SubclassMapping(source = <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">SourceSubclass1.class</error>, target = TargetSubclass2.class)
    <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">@MySubclassMappingAnnotation</error>
    Target map(Source source);
}

@Mapper
interface SubclassMappingSourceMappedMoreThanOnceByMySubclassMappingAnnotationAndSubclassMappingsAnnotationMapper {

    @SubclassMappings({
            @SubclassMapping(source = <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">SourceSubclass1.class</error>, target = TargetSubclass2.class)
    })
    <error descr="Subclass 'org.example.data.SourceSubclass1' is already defined as a source.">@MySubclassMappingAnnotation</error>
    Target map(Source source);
}
