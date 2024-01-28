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
import org.example.data.UnmappedTargetPropertiesData.Target;

import java.util.Map;

@Mapper
interface SingleMapper {

    Target map(Map<String, String> source, String secondSource);
}
@Mapper
abstract class AbstractMapperWitAbstractMethod {

    abstract Target map(Map<String, String> source, String secondSource);
}
