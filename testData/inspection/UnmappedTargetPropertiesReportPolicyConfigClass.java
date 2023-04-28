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

@Mapper(config = AllMappingsMapperConfig.class)
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target <error descr="Unmapped target property: moreTarget">map</error>(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class)
interface SingleMappingsMapper {

    @Mappings({
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target <error descr="Unmapped target property: testName">map</error>(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class)
interface SingleMappingsNoBracesMapper {

    @Mappings(
        @Mapping(target = "moreTarget", source = "moreSource")
    )
    Target <error descr="Unmapped target property: testName">map</error>(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class)
interface NoMappingMapper {

    Target <error descr="Unmapped target properties: moreTarget, testName">map</error>(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper(config = AllMappingsMapperConfig.class)
interface NoMappingsMapper {

    @Mappings({
    })
    Target <error descr="Unmapped target properties: moreTarget, testName">map</error>(Source source);
}

@MapperConfig(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface AllMappingsMapperConfig {

    @Mappings({
        @Mapping(target = "testName", source = "name"),
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target mapWithAllMappings(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class)
interface UpdateMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    void <error descr="Unmapped target property: testName">update</error>(@MappingTarget Target target, Source source);
}

@Mapper(config = AllMappingsMapperConfig.class)
interface MultiSourceUpdateMapper {

    void <error descr="Unmapped target property: moreTarget">update</error>(@MappingTarget Target moreTarget, Source source, String testName, @org.mapstruct.Context String matching);
}

@Mapper(config = AllMappingsMapperConfig.class)
interface SingleMappingConstantReferenceMapper {

    String TEST_NAME = "testName";

    @Mapping(target = TEST_NAME, source = "name")
    Target <error descr="Unmapped target property: moreTarget">map</error>(Source source);
}