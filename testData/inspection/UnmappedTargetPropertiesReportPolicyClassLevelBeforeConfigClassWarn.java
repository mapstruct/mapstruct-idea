package inspection;/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.example.data.UnmappedTargetPropertiesData.Source;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.mapstruct.*;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target <warning descr="Unmapped target property: moreTarget">map</warning>(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface SingleMappingsMapper {

    @Mappings({
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target <warning descr="Unmapped target property: testName">map</warning>(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface SingleMappingsNoBracesMapper {

    @Mappings(
        @Mapping(target = "moreTarget", source = "moreSource")
    )
    Target <warning descr="Unmapped target property: testName">map</warning>(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface NoMappingMapper {

    Target <warning descr="Unmapped target properties: moreTarget, testName">map</warning>(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface NoMappingsMapper {

    @Mappings({
    })
    Target <warning descr="Unmapped target properties: moreTarget, testName">map</warning>(Source source);
}

@MapperConfig(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface AllMappingsMapperConfig {

    @Mappings({
        @Mapping(target = "testName", source = "name"),
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    Target mapWithAllMappings(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface UpdateMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    void <warning descr="Unmapped target property: testName">update</warning>(@MappingTarget Target target, Source source);
}

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface MultiSourceUpdateMapper {

    void <warning descr="Unmapped target property: moreTarget">update</warning>(@MappingTarget Target moreTarget, Source source, String testName, @org.mapstruct.Context String matching);
}

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface SingleMappingConstantReferenceMapper {

    String TEST_NAME = "testName";

    @Mapping(target = TEST_NAME, source = "name")
    Target <warning descr="Unmapped target property: moreTarget">map</warning>(Source source);
}