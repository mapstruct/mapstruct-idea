/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target <warning descr="Unmapped target property: moreTarget">map</warning>(Source source);
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target <warning descr="Unmapped target property: testName">map</warning>(Source source);
}

@Mapper
interface SingleMappingsNoBracesMapper {

    @Mappings(
        @Mapping(target = "moreTarget", source = "moreSource")
    )
    Target <warning descr="Unmapped target property: testName">map</warning>(Source source);
}

@Mapper
interface NoMappingMapper {

    Target <warning descr="Unmapped target properties: moreTarget, testName">map</warning>(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper
interface NoMappingsMapper {

    @Mappings({
    })
    Target <warning descr="Unmapped target properties: moreTarget, testName">map</warning>(Source source);
}

@MapperConfig
interface AllMappingsMapperConfig {

    @Mappings({
        @Mapping(target = "testName", source = "name"),
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target mapWithAllMappings(Source source);
}

@Mapper
interface UpdateMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    void <warning descr="Unmapped target property: testName">update</warning>(@MappingTarget Target target, Source source);
}

@Mapper
interface MultiSourceUpdateMapper {

    void <warning descr="Unmapped target properties: matching, moreTarget">update</warning>(@MappingTarget Target moreTarget, Source source, String testName, @org.mapstruct.Context String matching);
}