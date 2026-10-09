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

    @ValueMapping(target = "A", source = <error descr="Value mapping source property 'A' must not be mapped more than once.">"A"</error>)
    @ValueMapping(target = "B", source = <error descr="Value mapping source property 'A' must not be mapped more than once.">"A"</error>)
    Target map(Source source);
}

@Mapper
interface ValueMappingSourceMappedMoreThanOnceByMappingsAnnotationsMapper {

    @ValueMappings({
            @ValueMapping(target = "A", source = <error descr="Value mapping source property 'A' must not be mapped more than once.">"A"</error>),
            @ValueMapping(target = "B", source = <error descr="Value mapping source property 'A' must not be mapped more than once.">"A"</error>)
    })
    Target map(Source source);
}

@Mapper
interface ValueMappingSourceMappedMoreThanOnceByMappingsAnnotationsAndMappingAnnotationMapper {

    @ValueMapping(target = "A", source = <error descr="Value mapping source property 'A' must not be mapped more than once.">"A"</error>)
    @ValueMappings({
            @ValueMapping(target = "B", source = <error descr="Value mapping source property 'A' must not be mapped more than once.">"A"</error>)
    })
    Target map(Source source);
}

@Mapper
interface ValueMappingSourceMappedMoreThanOnceByMyMappingAnnotationAndMappingAnnotationMapper {

    @ValueMapping(target = "A", source = <error descr="Value mapping source property 'A' must not be mapped more than once.">"A"</error>)
    <error descr="Value mapping source property 'A' must not be mapped more than once.">@MyValueMappingAnnotation</error>
    Target map(Source source);
}

@Mapper
interface ValueMappingSourceMappedMoreThanOnceByMyMappingAnnotationAndMappingsAnnotationMapper {

    @ValueMappings({
            @ValueMapping(target = "A", source = <error descr="Value mapping source property 'A' must not be mapped more than once.">"A"</error>)
    })
    <error descr="Value mapping source property 'A' must not be mapped more than once.">@MyValueMappingAnnotation</error>
    Target map(Source source);
}