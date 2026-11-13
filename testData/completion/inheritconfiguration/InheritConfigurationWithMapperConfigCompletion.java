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
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@MapperConfig
interface ConfigCompleteMapperConfig {

    @Mapping(target = "manufacturingDate", source = "manufacturingYear")
    @Mapping(target = "free", source = "available")
    Car configSourceToTarget(CarDto carDto);

    @Mapping(target = "numberOfSeats", source = "seatCount")
    @Mapping(target = "free", source = "available")
    @Mapping(target = "driver", source = "myDriver")
    Car configSourceToTargetIgnoringSeats(CarDto carDto);

}

@Mapper(config = ConfigCompleteMapperConfig.class)
interface InheritConfigurationWithMapperConfigCompletion {

    @InheritConfiguration(name = "<caret>")
    void carDtoIntoCar(CarDto carDto, @MappingTarget Car car);

}
