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

    Target map(Map source);
}

@Mapper
interface NoMappingMapper {

    Target map(Map<String, String> source);

    Target map(HashMap<String, String> source);
}

@Mapper
interface MultiSourceMappingsMapper {

    Target mapWithAllMapping(Map source, String moreTarget, String testName);
}

@Mapper
interface UpdateMapper {

    void update(@MappingTarget Target target, Map<String, String> source);

    void update(@MappingTarget Target target, HashMap<String, String> source);
}

@Mapper
interface MultiSourceUpdateMapper {

    void update(@MappingTarget Target moreTarget, Map source, String testName, @Context String matching);
}

@Mapper
interface DefaultMapper {

    default Target map(Map source) {
        return null;
    }
}

@Mapper
abstract class AbstractMapperWithoutAbstractMethod {

    protected Target map(Map source) {
        return null;
    }
}
