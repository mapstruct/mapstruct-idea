/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", ignore = true)
@Mapping(target = "creationDate", expression = "java(new java.util.Date())")
@Mapping(target = "name", source = "groupName")
@interface ToEntity { }

@Retention(RetentionPolicy.CLASS)
@Mappings({
    @Mapping(target = "id", ignore = true),
    @Mapping(target = "creationDate", expression = "java(new java.util.Date())"),
    @Mapping(target = "name", source = "groupName")
})
@interface ToEntityWithMappings { }

