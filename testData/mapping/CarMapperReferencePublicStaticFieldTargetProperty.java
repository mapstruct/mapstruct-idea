/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.CarDtoPublic;
import org.example.dto.Car;

@Mapper
public interface CarMapper {

    @Mapping(source = "numberOfSeats", target = "EMPTY_STRING<caret>")
    CarDtoPublic carToCarDto(Car car);
}