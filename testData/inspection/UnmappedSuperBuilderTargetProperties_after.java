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
            @Mapping(target = "testName", ignore = true),
            @Mapping(target = "testName", source = "")
    })
    Target map(Source source);
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "testName", source = "name")
    Target map(Source source);
}

@Mapper
interface NoMappingMapper {

    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "testName", source = "")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    Target map(Source source);

    @InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper
interface AllMappingMapper {

    @Mapping(target = "testName", source = "name")
    @Mapping(target = "moreTarget", source = "moreSource")
    Target mapWithAllMapping(Source source);
}

@Mapper
interface UpdateMapper {

    @Mapping(target = "testName", source = "")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "moreSource")
    void update(@MappingTarget Target target, Source source);
}

@Mapper
interface MultiSourceUpdateMapper {

    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    void update(@MappingTarget Target moreTarget, Source source, String testName, @Context String matching);
}