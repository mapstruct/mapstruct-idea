/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.example.dto.Car;
import org.example.dto.CarDto;
import org.mapstruct.*;

@Mapper(config = InheritMapperConfig.class)
public interface InheritConfigurationByMapperConfigMapper {

    @InheritConfiguration
    void carDtoIntoCar(CarDto carDto, @MappingTarget Car car);

}

@MapperConfig
interface InheritMapperConfig {

    @Mapping(target = "manufacturingDate", source = "manufacturingYear")
    @Mapping(target = "numberOfSeats", source = "seatCount")
    @Mapping(target = "free", source = "available")
    @Mapping(target = "driver", source = "myDriver")
    Car carDtoToCar(CarDto car);

}