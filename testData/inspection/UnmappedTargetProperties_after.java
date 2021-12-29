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

    @Mappings({
            @Mapping(target = "testName", source = "name"),
            @Mapping(target = "moreTarget", ignore = true),
            @Mapping(target = "moreTarget", source = "")
    })
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
interface SingleMappingsNoBracesMapper {

    @Mappings({
            @Mapping(target = "moreTarget", source = "moreSource"),
            @Mapping(target = "testName", ignore = true),
            @Mapping(target = "testName", source = "")
    })
    Target map(Source source);
}

@Mapper
interface NoMappingMapper {

    @Mappings({
            @Mapping(target = "moreTarget", ignore = true),
            @Mapping(target = "moreTarget", source = ""),
            @Mapping(target = "testName", ignore = true),
            @Mapping(target = "testName", source = ""),
            @Mapping(target = "moreTarget", ignore = true),
            @Mapping(target = "testName", ignore = true)
    })
    Target map(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper
interface NoMappingsMapper {

    @Mappings({
            @Mapping(target = "moreTarget", ignore = true),
            @Mapping(target = "moreTarget", source = ""),
            @Mapping(target = "testName", ignore = true),
            @Mapping(target = "testName", source = ""),
            @Mapping(target = "moreTarget", ignore = true),
            @Mapping(target = "testName", ignore = true)
    })
    Target map(Source source);
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

    @Mappings({
            @Mapping(target = "moreTarget", source = "moreSource"),
            @Mapping(target = "testName", ignore = true),
            @Mapping(target = "testName", source = "")
    })
    void update(@MappingTarget Target target, Source source);
}

@Mapper
interface MultiSourceUpdateMapper {

    @Mappings({
            @Mapping(target = "moreTarget", ignore = true),
            @Mapping(target = "moreTarget", source = "")
    })
    void update(@MappingTarget Target moreTarget, Source source, String testName, @org.mapstruct.Context String matching);
}

@Mapper
interface SingleMappingConstantReferenceMapper {

    String TEST_NAME = "testName";

    @Mappings({
            @Mapping(target = TEST_NAME, source = "name"),
            @Mapping(target = "moreTarget", ignore = true),
            @Mapping(target = "moreTarget", source = "")
    })
    Target map(Source source);
}