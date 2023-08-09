/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.example.data.UnmappedCollectionGetterPropertiesData.Target;
import org.example.data.UnmappedCollectionGetterPropertiesData.Source;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper
interface NoMappingMapper {

    Target <warning descr="Unmapped target properties: listTarget, mapTarget, setTarget">map</warning>(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper
interface AllMappingMapper {

    @Mapping(target = "listTarget", source = "listSource")
    @Mapping(target = "setTarget", source = "setSource")
    @Mapping(target = "mapTarget", source = "mapSource")
    Target mapWithAllMapping(Source source);
}
