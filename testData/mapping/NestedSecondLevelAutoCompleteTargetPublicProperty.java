/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.example.dto.CarPublic;
import org.example.dto.CarDtoPublic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CarMapper {

    @Mapping(source = "driver.name", target = "myDriver.<caret>name")
    CarDtoPublic carToCarDto(CarPublic car);
}
