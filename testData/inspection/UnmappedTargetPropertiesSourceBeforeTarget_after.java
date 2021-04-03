/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;

@Mapper
interface SingleMappingsMapper {

    @Mappings({
            @Mapping(target = "moreTarget", source = "moreSource"),
            @Mapping(target = "testName", ignore = true),
            @Mapping(source = "", target = "testName")
    })
    Target map(Source source);
}

@Mapper
interface SingleMappingMapper {

    @Mapping(source = "", target = "moreTarget")
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "testName", source = "name")
    Target map(Source source);
}

@Mapper
interface UpdateMapper {

    @Mapping(source = "", target = "testName")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "moreSource")
    void update(@MappingTarget Target target, Source source);
}
