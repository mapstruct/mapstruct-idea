/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.example.data.UnmappedCurrentTargetPropertiesData.Target;
import org.example.data.UnmappedCurrentTargetPropertiesData.Source;

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "testName", source = "")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = ".", source = "nested")
    Target map(Source source);
}

@Mapper
interface NoMappingMapper {

    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "matching", ignore = true)
    @Mapping(target = "testName", source = "")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "matching", source = "")
    @Mapping(target = "matching", ignore = true)
    Target map(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper
interface AllMappingMapper {

    @Mapping(target = ".", source = "nested")
    @Mapping(target = "moreTarget", source = "nested.moreSource")
    @Mapping(target = "testName", source = "nested.name")
    Target mapWithAllMapping(Source source);
}

@Mapper
interface UpdateMapper {

    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "testName", source = "")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = ".", source = "nested")
    void update(@MappingTarget Target target, Source source);
}
