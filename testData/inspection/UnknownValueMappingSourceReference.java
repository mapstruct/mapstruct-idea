/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

enum Target {
    FIST,
    SECOND,
    THIRD
}

enum Source {
    FIST,
    SECOND,
    THIRD
}

@Mapper
interface SingleValueMappingMapper {

    @ValueMapping(target = "FIST", source = "<error descr="Cannot resolve symbol 'OTHER'">OTHER</error>")
    Target map(Source source);
}

@Mapper
interface SingleValueMappingsMapper {

    @ValueMappings({
        @ValueMapping(target = "FIST", source = "<error descr="Cannot resolve symbol 'OTHER'">OTHER</error>")
})
Target map(Source source);
}

