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
import org.mapstruct.ReportingPolicy;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target map(Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface SingleMappingsMapper {

    @Mappings({
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target map(Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface SingleMappingsNoBracesMapper {

    @Mappings(
        @Mapping(target = "moreTarget", source = "moreSource")
    )
    Target map(Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NoMappingMapper {

    Target map(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NoMappingsMapper {

    @Mappings({
    })
    Target map(Source source);
}

@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface AllMappingsMapperConfig {

    @Mappings({
        @Mapping(target = "testName", source = "name"),
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target mapWithAllMappings(Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UpdateMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    void update(@MappingTarget Target target, Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface MultiSourceUpdateMapper {

    void update(@MappingTarget Target moreTarget, Source source, String testName, @org.mapstruct.Context String matching);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface SingleMappingConstantReferenceMapper {

    String TEST_NAME = "testName";

    @Mapping(target = TEST_NAME, source = "name")
    Target map(Source source);
}