/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Context;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.example.data.UnmappedSuperBuilderTargetPropertiesData.Target;
import org.example.data.UnmappedSuperBuilderTargetPropertiesData.Source;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
        @Mapping(target = "moreTarget", source = "moreSource"),
        @Mapping(target = "baseValue", source = "name")
    })
    Target <warning descr="Unmapped target property: testName">map</warning>(Source source);
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    @Mapping(target = "baseValue", source = "name")
    Target <warning descr="Unmapped target property: moreTarget">map</warning>(Source source);
}

@Mapper
interface NoMappingMapper {

    Target <warning descr="Unmapped target properties: baseValue, moreTarget, testName">map</warning>(Source source);

    @InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper
interface AllMappingMapper {

    @Mapping(target = "testName", source = "name")
    @Mapping(target = "moreTarget", source = "moreSource")
    @Mapping(target = "baseValue", source = "name")
    Target mapWithAllMapping(Source source);
}

@Mapper
interface UpdateMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    @Mapping(target = "baseValue", source = "name")
    void <warning descr="Unmapped target property: testName">update</warning>(@MappingTarget Target target, Source source);
}

@Mapper
interface MultiSourceUpdateMapper {

    void <warning descr="Unmapped target properties: baseValue, moreTarget">update</warning>(@MappingTarget Target moreTarget, Source source, String testName, @Context String matching);
}