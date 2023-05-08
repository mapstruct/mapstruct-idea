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
import org.example.data.IgnoreTestName;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;

@Mapper
interface SingleMappingsMapper {

    @Mappings({
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    @IgnoreTestName
    Target map(Source source);
}

@Mapper
interface SingleMappingMapper {

    @IgnoreTestName
    Target <warning descr="Unmapped target property: moreTarget">map</warning>(Source source);
}

@Mapper
interface AllMappingMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    @IgnoreTestName
    Target mapWithAllMapping(Source source);
}

@Mapper
interface UpdateMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    @IgnoreTestName
    void update(@MappingTarget Target target, Source source);
}
