/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.data.UnmappedTargetPropertiesData.Target;

@Mapper
interface DefaultMapper {

    @Mapping(target = "builderTestName", source = "")
    @Mapping(target = "builderTestName", ignore = true)
    Target map(String source);
}

@Mapper(builder = @Builder(disableBuilder = true))
interface MapperDisabledBuilder {

    @Mapping(target = "targetTestName", source = "")
    @Mapping(target = "targetTestName", ignore = true)
    Target map(String source);
}

@Mapper
interface BeanMappingDisabledBuilder {

    @Mapping(target = "targetTestName", source = "")
    @Mapping(target = "targetTestName", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Target map(String source);
}

@Mapper(builder = @Builder(disableBuilder = true))
interface MapperDisabledBuilderBeanMappingEnabledBuilder {

    @Mapping(target = "builderTestName", source = "")
    @Mapping(target = "builderTestName", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = false))
    Target map(String source);
}
