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
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
            @Mapping(target = "moreTarget", source = "moreSource"),
            @Mapping(target = "testName", ignore = true),
            @Mapping(target = "testName", source = "")
    })
    Target map(Source source);
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "testName", source = "name")
    Target map(Source source);
}

@Mapper
interface NoMappingMapper {

    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "testName", source = "")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    Target map(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper
interface AllMappingMapper {

    @Mapping(target = "testName", source = "name")
    @Mapping(target = "moreTarget", source = "moreSource")
    Target mapWithAllMapping(Source source);
}

@Mapper
interface MultiSourceMappingsMapper {

    Target mapWithAllMapping(Source source, String moreTarget, String testName);
}

@Mapper
interface UpdateMapper {

    @Mapping(target = "testName", source = "")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "moreSource")
    void update(@MappingTarget Target target, Source source);
}

@Mapper
interface MultiSourceUpdateMapper {

    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    void update(@MappingTarget Target moreTarget, Source source, String testName, @Context String matching);
}

@Mapper
interface DefaultMapper {

    default Target map(Source source) {
        return null;
    }
}

@Mapper
abstract class AbstractMapperWithoutAbstractMethod {

    protected Target map(Source source) {
        return null;
    }
}
