/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Ignored;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;
import org.example.data.UnmappedTargetPropertiesData.TargetWithInnerObject;

@Mapper
interface AllIgnoredMapper {

    @Ignored(targets = { "testName", "moreTarget" })
    Target map(Source source);
}

@Mapper
interface PartiallyIgnoredMapper {

    @Ignored(targets = { "testName" })
    Target <warning descr="Unmapped target property: moreTarget">map</warning>(Source source);
}

@Mapper
interface IgnoredAndMappedMapper {

    @Ignored(targets = { "moreTarget" })
    @Mapping(target = "testName", source = "name")
    Target map(Source source);
}

@Mapper
interface IgnoredWithPrefixMapper {

    @Ignored(prefix = "testTarget", targets = { "testName", "moreTarget" })
    TargetWithInnerObject map(Source source);
}

@Mapper
interface MultipleIgnoredAnnotationsMapper {

    @Ignored(targets = { "testName" })
    @Ignored(targets = { "moreTarget" })
    Target map(Source source);
}

@Mapper
interface IgnoredWithPrefixAndMappingMapper {

    @Ignored(prefix = "testTarget", targets = { "moreTarget" })
    @Mapping(target = "testTarget.testName", source = "name")
    TargetWithInnerObject map(Source source);
}
