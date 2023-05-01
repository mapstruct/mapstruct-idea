/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.example.data.UnmappedTargetPropertiesData.Source;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.mapstruct.*;

@Mapper(config = AllMappingsMapperConfig.class)
interface SingleMappingMapper {

    @Mapping(target = "testName", source = "name")
    Target map(Source source);
}

@Mapper(config = AllMappingsMapperConfig.class)
interface NoMappingMapper {

    Target map(Source source);

    @InheritInverseConfiguration
    Source reverse(Target target);
}

@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
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
    void update(@MappingTarget Target target, Source source);
}

@Mapper(config = AllMappingsMapperConfig.class)
interface MultiSourceUpdateMapper {

    void update(@MappingTarget Target moreTarget, Source source, String testName, @Context String matching);
}
