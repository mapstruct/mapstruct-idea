package inspection;/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.example.data.UnmappedTargetPropertiesData.Source;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.mapstruct.*;

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target <warning descr="Unmapped target property: moreTarget">map</warning>(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.WARN)
interface NoMappingMapper {

    Target <warning descr="Unmapped target properties: moreTarget, testName">map</warning>(Source source);

    @InheritInverseConfiguration
    Source reverse(Target target);
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

    void <warning descr="Unmapped target property: moreTarget">update</warning>(@MappingTarget Target moreTarget, Source source, String testName, @Context String matching);
}
