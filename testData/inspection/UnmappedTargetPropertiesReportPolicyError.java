/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target <error descr="Unmapped target property: moreTarget">map</error>(Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface NoMappingMapper {

    Target <error descr="Unmapped target properties: moreTarget, testName">map</error>(Source source);

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

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface UpdateMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    void <error descr="Unmapped target property: testName">update</error>(@MappingTarget Target target, Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface MultiSourceUpdateMapper {

    void <error descr="Unmapped target property: moreTarget">update</error>(@MappingTarget Target moreTarget, Source source, String testName, @Context String matching);
}
