/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;

@MapperConfig(
    mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG
)
interface CarMapperConfig {

    @Mapping(target = "primaryKey", source = "id")
    BaseEntity mapBase(BaseDto source);

}

@Mapper(config = CarMapperConfig.class)
interface CarMapper {

    // no warning: MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG is configured
    CarDto map(CarEntity source);

}

class BaseDto {

    public long id;

}

class CarDto extends BaseDto {

    public String name;

}

class BaseEntity {

    public long primaryKey;

}

class CarEntity extends BaseEntity {

    public String name;

}
