/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Ignored;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.Car;
import org.example.dto.CarDto;

@Mapper
public interface IgnoredTargetsWithMapping {

    @Ignored(targets = { "seatCount", "<caret>" })
    @Mapping(target = "make", ignore = true)
    CarDto carToCarDto(Car car);
}
