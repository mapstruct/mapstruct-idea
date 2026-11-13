/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.example.dto.Car;
import org.example.dto.CarDto;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InheritConfigurationCompletion {

    @Mapping(target = "numberOfSeats", ignore = true)
    @Mapping(target = "manufacturingDate", source = "manufacturingYear")
    @Mapping(target = "free", source = "available")
    Car carDtoToCarIgnoringSeatCount(CarDto car);

    @Mapping(target = "manufacturingDate", ignore = true)
    @Mapping(target = "free", source = "available")
    @Mapping(target = "driver", source = "myDriver")
    @Mapping(target = "numberOfSeats", source = "seatCount")
    Car carDtoToCarIgnoringManufacturingDate(CarDto car);

    @InheritConfiguration(name = "<caret>")
    void carDtoIntoCar(CarDto carDto, @MappingTarget Car car);

}
