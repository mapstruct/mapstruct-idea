/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.example.data.UnmappedTargetPropertiesData.TargetWithInnerObject;
import org.example.data.UnmappedTargetPropertiesData.Source;

@Mapper
interface SingleMappingsMapper {

    @Mappings({
            @Mapping(target = "testTarget.moreTarget", source = "moreSource")
    })
    TargetWithInnerObject map(Source source);
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "testTarget.testName", source = "name")
    TargetWithInnerObject map(Source source);
}

@Mapper
interface UpdateMapper {

    @Mapping(target = "testTarget.moreTarget", source = "moreSource")
    void update(@MappingTarget TargetWithInnerObject target, Source source);
}
