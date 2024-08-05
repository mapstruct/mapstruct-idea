/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.example.dto.CarDto;
import org.example.dto.Car;

@Mapper
public abstract class InheritMultipleMethodsCarMapper {

    @Mappings({
        @Mapping(target = "maker", source = "manufacturer"),
        @Mapping(target = "seatCount", source = "numberOfSeats")
    })
    public abstract CarDto mapToBase(Car car);

    @Mappings({
        @Mapping(target = "manufacturer", constant = "food"),
        @Mapping(target = "numberOfSeats", ignore = true)
    })
    public abstract Car mapFromBase(CarDto carDto);

    @InheritConfiguration(name = "mapToBase")
    @InheritInverseConfiguration(name = "mapFromBase")
    public abstract CarDto mapTo(Car car);

    @InheritConfiguration(name = "mapFromBase")
    @InheritInverseConfiguration(name = "<caret>")
    public abstract Car mapFrom(CarDto carDto);

}
