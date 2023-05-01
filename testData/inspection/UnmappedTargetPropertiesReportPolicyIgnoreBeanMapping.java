package inspection;/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.example.data.UnmappedTargetPropertiesData.Source;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.mapstruct.*;

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Target map(Source source);
}

@Mapper
interface NoMappingMapper {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Target map(Source source);

    @InheritInverseConfiguration
    Source reverse(Target target);
}

@MapperConfig
interface AllMappingsMapperConfig {

    @Mappings({
        @Mapping(target = "testName", source = "name"),
        @Mapping(target = "moreTarget", source = "moreSource")
    })
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Target mapWithAllMappings(Source source);
}

@Mapper
interface UpdateMapper {

    @Mapping(target = "moreTarget", source = "moreSource")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void update(@MappingTarget Target target, Source source);
}

@Mapper
interface MultiSourceUpdateMapper {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void update(@MappingTarget Target moreTarget, Source source, String testName, @Context String matching);
}
