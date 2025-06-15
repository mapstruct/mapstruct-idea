/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

record Source(String name, String matching, String moreSource, String onlyInSource) { }

record Target(String testName, String matching, String moreTarget) {

    public Target restrict(Target target) {
        return this;
    }

}

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target <warning descr="Unmapped target property: testName">map</warning>(Source source);
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target <warning descr="Unmapped target property: moreTarget">map</warning>(Source source);
}

@Mapper
interface NoMappingMapper {

    Target <warning descr="Unmapped target properties: moreTarget, testName">map</warning>(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper
interface AllMappingMapper {

    @Mapping(target = "testName", source = "name")
    @Mapping(target = "moreTarget", source = "moreSource")
    Target mapWithAllMapping(Source source);
}
