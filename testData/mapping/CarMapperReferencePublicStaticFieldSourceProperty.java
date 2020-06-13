/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.CarDto;
import org.example.dto.CarPublic;

@Mapper
public interface CarMapper {

    @Mapping(source = "EMPTY_STRING<caret>", target = "seatCount")
    CarDtoPublic carToCarDto(CarPublic car);
}
