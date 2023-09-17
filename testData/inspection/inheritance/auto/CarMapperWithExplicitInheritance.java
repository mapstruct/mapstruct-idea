/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.MappingTarget;

@MapperConfig(
    mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG
)
interface AutoInheritedConfig {

    @Mapping(target = "primaryKey", source = "id")
    @Mapping(target = "auditTrail", ignore = true)
    BaseVehicleEntity baseDtoToEntity(BaseVehicleDto dto);
}

@Mapper(
    uses = NotToBeUsedMapper.class,
    config = AutoInheritedConfig.class,
    mappingInheritanceStrategy = MappingInheritanceStrategy.EXPLICIT
)
abstract class CarMapperWithExplicitInheritance {

    @InheritConfiguration(name = "baseDtoToEntity")
    @Mapping(target = "color", source = "colour")
    abstract CarEntity toCarEntity(CarDto carDto);

    @InheritInverseConfiguration(name = "toCarEntity")
    abstract CarDto toCarDto(CarEntity entity);

    @InheritConfiguration(name = "toCarEntity")
    @Mapping(target = "auditTrail", constant = "fixed")
    abstract CarEntity toCarEntityWithFixedAuditTrail(CarDto carDto);

    // this method should not be considered. See issue #1013
    void toCarEntity(CarDto carDto, @MappingTarget CarEntity carEntity) { }
}

abstract class BaseVehicleEntity {
    public long primaryKey;
    public String auditTrail;
}

abstract class BaseVehicleDto {
    public long id;
}

class CarDto extends BaseVehicleDto {
    public String colour;
}

class CarEntity extends BaseVehicleEntity {
    public String color;
}

@Mapper
interface NotToBeUsedMapper {

    @Mapping(target = "primaryKey", ignore = true)
    @Mapping(target = "auditTrail", ignore = true)
    @Mapping(target = "color", ignore = true)
    CarEntity toCarEntity(CarDto carDto);
}