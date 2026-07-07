/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

enum Target {
    A,
    B,
}

enum Source {
    A,
    B,
}



@Retention(RetentionPolicy.CLASS)
@ValueMapping(target = "B", source = "A")
@interface MyValueMappingAnnotation {
}


@Mapper
interface ValueMappingSourceMappedMoreThanOnceByMappingAnnotationMapper {

    @ValueMapping(target = "B", source = "A")
    Target map(Source source);
}

@Mapper
interface ValueMappingSourceMappedMoreThanOnceByMappingsAnnotationsMapper {

    @ValueMappings({
            @ValueMapping(target = "A", source = "A")
    })
    Target map(Source source);
}

@Mapper
interface ValueMappingSourceMappedMoreThanOnceByMappingsAnnotationsAndMappingAnnotationMapper {

    @ValueMappings({
            @ValueMapping(target = "B", source = "A")
    })
    Target map(Source source);
}

@Mapper
interface ValueMappingSourceMappedMoreThanOnceByMyMappingAnnotationAndMappingAnnotationMapper {

    @ValueMapping(target = "A", source = "A")
    Target map(Source source);
}

@Mapper
interface ValueMappingSourceMappedMoreThanOnceByMyMappingAnnotationAndMappingsAnnotationMapper {

    @ValueMappings({
            @ValueMapping(target = "A", source = "A")
    })
    @MyValueMappingAnnotation
    Target map(Source source);
}