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
import org.example.dto.FluentCarDto;
import org.example.dto.PersonDto;
import org.example.dto.FluentCar;
import org.example.dto.Person;

@Mapper
public interface FluentCarMapper {

    @Mappings({
        @Mapping(source = "numberOfSeats", target = "unknown<caret>"),
        @Mapping(source = "manufacturingDate", target = "manufacturingYear")
    })
    FluentCarDto carToCarDto(FluentCar car);
}
