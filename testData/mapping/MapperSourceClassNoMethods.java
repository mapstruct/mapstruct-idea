/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.example.dto.CarDto;
import org.example.dto.EmptyClass;

@Mapper
public interface CarMapper {

    @Mappings({
        @Mapping(source = "<caret>numberOfSeats", target = "seatCount"),
        @Mapping(source = "manufacturingDate", target = "manufacturingYear")
    })
    CarDto carToCarDto(EmptyClass source);
}
