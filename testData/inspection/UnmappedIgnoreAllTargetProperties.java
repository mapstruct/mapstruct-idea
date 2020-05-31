/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;

@Mapper
interface NoMappingsMapper {

    Target <warning descr="Unmapped target properties: moreTarget, testName">map</warning>(Source source);
}
