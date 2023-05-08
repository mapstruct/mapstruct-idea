/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.example.dto.Car;
import org.example.dto.CarDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface CarMapper {

    @Mapping(source = "source.free", target = "target<caret>.available")
    void update(Car source, @MappingTarget CarDto target);
}
