/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.data.UnmappedTargetPropertiesData.Target;

@Mapper
interface DefaultMapper {

    Target <warning descr="Unmapped target property: builderTestName">map</warning>(String source);
}

@Mapper(builder = @Builder(disableBuilder = true))
interface MapperDisabledBuilder {

    Target <warning descr="Unmapped target property: targetTestName">map</warning>(String source);
}

@Mapper
interface BeanMappingDisabledBuilder {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    Target <warning descr="Unmapped target property: targetTestName">map</warning>(String source);
}

@Mapper(builder = @Builder(disableBuilder = true))
interface MapperDisabledBuilderBeanMappingEnabledBuilder {

    @BeanMapping(builder = @Builder(disableBuilder = false))
    Target <warning descr="Unmapped target property: builderTestName">map</warning>(String source);
}
