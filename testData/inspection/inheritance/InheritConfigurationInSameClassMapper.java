/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.example.dto.CarDto;
import org.example.dto.PersonDto;
import org.example.dto.Car;
import org.example.dto.Person;

@Mapper
public interface InheritConfigurationInSameClassMapper {

    @Mapping(target = "manufacturingDate", source = "manufacturingYear")
    @Mapping(target = "numberOfSeats", source = "seatCount")
    @Mapping(target = "free", source = "available")
    @Mapping(target = "driver", source = "myDriver")
    Car carDtoToCar(CarDto car);

    @InheritConfiguration  
    void carDtoIntoCar(CarDto carDto, @MappingTarget Car car);


}