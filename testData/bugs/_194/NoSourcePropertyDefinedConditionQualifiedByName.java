/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

class Source {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Target {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "name", conditionQualifiedByName = "notEmpty")
    Target map(Source source);

    @Condition
    @Named("notEmpty")
    default boolean notEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
            @Mapping(target = "name", conditionQualifiedByName = "notEmpty")
            })
    Target map(Source source);

    @Condition
    @Named("notEmpty")
    default boolean notEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}

