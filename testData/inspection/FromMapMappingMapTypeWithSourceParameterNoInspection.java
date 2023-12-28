/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashMap;
import java.util.Map;

class Target {

    private Map<Integer, String> innerMap;

    public Target(Map<Integer, String> innerMap) {
        this.innerMap = innerMap;
    }
}

@Mapper
interface ParameterNameMapper {

    Target map(Map<Integer, String> innerMap);

    Target map(HashMap<Integer, String> innerMap);

    void update(@MappingTarget Target target, Map<Integer, String> innerMap);

    void update(@MappingTarget Target target, HashMap<Integer, String> innerMap);
}

@Mapper
interface MappingSourceMapper {

    @Mapping(source = "source", target = "innerMap")
    Target map(Map<Integer, String> source);

    @Mapping(source = "source", target = "innerMap")
    Target map(HashMap<Integer, String> source);

    @Mapping(source = "source", target = "innerMap")
    void update(@MappingTarget Target target, Map<Integer, String> source);

    @Mapping(source = "source", target = "innerMap")
    void update(@MappingTarget Target target, HashMap<Integer, String> source);
}

@Mapper
interface UpdateMapper {



}