/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.example.dto.Car;
import org.example.dto.FluentCarDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface FluentCarMapper {

    @Mappings({
        @Mapping(source = "numberOfSeats", target = "<caret>"),
        @Mapping(source = "manufacturingDate", target = "manufacturingYear")
    })
    FluentCarDto carToCarDto(Car car);
}
