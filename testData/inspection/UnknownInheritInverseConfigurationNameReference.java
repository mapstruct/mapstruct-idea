/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
interface InheritInverseConfigurationUnknownNameMapper {

    @Mapping(target = "name", source = "name")
    Source targetToSource(Target target);

    @InheritInverseConfiguration(name = "<error descr="Unknown @InheritInverseConfiguration method 'doesNotExist'">doesNotExist</error>")
    Target inverse(Source source);

    @InheritInverseConfiguration(name =<error descr="Unknown @InheritInverseConfiguration method ''">""</error>)
    Target inverseAnother(Source source);

}
