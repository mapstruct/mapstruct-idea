/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.example.dto.Car;
import org.example.dto.CarDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InheritConfigurationByNameMapper {

    @Mapping(target = "numberOfSeats", ignore = true)
    @Mapping(target = "manufacturingDate", source = "manufacturingYear")
    @Mapping(target = "free", source = "available")
    // driver missing, must not be highlighted in inherited configuration
    Car carDtoToCarIgnoringSeatCount(CarDto car);

    @Mapping(target = "manufacturingDate", ignore = true)
    @Mapping(target = "free", source = "available")
    @Mapping(target = "driver", source = "myDriver")
    @Mapping(target = "numberOfSeats", source = "seatCount")
    Car carDtoToCarIgnoringManufacturingDate(CarDto car);

    @InheritConfiguration(name = "carDtoToCarIgnoringManufacturingDate")
    void carDtoIntoCar(CarDto carDto, @MappingTarget Car car);

}