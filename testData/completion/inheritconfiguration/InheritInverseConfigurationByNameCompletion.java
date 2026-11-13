/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.example.dto.Car;
import org.example.dto.CarDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InheritInverseConfigurationByNameCompletion {

    @Mapping(target = "numberOfSeats", ignore = true)
    @Mapping(target = "manufacturingDate", source = "manufacturingYear")
    CarDto carToCarDto(Car car);

    @Mapping(target = "driver", ignore = true)
    CarDto carToCarDtoIgnoringDriver(Car car);

    @InheritInverseConfiguration(name = "<caret>")
    Car carDtoToCar(CarDto carDto, @MappingTarget Car car);

}
