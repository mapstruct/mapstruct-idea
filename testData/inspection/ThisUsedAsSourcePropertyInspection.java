/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Context;

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

    private Source source;

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "source", source = <warning descr="''.'' should not be used as a source.">"."</warning>)
    Target map(Source source);
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
            @Mapping(target = "source", source = <warning descr="''.'' should not be used as a source.">"."</warning>)
    })
    Target map(Source source);
}

@Mapper
interface MultiSourceMappingMapper {

    @Mapping(target = "source", source = <warning descr="''.'' should not be used as a source.">"."</warning>)
    Target map(Source source, Long age, @Context String thisMustNotBeSuggested);
}

@Mapper
interface NoSourcePropertyMappingMapper {

    @Mapping(target = "source", ignore = true)
    Target map(Source source);
}

@Mapper
interface CorrectSourcePropertyMappingMapper {

    @Mapping(target = "source", source = "source")
    Target map(Source source);
}
