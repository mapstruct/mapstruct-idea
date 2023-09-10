/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.example.dto.Car;
import org.example.dto.CarDto;
import org.mapstruct.*;

@Mapper
public interface InheritConfigurationBySuperMapperMapper extends SuperMapper, IrrelevantSuperMapper {

    @InheritConfiguration 
    void carDtoIntoCar(CarDto carDto, @MappingTarget Car car);

}

@Mapper
interface SuperMapper extends IrrelevantSuperSuperMapper {

    @Mapping(target = "numberOfSeats", ignore = true)
    @Mapping(target = "manufacturingDate", source = "manufacturingYear")
    @Mapping(target = "free", source = "available")
    @Mapping(target = "driver", source = "myDriver")
    Car carDtoToCarIgnoringSeatCount(CarDto car);

}


@Mapper
interface IrrelevantSuperMapper {

    default String upperCase(String input) {
        return input.toUpperCase();
    }

}

@Mapper
interface IrrelevantSuperSuperMapper {

    default int nullSafeInt(Long input) {
        if (input != null) {
            return input.intValue();
        }
        return 0;
    }

}
