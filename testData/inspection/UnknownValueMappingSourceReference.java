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

    @ValueMapping(target = "FIST", source = "<error descr="Unknown enum constant 'OTHER'">OTHER</error>")
    Target map(Source source);
}

@Mapper
interface SingleValueMappingsMapper {

    @ValueMappings({
        @ValueMapping(target = "FIST", source = "<error descr="Unknown enum constant 'OTHER'">OTHER</error>")
})
Target map(Source source);
}

@Mapper
interface StringToEnumMapper {

    @ValueMapping(target = "FIST", source = "OTHER")
    Target map(String source);
}

@Mapper
interface EmptyValueMappingMapper {

    @ValueMapping(target = "FIST", source = <error descr="Unknown enum constant ''">""</error>)
    Target map(Source source);
}

