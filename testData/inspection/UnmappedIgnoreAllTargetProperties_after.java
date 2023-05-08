/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;
import org.mapstruct.Mapping;

@Mapper
interface NoMappingsMapper {

    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", ignore = true)
    Target map(Source source);
}
