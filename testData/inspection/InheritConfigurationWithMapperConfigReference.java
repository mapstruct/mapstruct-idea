/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

class SourceForConfig {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class TargetForConfig {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

@MapperConfig
interface InheritConfigurationWithMapperConfig {

    @Mapping(target = "name", source = "name")
    TargetForConfig configSourceToTarget(SourceForConfig source);

}

@Mapper(config = InheritConfigurationWithMapperConfig.class,
        unmappedTargetPolicy = ReportingPolicy.WARN)
interface MapperThatReferencesConfig {

    @InheritConfiguration(name = "configSourceToTarget")
    void sourceIntoTarget(SourceForConfig source, @MappingTarget TargetForConfig target);

    @InheritConfiguration(name = "<error descr="Unknown @InheritConfiguration method 'doesNotExist'">doesNotExist</error>")
    void sourceIntoAnotherTarget(SourceForConfig source, @MappingTarget TargetForConfig target);

}
