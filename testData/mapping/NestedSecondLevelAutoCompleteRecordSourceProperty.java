/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.example.dto.CarDtoRecord;
import org.example.dto.CarDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CarMapper {

    @Mapping(source = "myDriver.<caret>name", target = "myDriver.name")
    CarDto carToCarDto(CarDtoRecord car);
}
