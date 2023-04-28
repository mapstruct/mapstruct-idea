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

import static org.mapstruct.ReportingPolicy.ERROR;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper(unmappedTargetPolicy = ERROR)
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target <error descr="Unmapped target property: moreTarget">map</error>(Source source);
}

@Mapper(unmappedTargetPolicy = ERROR)
interface SingleMappingsMapper {

    @Mappings({
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target <error descr="Unmapped target property: testName">map</error>(Source source);
}

@Mapper(unmappedTargetPolicy = ERROR)
interface SingleMappingsNoBracesMapper {

    @Mappings(
        @Mapping(target = "moreTarget", source = "moreSource")
    )
    Target <error descr="Unmapped target property: testName">map</error>(Source source);
}

@Mapper(unmappedTargetPolicy = ERROR)
interface NoMappingMapper {

    Target <error descr="Unmapped target properties: moreTarget, testName">map</error>(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper(unmappedTargetPolicy = ERROR)
interface NoMappingsMapper {

    @Mappings({
    })
    Target <error descr="Unmapped target properties: moreTarget, testName">map</error>(Source source);
}

@MapperConfig(unmappedTargetPolicy = ERROR)
interface AllMappingsMapperConfig {

    @Mappings({
        @Mapping(target = "testName", source = "name"),
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target mapWithAllMappings(Source source);
}

@Mapper(unmappedTargetPolicy = ERROR)
interface UpdateMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    void <error descr="Unmapped target property: testName">update</error>(@MappingTarget Target target, Source source);
}

@Mapper(unmappedTargetPolicy = ERROR)
interface MultiSourceUpdateMapper {

    void <error descr="Unmapped target property: moreTarget">update</error>(@MappingTarget Target moreTarget, Source source, String testName, @org.mapstruct.Context String matching);
}

@Mapper(unmappedTargetPolicy = ERROR)
interface SingleMappingConstantReferenceMapper {

    String TEST_NAME = "testName";

    @Mapping(target = TEST_NAME, source = "name")
    Target <error descr="Unmapped target property: moreTarget">map</error>(Source source);
}