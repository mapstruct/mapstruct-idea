/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.example.dto.CarDto;
import org.example.dto.PersonDto;
import org.example.dto.Car;
import org.example.dto.Person;

@Mapper
public interface CarMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {
        "numberOfSeats",
        "<caret>"
    })
    CarDto carToCarDto(Car car);
}
