/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.HashMap;
import java.util.Map;

class Target {

    private String name;
    private String lastName;
    private String city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

interface NotMapStructMapper {

    Target map(Map<Integer, String> source);
}

@Mapper
interface NoMappingMapper {

    Target map(<warning descr="Key must be of type String for mapping Map to Bean">Map<Integer, String> source</warning>);

    Target map(<warning descr="Key must be of type String for mapping Map to Bean">HashMap<Integer, String> source</warning>);
}

@Mapper
interface MultiSourceMappingsMapper {

    Target mapWithAllMapping(Map<Integer, String> source, String moreTarget, String testName);
}

@Mapper
interface UpdateMapper {

    void update(@MappingTarget Target target, <warning descr="Key must be of type String for mapping Map to Bean">Map<Integer, String> source</warning>);

    void update(@MappingTarget Target target, <warning descr="Key must be of type String for mapping Map to Bean">HashMap<Integer, String> source</warning>);
}

@Mapper
interface MultiSourceUpdateMapper {

    void update(@MappingTarget Target moreTarget, Map<Integer, String> source, String testName, @Context String matching);
}

@Mapper
interface DefaultMapper {

    default Target map(Map<Integer, String> source) {
        return null;
    }
}

@Mapper
abstract class AbstractMapperWithoutAbstractMethod {

    protected Target map(Map<Integer, String> source) {
        return null;
    }
}
