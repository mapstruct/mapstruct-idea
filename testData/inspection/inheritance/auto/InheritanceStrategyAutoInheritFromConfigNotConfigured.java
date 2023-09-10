/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;

@MapperConfig
interface CarMapperConfig {

    @Mapping(target = "id", ignore = true)
    BaseEntity mapBase(Object source);

}

@Mapper(config = CarMapperConfig.class)
interface CarMapper {

    // warning: mappingInheritanceStrategy not configured
    CarEntity <warning descr="Unmapped target property: id">map</warning>(CarDto source);

}

class CarDto {

    public String name;

}

class BaseEntity {

    public int id;

}

class CarEntity extends BaseEntity {

    public String name;

}
