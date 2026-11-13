/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

class Source {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Target {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
interface InheritConfigurationUnknownNameMapper {

    @Mapping(target = "name", source = "name")
    Target sourceToTarget(Source source);

    @InheritConfiguration(name = "<error descr="Unknown @InheritConfiguration method 'doesNotExist'">doesNotExist</error>")
    void sourceIntoTarget(Source source, @MappingTarget Target target);

    @InheritConfiguration(name =<error descr="Unknown @InheritConfiguration method ''">""</error>)
    void sourceIntoAnotherTarget(Source source, @MappingTarget Target target);

}
