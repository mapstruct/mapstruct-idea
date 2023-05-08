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

    @Mapping(target = ".", source = "nested")
    Target <warning descr="Unmapped target properties: moreTarget, testName">map</warning>(Source source);
}

@Mapper
interface NoMappingMapper {

    Target <warning descr="Unmapped target properties: matching, moreTarget, testName">map</warning>(Source source);

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

    @Mapping(target = ".", source = "nested")
    void <warning descr="Unmapped target properties: moreTarget, testName">update</warning>(@MappingTarget Target target, Source source);
}
